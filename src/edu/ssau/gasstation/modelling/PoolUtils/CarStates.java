package edu.ssau.gasstation.modelling.PoolUtils;

public enum CarStates {
  Road, //car on the road
  MovingToDispenser, //car tries to find a path to dispenser
  GettingFuel, //car in one position, getting fuel
  Out //car tries to find a path to exit
}
