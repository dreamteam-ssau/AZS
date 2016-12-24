package edu.ssau.gasstation.GUI.components;

import edu.ssau.gasstation.DB.DBHelper;
import edu.ssau.gasstation.GUI.controllers.DBWindowController;
import edu.ssau.gasstation.GUI.model.CarRecord;
import edu.ssau.gasstation.GUI.model.FuelRecord;
import edu.ssau.gasstation.GUI.model.Record;
import edu.ssau.gasstation.car.Car;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by andrey on 05.12.16.
 */
public class EditButtonCell extends TableCell<Record, Boolean>{
    private final Button cellButton = new Button();
    private ObservableList data;

    public EditButtonCell(ObservableList data){
        this.data = data;
    }

    @Override
    public void updateItem(Boolean item, boolean empty ) {
        super.updateItem(item, empty);
        if ( empty ) {
            setGraphic( null );
            setText( null );
        }
        else {
            ImageView pic = new ImageView(new Image(getClass().getResourceAsStream("edit.png")));
            pic.setFitHeight(20);
            pic.setFitWidth(20);
            cellButton.paddingProperty().setValue(new Insets(2, 2, 2, 2));
            cellButton.graphicProperty().setValue(pic);
            cellButton.setPrefSize(17, 17);
            cellButton.setOnAction( ( ActionEvent event ) -> {
                Record current = EditButtonCell.this.getTableView().getItems().get(EditButtonCell.this.getIndex());
                DBHelper dbh = new DBHelper();
                Stage primaryStage = new Stage();
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(10);
                grid.setVgap(10);
                primaryStage.setTitle("Изменение записи");
                try {
                    if(current instanceof CarRecord) {
                        TextField newCarName = new TextField(((CarRecord)current).getCarType());
                        TextField newTankVolume = new TextField(String.valueOf(((CarRecord)current).getTankVolume()));
                        DBWindowController.addTextChangeListener(newTankVolume, "(\\d{1,2})(\\.\\d{0,1})?");
                        grid.add(newCarName, 1, 0);
                        grid.add(new Label("Марка автомобиля"), 0, 0);
                        grid.add(newTankVolume, 1, 1);
                        grid.add(new Label("Объем бака"), 0, 1);
                        grid.add(new Label("Тип топлива"), 0, 2);
                        ObservableList<FuelRecord> fuelList = new DBHelper().getFuelList();
                        ChoiceBox<FuelRecord> newFuelType = new ChoiceBox<>();
                        newFuelType.setItems(fuelList);
                        FuelRecord fr = null;
                        for(FuelRecord forItem: fuelList){
                            if(forItem.getFuelName().equals(((CarRecord)current).getFuelType())){
                                fr = forItem;
                            }
                        }
                        newFuelType.setValue(fr);
                        grid.add(newFuelType, 1, 2);
                        Button commit = new Button("OK");
                        commit.setOnAction(event1 -> {
                            if(!newCarName.getText().equals("") && !newFuelType.getValue().equals(null) && !newTankVolume.getText().equals("")) {
                                String uCarName = newCarName.getText();
                                double uTankVolume = Double.valueOf(newTankVolume.getText());
                                int uFuelID = newFuelType.getValue().getRecordId();
                                try {
                                    dbh.updateCar(uCarName, uTankVolume, uFuelID, current.getRecordId());
                                    ((CarRecord) current).setFuelType(newFuelType.getValue().getFuelName());
                                    ((CarRecord) current).setCarType(uCarName);
                                    ((CarRecord) current).setTankVolume(uTankVolume);
                                    primaryStage.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            else DBWindowController.showAlert("Необъожимо заполнить все поля", "Ошибка");
                        });
                        grid.add(commit, 1, 3);
                        primaryStage.setScene(new Scene(grid, 400, 200));
                        primaryStage.show();
                    }
                    else if(current instanceof FuelRecord){
                       TextField newFuelName = new TextField(((FuelRecord)current).getFuelName());
                       TextField newFuelCost = new TextField(String.valueOf(((FuelRecord)current).getFuelCost()));
                       DBWindowController.addTextChangeListener(newFuelCost, "(\\d{1,2})(\\.\\d{0,2})?");
                       grid.add(newFuelName, 1, 0);
                       grid.add(newFuelCost, 1, 1);
                       grid.add(new Label("Тип топлива"), 0, 0);
                       grid.add(new Label("Цена"), 0, 1);
                       Button commit = new Button("OK");
                       commit.setOnAction(event12 -> {
                           if(!newFuelCost.getText().equals("") && !newFuelCost.getText().equals("")) {
                               String uFuelName = newFuelName.getText();
                               double uFuelCost = Double.valueOf(newFuelCost.getText());
                               try {
                                   dbh.updateFuel(uFuelName, uFuelCost, current.getRecordId());
                                   ((FuelRecord) current).setFuelCost(uFuelCost);
                                   ((FuelRecord) current).setFuelName(uFuelName);
                                   primaryStage.close();
                               } catch (SQLException e) {
                                   DBWindowController.showAlert("Дублирование типа топлива", "Ошибка");
                               }
                           }
                           else DBWindowController.showAlert("Необходимо заполнить все поля", "Ошибка");
                       });
                       grid.add(commit, 1, 2);
                       primaryStage.setScene(new Scene(grid, 400, 150));
                       primaryStage.show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } );
            cellButton.setPrefSize(30.0, 30.0);
            setGraphic( cellButton );
            setText( null );
        }
    }
}
