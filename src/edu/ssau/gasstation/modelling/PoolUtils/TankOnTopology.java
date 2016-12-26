package edu.ssau.gasstation.modelling.PoolUtils;

import edu.ssau.gasstation.topology.Tank;

public class TankOnTopology {
  Tank tank;
  int x , y;

  public TankOnTopology(Tank tank, int x, int y) {
    this.tank = tank;
    this.x = x;
    this.y = y;
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


}
