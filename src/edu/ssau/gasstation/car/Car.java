package edu.ssau.gasstation.car;

/**
 * Created by andrey on 04.12.16.
 */
public class Car {
    private double time;
    private int fuelID;
    private int picID;
    private double totalVolumOfTank;
    private double currentVolumeOfTank;

    public Car(double time, int fuelID, int picID, double totalVolumOfTank, double currentVolumeOfTank) {
        this.time = time;
        this.fuelID = fuelID;
        this.picID = picID;
        this.totalVolumOfTank = totalVolumOfTank;
        this.currentVolumeOfTank = currentVolumeOfTank;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getFuelID() {
        return fuelID;
    }

    public void setFuelID(int fuelID) {
        this.fuelID = fuelID;
    }

    public int getPicID() {
        return picID;
    }

    public void setPicID(int picID) {
        this.picID = picID;
    }

    public double getTotalVolumOfTank() {
        return totalVolumOfTank;
    }

    public void setTotalVolumOfTank(double totalVolumOfTank) {
        this.totalVolumOfTank = totalVolumOfTank;
    }

    public double getCurrentVolumeOfTank() {
        return currentVolumeOfTank;
    }

    public void setCurrentVolumeOfTank(double currentVolumeOfTank) {
        this.currentVolumeOfTank = currentVolumeOfTank;
    }

    public void fill(){
        currentVolumeOfTank = totalVolumOfTank;
    }

    public static Car createCar(){
        //todo возвращает объект автомобиль заполняя поля из бд (номер записи получается случайно)
        return null;
    }
}
