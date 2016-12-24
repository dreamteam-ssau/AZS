package edu.ssau.gasstation.topology;
/**
 * Created by andrey on 04.12.16.
 */
public class Dispenser implements TopologyItem{
    private int fuelID;

    public Dispenser(){
        fuelID = -1;
    }

    public Dispenser(int fuelID) {
        this.fuelID = fuelID;
    }

    public int getFuelID() {
        return fuelID;
    }

    public void setFuelID(int fuelID) {
        this.fuelID = fuelID;
    }

    void fillTheCar(double volume){//Второй параметр автомобиль
        //todo вызовы методов кассы, автомобиля и резервуара
    }
}
