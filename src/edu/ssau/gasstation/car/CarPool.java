package edu.ssau.gasstation.car;

/**
 * Created by andrey on 04.12.16.
 */
public class CarPool {
    private int itter;
    //private Stream stream;
    private Car[] carPool;

    public CarPool(int size) {
        this.carPool = new Car[size];
        //todo инициализация потока
        //todo создание size автомобилей
    }

    public Car getCar(){
        if(itter == carPool.length - 1){
            //todo обнавить время появления автомобилей
        }
        if(itter >= carPool.length){
            itter = 0;
        }
        return this.carPool[itter];
    }

    private void updateIntervals(){
        //todo обновить интервалы времени в пуле автомобилей
    }
}
