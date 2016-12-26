package edu.ssau.gasstation.GUI.controllers;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import edu.ssau.gasstation.GUI.components.ImageCell;
import edu.ssau.gasstation.modelling.PoolUtils.CarController;
import edu.ssau.gasstation.modelling.PoolUtils.Pair;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

/**
 * Created by Sasha on 20.12.2016.
 */
public class ModelWindowController implements Initializable {
  @FXML
  private Button slowerBtn;
  @FXML
  private Button pauseBtn;
  @FXML
  private Button fasterBtn;
  @FXML
  private TableView<String> parametersList;
  @FXML
  private VBox charts;
  @FXML
  private Pane model;
  @FXML
  private GridPane modelField;

  //    private Topology topology = new Topology(6, 6);

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initToolBar();
    addModelField(6, 6);
  }

  private void initToolBar() {
    Image slowerImage = new Image(getClass().getResourceAsStream("../view/icons/slower.png"));
    Image pauseImage = new Image(getClass().getResourceAsStream("../view/icons/pause.png"));
    Image fasterImage = new Image(getClass().getResourceAsStream("../view/icons/faster.png"));

    ImageView pic = new ImageView(slowerImage);
    pic.setFitHeight(25);
    pic.setFitWidth(25);
    slowerBtn.graphicProperty().setValue(pic);
    slowerBtn.setPrefSize(20, 20);

    pic = new ImageView(pauseImage);
    pic.setFitHeight(25);
    pic.setFitWidth(25);
    pauseBtn.graphicProperty().setValue(pic);
    pauseBtn.setPrefSize(20, 20);

    pic = new ImageView(fasterImage);
    pic.setFitHeight(25);
    pic.setFitWidth(25);
    fasterBtn.setGraphic(pic);
    fasterBtn.setPrefSize(20, 20);
  }


  private void addModelField(int rowCount, int columnCount) {
    modelField = new GridPane();

    double gridWidth = 0;
    double gridHeight = 0;
    for (int i = 0; i < columnCount; i++) {
      ColumnConstraints column = new ColumnConstraints(50);
      modelField.getColumnConstraints().add(column);
      gridWidth += 50;
    }
    for (int i = 0; i < rowCount; i++) {
      RowConstraints row = new RowConstraints(50);
      modelField.getRowConstraints().add(row);
      gridHeight += 50;
    }

    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++) {
        ImageCell ic = new ImageCell(i, j, 50, 50);
        Boolean isSet = false;
        modelField.add(ic.getCanvas(), j, i);
      }
    }
    modelField.setGridLinesVisible(true);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    model.setPrefWidth(dim.getWidth() - parametersList.getPrefWidth() - charts.getPrefWidth());
    model.setPrefHeight(dim.getHeight() - 50);
    modelField.setLayoutX(model.getPrefWidth() / 2 > gridWidth / 2 ? model.getPrefWidth() / 2 - gridWidth / 2 : 0);
    modelField.setLayoutY(model.getPrefHeight() / 2 > gridHeight / 2 ? model.getPrefHeight() / 2 - gridHeight / 2 : 0);
    model.getChildren().add(modelField);
    model.setPrefWidth(500);

  }

  public void addModelFieldBlack(int rowCount, int columnCount, Image[][] images, Tooltip[][] tooltips) {
    model.getChildren().clear();
    modelField = new GridPane();

    double gridWidth = 0;
    double gridHeight = 0;
    for (int i = 0; i < columnCount; i++) {
      ColumnConstraints column = new ColumnConstraints(50);
      modelField.getColumnConstraints().add(column);
      gridWidth += 50;
    }
    for (int i = 0; i < rowCount; i++) {
      RowConstraints row = new RowConstraints(50);
      modelField.getRowConstraints().add(row);
      gridHeight += 50;
    }

    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++) {
        if(images[i][j]!=null) {
          ImageCell ic = new ImageCell(i, j, 50, 50);
          ic.setPicture(images[i][j]);
          Boolean isSet = false;
          Tooltip tooltip = new Tooltip("some text");
          if (tooltips[i][j]!=null){
            tooltip = tooltips[i][j];
          }
          final Tooltip t = tooltip;
          ic.getCanvas().setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
              if(!t.getText().equals("some text")){
                Point2D p = ic.getCanvas().localToScreen(ic.getCanvas().getLayoutBounds().getMaxX(), ic.getCanvas().getLayoutBounds().getMaxY()); //I position the tooltip at bottom right of the node (see below for explanation)
                t.show(ic.getCanvas(), p.getX(), p.getY());
              }
            }
          });
          ic.getCanvas().setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
              if (!t.getText().equals("some text")){
                t.hide();
              }
            }
          });
          modelField.add(ic.getCanvas(), j, i);
        } else {
          ImageCell ic = new ImageCell(i, j, 50, 50);
          ic.removeImage();
          modelField.add(ic.getCanvas(), j, i);

        }
      }
    }
    modelField.setGridLinesVisible(true);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    model.setPrefWidth(dim.getWidth() - parametersList.getPrefWidth() - charts.getPrefWidth());
    model.setPrefHeight(dim.getHeight() - 50);
    modelField.setLayoutX(model.getPrefWidth() / 2 > gridWidth / 2 ? model.getPrefWidth() / 2 - gridWidth / 2 : 0);
    modelField.setLayoutY(model.getPrefHeight() / 2 > gridHeight / 2 ? model.getPrefHeight() / 2 - gridHeight / 2 : 0);
    model.getChildren().add(modelField);
    model.setPrefWidth(500);

  }

  public static void updateCell(ImageCell cell, Image image) {
    if (cell.isSet()) {
      cell.removeImage();
      cell.setPicture(image);
    }
  }
}
