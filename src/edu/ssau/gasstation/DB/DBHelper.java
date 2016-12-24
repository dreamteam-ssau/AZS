package edu.ssau.gasstation.DB;

import edu.ssau.gasstation.GUI.model.CarRecord;
import edu.ssau.gasstation.GUI.model.FuelRecord;
import edu.ssau.gasstation.car.Car;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.Properties;

/**
 * Created by andrey on 06.12.16.
 */
public class DBHelper {
    private static Connection connect;
    private static Statement st;

    public DBHelper() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", "root");
            properties.setProperty("password", "2");
            properties.setProperty("useUnicode","true");
            properties.setProperty("characterEncoding","UTF-8");
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/gasstation", properties);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<CarRecord> getCarList() throws SQLException {
        String query = "SELECT * FROM car;";
        ObservableList<CarRecord> result = FXCollections.observableArrayList();
        ResultSet rs = connect.prepareStatement(query).executeQuery();
        while (rs.next()){
            String carName = rs.getString("car_name");
            double carTankVolume = rs.getDouble("car_tank_volume");
            int carFuelID = rs.getInt("fuel_id");
            int carID = rs.getInt("car_id");
            result.add(new CarRecord(carID, carName, getFuelName(carFuelID), carTankVolume));
        }
        return result;
    }

    public ObservableList<FuelRecord> getFuelList() throws SQLException {
        String query = "SELECT * FROM fuel;";
        ResultSet rs = connect.prepareStatement(query).executeQuery();
        ObservableList<FuelRecord> fuelList = FXCollections.observableArrayList();
        while(rs.next()) {
            String name = rs.getString("fuel_name");
            double fuelCost = rs.getDouble("fuel_cost");
            int recordID = rs.getInt("fuel_id");
            fuelList.add(new FuelRecord(recordID, name, fuelCost));
        }
        return fuelList;
    }

    public int getMaxID(String table) throws SQLException {
        String query = "SELECT max(" + table + "_id) FROM " + table + ";";
        ResultSet rs = connect.prepareStatement(query).executeQuery();
        rs.next();
        return rs.getInt("max(" + table + "_id)");
    }

    public void insertFuel(FuelRecord fr) throws SQLException {
        String fuel_name = fr.getFuelName();
        double fuel_cost = fr.getFuelCost();
        String query = "INSERT INTO fuel(fuel_name, fuel_cost) VALUES(?, ?);";
        PreparedStatement ps = connect.prepareStatement(query);
        ps.setString(1, new String(fuel_name.getBytes(), Charset.forName("utf8")));
        ps.setDouble(2, fuel_cost);
        ps.execute();
    }

    public void updateFuel(String fuel_name, double fuel_cost, int fuel_id) throws SQLException {
        String query = "UPDATE fuel set fuel_name=?, fuel_cost=? where fuel_id=?";
        PreparedStatement ps = connect.prepareStatement(query);
        ps.setString(1, new String(fuel_name.getBytes(), Charset.forName("utf8")));
        ps.setDouble(2, fuel_cost);
        ps.setInt(3, fuel_id);
        ps.execute();
    }

    public void deleteFuel(int fuelID) throws SQLException {
        String query = "DELETE FROM fuel WHERE(fuel_id = ?);";
        PreparedStatement ps = connect.prepareStatement(query);
        ps.setInt(1, fuelID);
        ps.execute();
    }

    public void insertCar(CarRecord cr) throws SQLException {
        String car_name = cr.getCarType();
        double car_tank_volume = cr.getTankVolume();
        int fuel_id = getFuelID(cr.getFuelType());
        String query = "INSERT INTO car(car_name, car_tank_volume, fuel_id) VALUES(?, ?, ?);";
        PreparedStatement ps = connect.prepareStatement(query);
        ps.setString(1, new String(car_name.getBytes(), Charset.forName("utf8")));
        ps.setDouble(2, car_tank_volume);
        ps.setInt(3, fuel_id);
        ps.execute();
    }

    public void updateCar(String car_name, double car_tank_volume, int fuel_id, int car_id) throws SQLException {
        String query = "UPDATE car set car_name=?, car_tank_volume=?, fuel_id=? where car_id=?";
        PreparedStatement ps = connect.prepareStatement(query);
        ps.setString(1, new String(car_name.getBytes(), Charset.forName("utf8")));
        ps.setDouble(2, car_tank_volume);
        ps.setInt(3, fuel_id);
        ps.setInt(4, car_id);
        ps.execute();
        System.out.println("Update car with car_id=" + car_id);
    }
    public void deleteCar(int carID) throws SQLException {
        String query = "DELETE FROM car WHERE(car_id = ?);";
        PreparedStatement ps = connect.prepareStatement(query);
        ps.setInt(1, carID);
        ps.execute();
    }

    public int getFuelID(String fuel_name) throws SQLException {
        String query = "SELECT fuel_id FROM fuel WHERE fuel_name=?";
        PreparedStatement ps = connect.prepareStatement(query);
        ps.setString(1, fuel_name);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("fuel_id");
    }

    public String getFuelName(int fuel_id) throws SQLException {
        String query = "SELECT fuel_name FROM fuel WHERE fuel_id=?";
        PreparedStatement ps = connect.prepareStatement(query);
        ps.setInt(1, fuel_id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("fuel_name");
    }
}
