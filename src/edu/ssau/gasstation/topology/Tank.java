package edu.ssau.gasstation.topology;

/**
 * Created by andrey on 04.12.16.
 */
public class Tank implements TopologyItem{
    private int fuelID;
    private double totalVolume;
    private double curentVolume;
    private double criticalVolume;

    public Tank(){
        fuelID = -1;
    }

    public Tank(int fuelID) {
        this.fuelID = fuelID;
        this.totalVolume = 20.0;
        this.curentVolume = 20.0;
    }

    public int getFuelID() {
        return fuelID;
    }

    public void setFuelID(int fuelID) {
        this.fuelID = fuelID;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public double getCurentVolume() {
        return curentVolume;
    }

    public void setCurentVolume(double curentVolume) {
        this.curentVolume = curentVolume;
    }

    public void setCriticalVolume(double criticalVolume){
        if(criticalVolume >= 0.3 && criticalVolume < 1.0) {
            this.criticalVolume = criticalVolume;
        }
    }

    public void getFuel(double volume){
        if(curentVolume > volume){
            curentVolume -= volume;
            if(curentVolume < criticalVolume*totalVolume){
                callRefueller();
            }
        }
    }

    private void callRefueller(){
        //todo информировать о вызови дозаправщика (либо отрисовка приезда дозаправщика)
    }
}
