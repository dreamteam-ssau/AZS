package edu.ssau.gasstation.GUI.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by andrey on 17.12.16.
 */
public class FuelRecord implements Record {
    private final SimpleIntegerProperty recordId;
    private final SimpleStringProperty fuelName;
    private final SimpleDoubleProperty fuelCost;

    public FuelRecord(int recordId, String fuelName, double fuelCost) {
        this.recordId = new SimpleIntegerProperty(recordId);
        this.fuelName = new SimpleStringProperty(fuelName);
        this.fuelCost = new SimpleDoubleProperty(fuelCost);
    }

    public int getRecordId() {
        return recordId.get();
    }

    public SimpleIntegerProperty recordIdProperty() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId.set(recordId);
    }

    public String getFuelName() {
        return fuelName.get();
    }

    public SimpleStringProperty fuelNameProperty() {
        return fuelName;
    }

    public void setFuelName(String fuelName) {
        this.fuelName.set(fuelName);
    }

    public double getFuelCost() {
        return fuelCost.get();
    }

    public SimpleDoubleProperty fuelCostProperty() {
        return fuelCost;
    }

    public void setFuelCost(double fuelCost) {
        this.fuelCost.set(fuelCost);
    }

    @Override
    public String toString() {
        return this.fuelName.get();
    }
}
