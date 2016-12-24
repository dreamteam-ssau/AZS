package edu.ssau.gasstation.GUI.components;

import edu.ssau.gasstation.DB.DBHelper;
import edu.ssau.gasstation.GUI.controllers.DBWindowController;
import edu.ssau.gasstation.GUI.model.CarRecord;
import edu.ssau.gasstation.GUI.model.FuelRecord;
import edu.ssau.gasstation.GUI.model.Record;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;

/**
 * Created by andrey on 12.12.16.
 */
public class DeletButtonCell extends TableCell<Record, Boolean> {
    private final Button cellButton = new Button();
    private ObservableList data;

    public DeletButtonCell(ObservableList data){
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
            ImageView pic = new ImageView(new Image(getClass().getResourceAsStream("delete.png")));
            pic.setFitHeight(20);
            pic.setFitWidth(20);
            cellButton.paddingProperty().setValue(new Insets(2, 2, 2, 2));
            cellButton.graphicProperty().setValue(pic);
            cellButton.setPrefSize(17, 17);
            cellButton.setOnAction( ( ActionEvent event ) -> {
                Record current = DeletButtonCell.this.getTableView().getItems().get(DeletButtonCell.this.getIndex());
                DBHelper dbh = new DBHelper();
                try {
                    if(current instanceof CarRecord) {
                        dbh.deleteCar(current.getRecordId());
                        data.remove(current);
                    }
                    else if(current instanceof FuelRecord){
                        dbh.deleteFuel(current.getRecordId());
                        data.remove(current);
                    }
                } catch (SQLException e) {
                    DBWindowController.showAlert("Данный тип топлива используется в таблице \"Автомобиль\"", "Невозможно удатлить тип топлива");
                }
            } );
            cellButton.setPrefSize(30.0, 30.0);
            setGraphic( cellButton );
            setText( null );
        }
    }
}
