package edu.ssau.gasstation.car;

import edu.ssau.gasstation.DB.DBHelper;
import edu.ssau.gasstation.GUI.model.CarRecord;
import edu.ssau.gasstation.modelling.Flow;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by andrey on 04.12.16.
 */
public class CarPool {
    private int itter;
    //private Stream stream;
    private ArrayList<Car> cars;
    private Flow flow;

    public CarPool(Flow flow) {
        this.cars  = new ArrayList<>();
        this.flow = flow;
    }

    public ArrayList<Car> getCarPool(int size) throws SQLException {
        createPool(size);
        return this.cars;
    }

    private void createPool(int size) throws SQLException {
        DBHelper dbh = new DBHelper();
        ObservableList<CarRecord> car = dbh.getCarList();
        List<Double> time = flow.getValuesSequence(size);
        double currentTime = 0;
        Random rnd = new Random();
        for(int i = 0; i < size; i++){
            currentTime += time.get(i);
            CarRecord curr = car.get(rnd.nextInt(car.size()));
            this.cars.add(new Car(curr.getCarType(), currentTime, dbh.getFuelID(curr.getFuelType()), 1, curr.getTankVolume(), curr.getTankVolume()*0.3));
        }
    }

    public void updateIntervals(){
        double currentTime = cars.get(cars.size()).getTime();
        List<Double> time = flow.getValuesSequence(cars.size());
        int index = 0;
        for(Car item: cars){
            currentTime += time.get(index);
            item.setTime(currentTime);
            index++;
        }
    }
}
