package edu.ssau.gasstation.modelling.PoolUtils;

public class CollectorOnTopology {
  int x, y;
  CarStates state = CarStates.MovingToDispenser;

  public int getX() {
    return x;
  }

  public CarStates getState() {
    return state;
  }

  public void setState(CarStates state) {
    this.state = state;
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
}
