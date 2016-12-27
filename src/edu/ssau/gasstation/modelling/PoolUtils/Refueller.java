package edu.ssau.gasstation.modelling.PoolUtils;

import edu.ssau.gasstation.topology.TopologyItem;

public class Refueller implements TopologyItem{
  private int x, y;
  private CarStates state = CarStates.MovingToDispenser;

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
