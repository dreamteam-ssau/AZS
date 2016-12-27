package edu.ssau.gasstation.modelling.PoolUtils;

import java.util.*;

import edu.ssau.gasstation.GUI.controllers.ModelWindowController;
import edu.ssau.gasstation.car.*;
import edu.ssau.gasstation.topology.*;
import edu.ssau.gasstation.topology.Car;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

import javax.swing.text.html.HTMLDocument;

public class CarController extends Thread {
  private final static boolean DEBUG = true;
  private final static double SPEED = 40.0 / 20.0;
  private ArrayList<CarOnTopology> cars = new ArrayList<>();
  private ArrayList<CarOnTopology> pool = new ArrayList<>();
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
  private HashMap<Integer, ArrayList<TankOnTopology>> fuelTankList = new HashMap<>();
  private ModelWindowController controller;
  private HashMap<Refueller, ArrayList<Pair>> pathToTank = new HashMap<>();
  private HashMap<Refueller, Integer> countOnPlace = new HashMap<>();
  private HashMap<Refueller, ArrayList<Pair>> pathToExitForRefueller = new HashMap<>();
  private HashMap<Refueller, TankOnTopology> refuellerToTank = new HashMap<>();
  private ArrayList<Refueller> refuellers = new ArrayList<>();
  private HashSet<TankOnTopology> tanksUnderControl = new HashSet<>();
  private Office office = new Office();
  private CollectorOnTopology collector;
  private ArrayList<Pair> pathToOffice = new ArrayList<>();
  private ArrayList<Pair> pathFromOfficeToExit = new ArrayList<>();
  private int countInOffice = 0;
  private Pair officeOnTopology;
  private boolean usesOffice = false;


  public void setController(ModelWindowController controller) {
    this.controller = controller;
  }

  public CarController(Topology topology, ArrayList<edu.ssau.gasstation.car.Car> pool) {
    this.topology = topology;
    items = topology.getTopology();
    completeObjects();
    createTanks();
    setCarPool(pool);
    createPathFromEntry();
    //createPathToExit();
  }

  public void setCarPool(ArrayList<edu.ssau.gasstation.car.Car> pool){
    for(int i = 0; i < pool.size(); i++){
      this.pool.add(new CarOnTopology(pool.get(i), entry.x, entry.y, CarStates.MovingToDispenser));
    }
  }

  private void turnOffice() {
    collector = new CollectorOnTopology();
    collector.setX(entry.x);
    collector.setY(entry.y);
    findPathToOffice();
  }

  private void createPathFromEntry() {
    entryDijkstra = new Dijkstra(items, entry.x, entry.y);
    //entryDijkstra.dijkstra();
    int[][] dist = entryDijkstra.getDist();
    /*if (DEBUG) {
      System.out.println("from " + entry.x + " " + entry.y);
      System.out.println("");
      for (int i = 0; i < dispensers.size(); i++) {
        int x = dispensers.get(i).x;
        int y = dispensers.get(i).y;
        System.out.println("to " + x + " " + y + " dist: " + dist[x][y]);
        getPathTo(entry.x, entry.y, x, y);
      }
    }*/
    for (int i = 0; i < cars.size(); i++) {
      if (!carToDispenser.containsKey(cars.get(i))) {
        setCarToDispenser(cars.get(i));
      }
    }
  }

  private void checkTanks() {
    for (Map.Entry<Integer, ArrayList<TankOnTopology>> entry : fuelTankList.entrySet()) {
      for (TankOnTopology tank : entry.getValue()) {
        if (tank.tank.getCurentVolume() <= tank.tank.getTotalVolume() * 0.3) {
          if (!tanksUnderControl.contains(tank)) {
            Refueller refueller = new Refueller();
            findPathToTankForRefueller(tank, refueller);
            tanksUnderControl.add(tank);
            refuellerToTank.put(refueller, tank);
            refuellers.add(refueller);
          } else {
            if (tank.tank.getCurentVolume() > tank.tank.getTotalVolume() * 0.3) {
              tanksUnderControl.remove(tank);
            }
          }
        } else {
          if(tanksUnderControl.contains(tank)) {
            tanksUnderControl.remove(tank);
          }
        }
      }
    }
  }

  private void findPathToOffice() {
    entryDijkstra = new Dijkstra(items, entry.x, entry.y);
    entryDijkstra.dijkstra(officeOnTopology.x, officeOnTopology.y);
    pathToOffice = getPathTo(entry.x, entry.y, officeOnTopology.x, officeOnTopology.y);
  }

  private void findPathFromOfficeToExit() {
    exitDijkstra = new Dijkstra(items, exit.x, exit.y);
    exitDijkstra.dijkstra(collector.x, collector.y);
    pathFromOfficeToExit = getPathFrom(exit.x, exit.y, collector.x, collector.y);
  }

  private void findPathToTankForRefueller(TankOnTopology tank, Refueller refueller) {
    entryDijkstra.dijkstra(tank.getX(), tank.getY());
    ArrayList<Pair> path = getPathTo(entry.x, entry.y, tank.getX(), tank.getY());
    refueller.setX(entry.x);
    refueller.setY(entry.y);
    pathToTank.put(refueller, path);
  }

  private void findPathToExitForRefueller(Refueller refueller) {
    exitDijkstra = new Dijkstra(items, exit.x, exit.y);
    //exitDijkstra.dijkstra(exit.x, exit.y);
    exitDijkstra.dijkstra(refueller.getX(), refueller.getY());
    pathToExitForRefueller.put(refueller, getPathFrom(exit.x, exit.y, refueller.getX(), refueller.getY()));
  }

  private void fillCars() {
    pool.add(new CarOnTopology(new edu.ssau.gasstation.car.Car("1", 1, 3, 1, 20, 0), entry.x, entry.y, CarStates.MovingToDispenser));
    pool.add(new CarOnTopology(new edu.ssau.gasstation.car.Car("1", 20, 3, 1, 20, 0), entry.x, entry.y, CarStates.MovingToDispenser));
    pool.add(new CarOnTopology(new edu.ssau.gasstation.car.Car("1", 40, 3, 1, 10, 3), entry.x, entry.y, CarStates.MovingToDispenser));
    pool.add(new CarOnTopology(new edu.ssau.gasstation.car.Car("1", 70, 3, 1, 40, 0), entry.x, entry.y, CarStates.MovingToDispenser));
  }

  private void createTanks() {
    for (int i = 0; i < items.length; i++) {
      for (int j = 0; j < items[i].length; j++) {
        if (items[i][j] instanceof Tank) {
          Tank t = (Tank) items[i][j];

          ArrayList<TankOnTopology> tanks = null;
          if (fuelTankList.containsKey(t.getFuelID())) {
            tanks = fuelTankList.get(t.getFuelID());
          } else {
            tanks = new ArrayList<>();
          }
          TankOnTopology currT = new TankOnTopology(t, i, j);
          tanks.add(currT);
          fuelTankList.put(t.getFuelID(), tanks);
        }
      }
    }
  }

  private void createPathToExit() {
    exitDijkstra = new Dijkstra(items, exit.x, exit.y);

    for (int i = 0; i < cars.size(); i++) {
      if (carToDispenser.containsKey(cars.get(i)) && !pathToExit.containsKey(cars.get(i))) {
        if (cars.get(i).car.getCurrentVolumeOfTank() != cars.get(i).car.getTotalVolumOfTank())
          continue;
        exitDijkstra.dijkstra(cars.get(i).getX(), cars.get(i).getY());
        pathToExit.put(cars.get(i), getPathFrom(exit.x, exit.y, (cars.get(i)).x, (cars.get(i)).y));
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
      entryDijkstra.dijkstra(x, y);
      int[][] dist = entryDijkstra.getDist();
      if (dist[x][y] < min) {
        min = dist[x][y];
        index = i;
      }
    }
    pathToDispenser.put(car, getPathTo(car.getX(), car.getY(), canBeDispenser.get(index).x, canBeDispenser.get(index).y));
    countOnDispenser.put(canBeDispenser.get(index), countOnDispenser.getOrDefault(canBeDispenser.get(index), 0) + 1);
    carToDispenser.put(car, canBeDispenser.get(index));
  }

  private void moveCollector() {
    switch (collector.getState()) {
      case MovingToDispenser:
        int currentX = collector.getX();
        int currentY = collector.getY();
        for (int i = 0; i < pathToOffice.size(); i++) {
          Pair coord = pathToOffice.get(i);
          if (coord.x != currentX || coord.y != currentY)
            continue;
          Pair nextCoord = pathToOffice.get(i + 1);
          if (i == pathToOffice.size() - 2) {
            collector.setX(coord.x);
            collector.setY(coord.y);
            if (!(items[currentX][currentY] instanceof Exit) && !(items[currentX][currentY] instanceof Entry))
              items[currentX][currentY] = null;
            collector.setState(CarStates.GettingFuel);
            findPathFromOfficeToExit();
          }
          if (items[nextCoord.x][nextCoord.y] == null) {
            if (!(items[currentX][currentY] instanceof Exit) && !(items[currentX][currentY] instanceof Entry))
              items[currentX][currentY] = null;
            collector.setX(nextCoord.x);
            collector.setY(nextCoord.y);
            items[collector.getX()][collector.getY()] = new edu.ssau.gasstation.topology.Collector();// TODO: 18.12.2016 need to fix that
            if (i == pathToOffice.size() - 2) {
              collector.setState(CarStates.GettingFuel);
              findPathFromOfficeToExit();
            }
          }
        }
        break;

      case GettingFuel:
        countInOffice++;
        if (countInOffice >= 4) {
          collector.setState(CarStates.Out);
          countInOffice = 0;
        }
        break;

      case Out:
        currentX = collector.getX();
        currentY = collector.getY();
        for (int i = 0; i < pathFromOfficeToExit.size(); i++) {
          Pair coord = pathFromOfficeToExit.get(i);
          if (coord.x == currentX && coord.y == currentY) {
            Pair nextCoord = pathFromOfficeToExit.get(i + 1);
            if (items[nextCoord.x][nextCoord.y] == null || items[nextCoord.x][nextCoord.y] instanceof Exit) {
              if (i == pathFromOfficeToExit.size() - 2) {
                items[collector.getX()][collector.getY()] = null;
                usesOffice = false;
                office.setCashAmount(0);
                break;
              }
              collector.setX(nextCoord.x);
              collector.setY(nextCoord.y);
              if (items[currentX][currentY] instanceof edu.ssau.gasstation.topology.Collector) {
                items[currentX][currentY] = null;
              }
              items[collector.getX()][collector.getY()] = new edu.ssau.gasstation.topology.Collector();
            }
          }
        }
        break;
    }
  }

  private void moveRefueller(Refueller refueller) {
    switch (refueller.getState()) {
      case MovingToDispenser:
        int currentX = refueller.getX();
        int currentY = refueller.getY();
        ArrayList<Pair> path = pathToTank.get(refueller);
        for (int i = 0; i < path.size(); i++) {
          Pair coord = path.get(i);
          if (coord.x != currentX || coord.y != currentY)
            continue;
          Pair nextCoord = path.get(i + 1);
          if (i == path.size() - 2) {
            refueller.setX(coord.x);
            refueller.setY(coord.y);
            if (!(items[currentX][currentY] instanceof Exit) && !(items[currentX][currentY] instanceof Entry))
              items[currentX][currentY] = null;
            refueller.setState(CarStates.GettingFuel);
            findPathToExitForRefueller(refueller);
          }
          if (items[nextCoord.x][nextCoord.y] == null) {
            if (!(items[currentX][currentY] instanceof Exit) && !(items[currentX][currentY] instanceof Entry))
              items[currentX][currentY] = null;
            refueller.setX(nextCoord.x);
            refueller.setY(nextCoord.y);
            items[refueller.getX()][refueller.getY()] = new edu.ssau.gasstation.topology.Refueller();// TODO: 18.12.2016 need to fix that
            if (i == path.size() - 2) {
              refueller.setState(CarStates.GettingFuel);
              findPathToExitForRefueller(refueller);
            }
          }
        }
        break;

      case GettingFuel:
        TankOnTopology tank = refuellerToTank.get(refueller);
        tank.tank.setLocked(true);
        countOnPlace.put(refueller, countOnPlace.getOrDefault(refueller, 0)+1);
        if (countOnPlace.get(refueller) >= 5) {
          refueller.setState(CarStates.Out);
          tank.tank.setCurentVolume(tank.tank.getTotalVolume());
          tank.tank.setLocked(false);
        }
        break;

      case Out:
        currentX = refueller.getX();
        currentY = refueller.getY();
        path = pathToExitForRefueller.get(refueller);
        for (int i = 0; i < path.size(); i++) {
          Pair coord = path.get(i);
          if (coord.x == currentX && coord.y == currentY) {
            Pair nextCoord = path.get(i + 1);
            if (items[nextCoord.x][nextCoord.y] == null || items[nextCoord.x][nextCoord.y] instanceof Exit) {
              if (i == path.size() - 2) {
                refuellers.remove(refueller);
                items[refueller.getX()][refueller.getY()] = null;
                break;
              }
              refueller.setX(nextCoord.x);
              refueller.setY(nextCoord.y);
              if (items[currentX][currentY] instanceof edu.ssau.gasstation.topology.Refueller) {
                items[currentX][currentY] = null;
              }
              items[refueller.getX()][refueller.getY()] = new edu.ssau.gasstation.topology.Refueller();

            }
          }
        }

        break;
    }
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
              if (!(items[currentX][currentY] instanceof Exit) && !(items[currentX][currentY] instanceof Entry))
                items[currentX][currentY] = null;
              car.setState(CarStates.GettingFuel);
            }
            if (items[nextCoord.x][nextCoord.y] == null) {
              if (!(items[currentX][currentY] instanceof Exit) && !(items[currentX][currentY] instanceof Entry))
                items[currentX][currentY] = null;
              car.setX(nextCoord.x);
              car.setY(nextCoord.y);
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
        if (currentCar.getCurrentVolumeOfTank() + SPEED > currentCar.getTotalVolumOfTank()) {
          double needToFillFull = currentCar.getTotalVolumOfTank() - currentCar.getCurrentVolumeOfTank();
          currentCar.setCurrentVolumeOfTank(currentCar.getTotalVolumOfTank());
          int fuelId = car.car.getFuelID();
          ArrayList<TankOnTopology> tanksWithNeedFuel = fuelTankList.get(fuelId);
          int indexMax = -1;
          double max = Double.MIN_VALUE;
          int currentIndex = 0;
          for (TankOnTopology tank : tanksWithNeedFuel) {
            if (tank.tank.getCurentVolume() > max && !tank.tank.isLocked()) {
              indexMax = currentIndex;
              max = tank.tank.getCurentVolume();
            }
            currentIndex++;
          }
          if (indexMax == -1)
            break;
          double prevVolume = tanksWithNeedFuel.get(indexMax).tank.getCurentVolume();
          tanksWithNeedFuel.get(indexMax).tank.setCurentVolume(prevVolume - needToFillFull);
          office.payForFuel(229, needToFillFull);

          createPathToExit();

          car.setState(CarStates.Out);
        } else {
          currentCar.setCurrentVolumeOfTank(currentCar.getCurrentVolumeOfTank() + SPEED);
          int fuelId = car.car.getFuelID();
          ArrayList<TankOnTopology> tanksWithNeedFuel = fuelTankList.get(fuelId);
          int indexMax = -1;
          double max = Double.MIN_VALUE;
          int currentIndex = 0;
          for (TankOnTopology tank : tanksWithNeedFuel) {
            if (tank.tank.getCurentVolume() > max && !tank.tank.isLocked()) {
              indexMax = currentIndex;
              max = tank.tank.getCurentVolume();
            }
            currentIndex++;
          }
          if (indexMax == -1)
            break;
          double prevVolume = tanksWithNeedFuel.get(indexMax).tank.getCurentVolume();
          tanksWithNeedFuel.get(indexMax).tank.setCurentVolume(prevVolume - SPEED);
          office.payForFuel(229, SPEED);
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
            if (items[nextCoord.x][nextCoord.y] == null || items[nextCoord.x][nextCoord.y] instanceof Exit) {
              if (i == path.size() - 2) {
                cars.remove(car);
                items[car.getX()][car.getY()] = null;
                break;
              }
              car.setX(nextCoord.x);
              car.setY(nextCoord.y);
              if (items[currentX][currentY] instanceof Car) {
                items[currentX][currentY] = null;
              }
              items[car.getX()][car.getY()] = new Car();

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
        } else if(items[i][j] instanceof Office) {
          officeOnTopology = new Pair(i, j);
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
    path.remove(path.size() - 1);

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
    int time = 0;
    while (true) {
      if(office.isFull()) {
        if(!usesOffice) {
          turnOffice();
          usesOffice = true;
        }
        moveCollector();
        checkTanks();
        for (int i = 0; i < refuellers.size(); i++) {
          moveRefueller(refuellers.get(i));
        }
      } else {
        for (int i = 0; i < pool.size(); i++) {
          if (pool.get(i).car.getTime() <= .0 + time) {
            cars.add(pool.get(i));
            pool.remove(i);
            createPathFromEntry();
            //createPathToExit();
          }
        }
        checkTanks();
        for (int i = 0; i < refuellers.size(); i++) {
          moveRefueller(refuellers.get(i));
        }
        for (int i = 0; i < cars.size(); i++) {
          moveCar(cars.get(i));
        }
      }
      updateImage();
      try {
        Thread.sleep(400);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      time++;
    }
  }

  private void updateImage() {
    Image[][] images = new Image[items.length][items[0].length];
    Tooltip[][] tooltipsForImages = new Tooltip[items.length][items[0].length];
    for (int i = 0; i < items.length; i++) {
      for (int j = 0; j < items[i].length; j++) {
        if (items[i][j] != null) {
          Image img = SelectImageUtil.getImageByItemType(items[i][j], topology);
          images[i][j] = img;
          if ((!(items[i][j] instanceof Tank) && !(items[i][j] instanceof Entry) && !(items[i][j] instanceof Office) && !(items[i][j] instanceof Exit)
                  && !(items[i][j] instanceof Dispenser) && !(items[i][j] instanceof edu.ssau.gasstation.topology.Refueller) && !(items[i][j] instanceof Collector)) || (items[i][j] instanceof Tank)){
            if (items[i][j] instanceof Tank){
              tooltipsForImages[i][j] = new Tooltip(getStringForTankTooltip(i,j));
            } else {
              tooltipsForImages[i][j] = new Tooltip(getStringForCarTooltip(i, j));
            }
          }
        }
      }
    }
    Platform.runLater(() -> controller.addModelFieldBlack(items.length, items[0].length, images, tooltipsForImages));
  }
  private void updateTooltip(Image[][] images) {


    /*
    final Tooltip t = new Tooltip("some text");
    ic.getCanvas().setOnMouseEntered(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent event) {
        Point2D p = ic.getCanvas().localToScreen(ic.getCanvas().getLayoutBounds().getMaxX(), ic.getCanvas().getLayoutBounds().getMaxY()); //I position the tooltip at bottom right of the node (see below for explanation)
        t.show(ic.getCanvas(), p.getX(), p.getY());
      }
    });
    ic.getCanvas().setOnMouseExited(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent event) {
        t.hide();
      }
    });*/
  }
  private String getStringForCarTooltip(int i, int j){
    String res = "";
    for (CarOnTopology carOnTopology : cars){
      if (carOnTopology.getX() == i && carOnTopology.getY() == j){
        res = carOnTopology.car.toString();
      }
    }
    return res;
  }

  private String getStringForTankTooltip(int i, int j){
    String res = "";
    for (Map.Entry<Integer, ArrayList<TankOnTopology>> entry : fuelTankList.entrySet()) {
      for (TankOnTopology tank : entry.getValue()) {
        if (tank.getX() == i && tank.getY() == j) {
          res = tank.tank.toString();
        }
      }
    }
    return res;
  }

  private TopologyItem getItemByCoordinates(Pair p) {
    return items[p.x][p.y];
  }
}
