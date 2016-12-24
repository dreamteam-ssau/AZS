package edu.ssau.gasstation.GUI.controllers;

import edu.ssau.gasstation.DB.DBHelper;
import edu.ssau.gasstation.GUI.components.DeletButtonCell;
import edu.ssau.gasstation.GUI.components.EditButtonCell;

import edu.ssau.gasstation.GUI.model.CarRecord;
import edu.ssau.gasstation.GUI.model.FuelRecord;
import edu.ssau.gasstation.GUI.model.Record;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by EAA on 04.12.16 очень стремный контроллер.
 */
public class DBWindowController {
    //Table CAR
    @FXML
    private TableView<CarRecord> car;
    @FXML
    private TableColumn<CarRecord, Double> tankVolume;
    @FXML
    private TableColumn<CarRecord, String> carType;
    @FXML
    private TableColumn<CarRecord, String> carFuelType;
    @FXML
    private TableColumn<Record, Boolean> editCar;
    @FXML
    private TableColumn<Record, Boolean> deleteCar;
    @FXML
    private Button addCar;
    @FXML
    private TextField addCarName;
    @FXML
    private TextField addTankVolume;
    @FXML
    private ChoiceBox<FuelRecord> addCarFuelType;
    private ObservableList<CarRecord> carList = FXCollections.observableArrayList();
    //Table FUEL
    @FXML
    private TableView<FuelRecord> fuel;
    @FXML
    private TableColumn<FuelRecord, String> fuelType;
    @FXML
    private TableColumn<FuelRecord, Double> fuelCost;
    @FXML
    private TableColumn<Record, Boolean> editFuel;
    @FXML
    private TableColumn<Record, Boolean> deleteFuel;
    @FXML
    private TextField addFuelName;
    @FXML
    private TextField addFuelCost;
    @FXML
    private Button addFuel;
    private ObservableList<FuelRecord> fuelList = FXCollections.observableArrayList();

    private DBHelper dbh;

    @FXML
    private void initialize() {
        dbh = new DBHelper();
        try {
            int a = dbh.getMaxID("car");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initCarList();
        initFuelList();
        initButtons();
        initTextFields();
        addCarFuelType.setItems(fuelList);
    }

    private void initButtons(){
        ImageView pic = new ImageView(new Image(getClass().getResourceAsStream("images/add.png")));
        pic.setFitHeight(23);
        pic.setFitWidth(23);
        addCar.paddingProperty().setValue(new Insets(2, 2, 2, 2));
        addCar.graphicProperty().setValue(pic);
        addCar.setPrefSize(16, 16);
        pic = new ImageView(new Image(getClass().getResourceAsStream("images/add.png")));
        pic.setFitHeight(23);
        pic.setFitWidth(23);
        addFuel.paddingProperty().setValue(new Insets(2, 2, 2, 2));
        addFuel.graphicProperty().setValue(pic);
        addFuel.setPrefSize(16, 16);
    }

    private void initCarList(){
        try {
            carList = dbh.getCarList();
            tankVolume.setCellValueFactory(new PropertyValueFactory<>("tankVolume"));
            tankVolume.setEditable(true);
            carType.setCellValueFactory(new PropertyValueFactory<>("carType"));
            carFuelType.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
            editCar.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
            editCar.setCellFactory(param -> new EditButtonCell(carList));
            deleteCar.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
            deleteCar.setCellFactory(param -> new DeletButtonCell(carList));
            car.setItems(carList);
            addCar.setOnAction(event -> {
                try {
                    if(!addCarName.getText().equals("") && !addTankVolume.getText().equals("") && addCarFuelType.getValue() != null) {
                        int id = dbh.getMaxID("car");
                        CarRecord cr = new CarRecord(id + 1, addCarName.getText(), addCarFuelType.getValue().getFuelName(), Double.valueOf(addTankVolume.getText()));
                        if(CarRecord.indexOf(carList, cr) < 0) {
                            dbh.insertCar(cr);
                            carList.add(cr);
                            addCarName.clear();
                            addTankVolume.clear();
                            addCarFuelType.setValue(null);
                        }
                        else {
                            showAlert("Дублироваиние машины", "Ошибка");
                        }
                    }
                    else{
                        showAlert("Необходимо заполнить все поля.", "Ошибка");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            car.edit(0, carType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initFuelList(){
        try {
            fuelList = dbh.getFuelList();
            fuelType.setCellValueFactory(new PropertyValueFactory<>("fuelName"));
            fuelCost.setCellValueFactory(new PropertyValueFactory<>("fuelCost"));
            fuel.setItems(fuelList);
            editFuel.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
            editFuel.setCellFactory(param -> new EditButtonCell(fuelList));
            deleteFuel.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue() != null));
            deleteFuel.setCellFactory(param -> new DeletButtonCell(fuelList));
            car.setItems(carList);
            addFuel.setOnAction(event -> {
                try {
                    if(!addFuelName.getText().equals("") && !addFuelCost.getText().equals("")) {
                        int id = dbh.getMaxID("fuel");
                        FuelRecord fr = new FuelRecord(id + 1, addFuelName.getText().toUpperCase(), Double.valueOf(addFuelCost.getText()));
                        dbh.insertFuel(fr);
                        fuelList.add(fr);
                        addFuelCost.clear();
                        addFuelName.clear();
                    }
                    else{
                        showAlert("Необходимо заполнить все поля.", "Ошибка");
                    }
                } catch (SQLException e) {
                    showAlert("Дублирование типа топлива", "Ошибка");
                }
            });
        } catch (SQLException e) {
            showAlert("Дублирование типов топлива", "Ошибка");
        }
    }

    private void initTextFields(){
        addTextChangeListener(addFuelCost, "(\\d{1,2})(\\.\\d{0,2})?");
        addTextChangeListener(addTankVolume, "(\\d{1,2})(\\.\\d{0,1})?");
    }

   public static void showAlert(String text, String header){
       Alert alert = new Alert(Alert.AlertType.ERROR);
       alert.setTitle("Невозможно выполнить операцию");
       alert.setHeaderText(header);
       alert.setContentText(text);
       alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
       alert.showAndWait();
   }

   public static void addTextChangeListener(TextField field, String matcher){
       field.textProperty().addListener((observable, oldValue, newValue) -> {
           if (!newValue.isEmpty()) {
               if (!newValue.matches(matcher)) {
                   field.setText(oldValue);
               } else {
                   try {
                       field.setText(newValue);
                   } catch (NumberFormatException e) {
                       field.setText(oldValue);
                   }
               }
           }
       });
   }
}

