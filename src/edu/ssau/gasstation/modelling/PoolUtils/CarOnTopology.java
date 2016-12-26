package edu.ssau.gasstation.modelling.PoolUtils;

import edu.ssau.gasstation.car.Car;

public class CarOnTopology {
  Car car;
  int x, y; //current coordinates on topology
  CarStates state;

  public CarOnTopology(Car car, int x, int y, CarStates state) {
    this.car = car;
    this.x = x;
    this.y = y;
    this.state = state;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public CarStates getState() {
    return state;
  }

  public void setState(CarStates state) {
    this.state = state;
  }
}
