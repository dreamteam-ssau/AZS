package edu.ssau.gasstation.modelling.PoolUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import edu.ssau.gasstation.topology.Car;
import edu.ssau.gasstation.topology.Dispenser;
import edu.ssau.gasstation.topology.Entry;
import edu.ssau.gasstation.topology.Exit;
import edu.ssau.gasstation.topology.Tank;
import edu.ssau.gasstation.topology.Topology;
import edu.ssau.gasstation.topology.TopologyItem;

public class CarController extends Thread {
  private final static boolean DEBUG = true;
  private final static double SPEED = 40 / 60;
  private ArrayList<CarOnTopology> cars = new ArrayList<>();
  private HashMap<CarOnTopology, Pair> carToDispenser = new HashMap<>();
  private Topology topology;
  private TopologyItem[][] items;
  private ArrayList<Pair> dispensers = new ArrayList<>();
  private ArrayList<Pair> tanks = new ArrayList<>();
  private HashMap<CarOnTopology, ArrayList<Pair>> pathToDispenser = new HashMap<>();
  private HashMap<CarOnTopology, ArrayList<Pair>> pathToExit = new HashMap<>();
  private Pair exit;
  private Pair entry;
  private HashMap<Pair, Integer> countOnDispenser = new HashMap<>();
  private Dijkstra entryDijkstra;
  private Dijkstra exitDijkstra;
  private HashMap<Integer, Tank> fuelTanks = new HashMap<>();

  public CarController(Topology topology) {
    this.topology = topology;
    items = topology.getTopology();
    completeObjects();
    createTanks();
    fillCars();
    createPathFromEntry();
    createPathToExit();
  }

  public void createPathFromEntry() {
    entryDijkstra = new Dijkstra(items, entry.x, entry.y);
    //entryDijkstra.dijkstra();
    int[][] dist = entryDijkstra.getDist();
    if (DEBUG) {
      System.out.println("from " + entry.x + " " + entry.y);
      System.out.println("");
      for (int i = 0; i < dispensers.size(); i++) {
        int x = dispensers.get(i).x;
        int y = dispensers.get(i).y;
        System.out.println("to " + x + " " + y + " dist: " + dist[x][y]);
        getPathTo(entry.x, entry.y, x, y);
      }
    }
    for (int i = 0; i < cars.size(); i++) {
      if (!carToDispenser.containsKey(cars.get(i))) {
        setCarToDispenser(cars.get(i));
      }
    }
  }

  private void fillCars() {
    cars.add(new CarOnTopology(new edu.ssau.gasstation.car.Car(1, 229, 1,10, 0), entry.x, entry.y, CarStates.MovingToDispenser));
  }

  private void createTanks() {
    for (int i = 0; i < items.length; i++) {
      for (int j = 0; j < items[i].length; j++) {
        if (items[i][j] instanceof Tank) {
          Tank t = (Tank) items[i][j];
          fuelTanks.put(t.getFuelID(), t);
        }
      }
    }
  }

  private void createPathToExit() {
    exitDijkstra = new Dijkstra(items, exit.x, exit.y);
    //exitDijkstra.dijkstra();

    for (int i = 0; i < cars.size(); i++) {
      if (carToDispenser.containsKey(cars.get(i)) && !pathToExit.containsKey(cars.get(i))) {
        pathToExit.put(cars.get(i), getPathFrom(exit.x, exit.y, carToDispenser.get(cars.get(i)).x, carToDispenser.get(cars.get(i)).y));
      }
    }
  }


  private void setCarToDispenser(CarOnTopology car) {
    ArrayList<Pair> canBeDispenser = new ArrayList<>();

    for (int i = 0; i < dispensers.size(); i++) {
      if (car.car.getFuelID() == ((Dispenser) getItemByCoordinates(dispensers.get(i))).getFuelID()) {
        canBeDispenser.add(dispensers.get(i));
      }
    }

    int[][] dist = entryDijkstra.getDist();
    int min = Integer.MAX_VALUE;
    int countCars = Integer.MAX_VALUE;
    int index = 0;
    for (int i = 0; i < canBeDispenser.size(); i++) {
      if (countOnDispenser.getOrDefault(canBeDispenser.get(i), 0) < countCars) {
        countCars = countOnDispenser.getOrDefault(canBeDispenser.get(i), 0);
      }
    }

    ArrayList<Pair> withBestCountOfCars = new ArrayList<>();
    for (int i = 0; i < canBeDispenser.size(); i++) {
      if (countOnDispenser.getOrDefault(canBeDispenser.get(i), 0) == countCars) {
        withBestCountOfCars.add(canBeDispenser.get(i));
      }
    }

    canBeDispenser.clear();
    canBeDispenser.addAll(withBestCountOfCars);

    for (int i = 0; i < canBeDispenser.size(); i++) {
      int x = canBeDispenser.get(i).x;
      int y = canBeDispenser.get(i).y;
      if (dist[x][y] < min) {
        min = dist[x][y];
        index = i;
      }
    }
    pathToDispenser.put(car, getPathTo(car.getX(), car.getY(), canBeDispenser.get(index).x, canBeDispenser.get(index).y));
    countOnDispenser.put(canBeDispenser.get(index), countOnDispenser.getOrDefault(canBeDispenser.get(index), 0) + 1);
    carToDispenser.put(car, canBeDispenser.get(index));
  }

  private void moveCar(CarOnTopology car) {
    switch (car.getState()) {
      case MovingToDispenser:
        int currentX = car.x;
        int currentY = car.y;
        ArrayList<Pair> path = pathToDispenser.get(car);
        for (int i = 0; i < path.size(); i++) {
          Pair coord = path.get(i);
          if (coord.x == currentX && coord.y == currentY) {
            Pair nextCoord = path.get(i + 1);
            if (i == path.size() - 2) {
              car.setX(nextCoord.x);
              car.setY(nextCoord.y);
              items[currentX][currentY] = null;
              car.setState(CarStates.GettingFuel);
            }
            if (items[nextCoord.x][nextCoord.y] == null) {
              car.setX(nextCoord.x);
              car.setY(nextCoord.y);
              items[currentX][currentY] = null;
              items[car.getX()][car.getY()] = new Car();// TODO: 18.12.2016 need to fix that
              if (i == path.size() - 2) {
                car.setState(CarStates.GettingFuel);
              }
            }
          }
        }
        break;
      case GettingFuel:
        edu.ssau.gasstation.car.Car currentCar = car.car;
        if (currentCar.getCurrentVolumeOfTank() + SPEED < currentCar.getTotalVolumOfTank()) {
          currentCar.setCurrentVolumeOfTank(currentCar.getTotalVolumOfTank());
          fuelTanks.get(((Dispenser) getItemByCoordinates(carToDispenser.get(car))).getFuelID()).setCurentVolume(fuelTanks.get(((Dispenser) getItemByCoordinates(carToDispenser.get(car))).getFuelID()).getCurentVolume() - (currentCar.getTotalVolumOfTank() - currentCar.getCurrentVolumeOfTank()));
          car.setState(CarStates.Out);
        } else {
          currentCar.setCurrentVolumeOfTank(currentCar.getCurrentVolumeOfTank() + SPEED);
          fuelTanks.get(((Dispenser) getItemByCoordinates(carToDispenser.get(car))).getFuelID()).setCurentVolume(fuelTanks.get(((Dispenser) getItemByCoordinates(carToDispenser.get(car))).getFuelID()).getCurentVolume() - SPEED);
        }

        break;

      case Out:
        currentX = car.x;
        currentY = car.y;
        path = pathToExit.get(car);
        for (int i = 0; i < path.size(); i++) {
          Pair coord = path.get(i);
          if (coord.x == currentX && coord.y == currentY) {
            Pair nextCoord = path.get(i + 1);
            if (items[nextCoord.x][nextCoord.y] == null) {
              car.setX(nextCoord.x);
              car.setY(nextCoord.y);
              items[currentX][currentY] = null;
              items[car.getX()][car.getY()] = new Car();// TODO: 18.12.2016 need to fix that
              if (i == path.size() - 2) {
                cars.remove(car);
                items[car.getX()][car.getY()] = null;
              }
            }
          }
        }

        break;
    }
  }

  private void completeObjects() {
    for (int i = 0; i < items.length; i++) {
      for (int j = 0; j < items[i].length; j++) {
        if (items[i][j] instanceof Exit) {
          exit = new Pair(i, j);
        } else if (items[i][j] instanceof Entry) {
          entry = new Pair(i, j);
        } else if (items[i][j] instanceof Tank) {
          tanks.add(new Pair(i, j));
        } else if (items[i][j] instanceof Dispenser) {
          dispensers.add(new Pair(i, j));
        }
      }
    }
  }

  private ArrayList<Pair> getPathTo(int xFrom, int yFrom, int xTo, int yTo) {
    Dijkstra.Cell[][] cells = entryDijkstra.getPrev();
    ArrayList<Pair> path = new ArrayList<>();

    while (xFrom != xTo || yFrom != yTo) {
      path.add(new Pair(xTo, yTo));
      int xTemp = xTo;
      xTo = cells[xTo][yTo].x;
      yTo = cells[xTemp][yTo].y;
    }

    path.add(new Pair(xFrom, yFrom));
    Collections.reverse(path);
    if (DEBUG) {
      System.out.println();
      System.out.println(Arrays.toString(path.toArray()));
      System.out.println();
    }
    return path;
  }

  private ArrayList<Pair> getPathFrom(int xFrom, int yFrom, int xTo, int yTo) {
    Dijkstra.Cell[][] cells = exitDijkstra.getPrev();
    ArrayList<Pair> path = new ArrayList<>();

    while (xFrom != xTo || yFrom != yTo) {
      path.add(new Pair(xTo, yTo));
      int xTemp = xTo;
      xTo = cells[xTo][yTo].x;
      yTo = cells[xTemp][yTo].y;
    }

    path.add(new Pair(xFrom, yFrom));
    if (DEBUG) {
      System.out.println();
      System.out.println(Arrays.toString(path.toArray()));
      System.out.println();
    }
    return path;
  }

  @Override
  public void run() {
    while(true) {
      for (int i = 0; i < cars.size(); i++) {
        moveCar(cars.get(i));
      }
    }
  }

  private TopologyItem getItemByCoordinates(Pair p) {
    return items[p.x][p.y];
  }
}
