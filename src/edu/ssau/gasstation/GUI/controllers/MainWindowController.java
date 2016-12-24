package edu.ssau.gasstation.GUI.controllers;

import edu.ssau.gasstation.DB.DBHelper;
import edu.ssau.gasstation.GUI.components.ImageCell;
import edu.ssau.gasstation.GUI.model.FuelRecord;
import edu.ssau.gasstation.XMLHelper.XMLParser;
import edu.ssau.gasstation.XMLHelper.XMLWriter;
import edu.ssau.gasstation.topology.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.*;


/**
 * Created by andrey on 18.12.16.
 */


public class MainWindowController {
    ObservableList<String> topologyItem = FXCollections.observableArrayList("Въезд", "Выезд", "ТРК", "Резервуар", "Касса");
    @FXML
    private ListView<String> itemList;
    @FXML
    private GridPane constructorField;
    @FXML
    private Pane constructor;
    @FXML
    private Menu dbButton;
    @FXML
    private GridPane settings;
    @FXML
    private MenuItem create;
    @FXML
    private MenuItem load;
    @FXML
    private MenuItem save;
    @FXML MenuItem authors;
    @FXML
    private MenuItem close;
    @FXML MenuItem clear;
    @FXML MenuBar menuBar;
    private Topology topology;
    @FXML
    private MenuItem help;
    @FXML private Menu runButton;
    private int dispenserCount = 0;
    private int tankCount = 0;
    private boolean in = false;
    private boolean out = false;
    private boolean office = false;
    private Parent root = null;
    private ArrayList<ImageCell> icList = new ArrayList<>();
    private ArrayList<ListCell<String>> refItemList = new ArrayList<>();

    @FXML
    private void initialize() {
        try {
            root  = FXMLLoader.load(getClass().getResource("../view/DBWindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        constructor.setOnMouseClicked(event -> {
            removeEditing();
        });
        fillList();
        addMenuBarActions();
    }

    private String getImageByName(String name){
        String image = "images/";
        switch (name){
            case "Въезд":
                image += "in.png";
                break;
            case "Въезд-small":
                image += "in-small.png";
                break;
            case "Выезд":
                image += "out.png";
                break;
            case "Выезд-small":
                image += "out-small.png";
                break;
            case "ТРК":
                image += "dispenser.png";
                break;
            case "ТРК-small":
                image += "dispenser-small.png";
                break;
            case "Резервуар":
                image += "tank.png";
                break;
            case "Резервуар-small":
                image += "tank-small.png";
                break;
            case "Касса":
                image += "office.png";
                break;
            case "Касса-small":
                image += "office-small.png";
                break;
            default:
                throw new IllegalArgumentException();
        }
        return image;
    }

    private void fillList(){
        itemList.setCellFactory(listView -> new ListCell<String>(){
            private Image img;
            private ImageView pic;
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                    getStylesheets().add("edu/ssau/gasstation/GUI/view/style.css");
                    getStyleClass().add("list-cell");
                } else {
                    refItemList.add(this);
                    img = new Image(getClass().getResourceAsStream(getImageByName(name)));
                    pic = new ImageView(img);
                    pic.setFitWidth(100);
                    pic.setFitHeight(100);
                    setText(name);
                    setGraphic(pic);
                    setOnDragDetected(event -> {
                        if(!(((ListCell<String>)event.getSource()).getText().equals("Въезд") && in)
                                && !(((ListCell<String>)event.getSource()).getText().equals("Выезд") && out)
                                && !(((ListCell<String>)event.getSource()).getText().equals("Касса") && office)
                                && !(((ListCell<String>)event.getSource()).getText().equals("ТРК") && (dispenserCount > 4))
                                && !(((ListCell<String>)event.getSource()).getText().equals("Резервуар") && (tankCount  > 4))) {
                            Dragboard db = this.startDragAndDrop(TransferMode.ANY);
                            ClipboardContent cc = new ClipboardContent();
                            cc.putImage(new Image(getClass().getResourceAsStream(getImageByName(name + "-small"))));
                            db.setContent(cc);
                            event.consume();
                        }
                    });
                }
            }
        });
        itemList.setItems(topologyItem);
    }

    private void addMenuBarActions(){
        create.setOnAction(event -> {
            Stage primaryStage = new Stage();
            primaryStage.initOwner(menuBar.getScene().getWindow());
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            GridPane gp = new GridPane();
            gp.setPadding(new Insets(5, 5, 5, 5));
            gp.setVgap(10);
            gp.setHgap(10);
            HBox line1 = new HBox();
            VBox sep1 = new VBox();
            TextField width = new TextField("3");
            chekTopologySize(width, "\\d{0,2}");
            width.setStyle("-fx-pref-height: 32px");
            line1.getChildren().addAll(width);
            Button widthUp = new Button();
            widthUp.setPadding(new Insets(0, 0, 0, 0));
            widthUp.setText("▲");
            widthUp.setOnAction(event1 -> {
                int col;
                if((col = Integer.valueOf(width.getText())) < 10){
                    width.setText(String.valueOf(col + 1));
                }
            });
            Button widthDown = new Button();
            widthDown.setPadding(new Insets(0, 0, 0, 0));
            widthDown.setText("▼");
            widthDown.setOnAction(event1 -> {
                int col;
                if((col = Integer.valueOf(width.getText())) > 3){
                    width.setText(String.valueOf(col - 1));
                }
            });
            sep1.getChildren().addAll(widthUp, widthDown);
            line1.getChildren().add(sep1);
            gp.add(line1, 1, 1);
            gp.add(new Label("По горизонтали"), 0, 1);
            HBox line2 = new HBox();
            VBox sep2 = new VBox();
            TextField height = new TextField("3");
            chekTopologySize(height, "\\d{0,2}");
            height.setStyle("-fx-pref-height: 32px");
            line2.getChildren().addAll(height);
            Button heightUp = new Button();
            heightUp.setPadding(new Insets(0, 0, 0, 0));
            heightUp.setText("▲");
            heightUp.setOnAction(event1 -> {
                int row;
                if((row = Integer.valueOf(height.getText())) < 10){
                    height.setText(String.valueOf(row + 1));
                }
            });
            Button heightDown = new Button();
            heightDown.setPadding(new Insets(0, 0, 0, 0));
            heightDown.setText("▼");
            heightDown.setOnAction(event1 -> {
                int row;
                if((row = Integer.valueOf(height.getText())) > 3){
                    height.setText(String.valueOf(row - 1));
                }
            });
            sep2.getChildren().addAll(heightUp, heightDown);
            line2.getChildren().add(sep2);
            gp.add(line2, 1, 0);
            gp.add(new Label("По вертикали"), 0, 0);
            Button commit = new Button("OK");
            commit.setPrefSize(50, 20);
            commit.setOnAction(event1 -> {
                if(!width.getText().equals("") && !height.getText().equals("")){
                    constructor.getChildren().clear();
                    int iWidth = Integer.valueOf(width.getText());
                    int iHeight = Integer.valueOf(height.getText());
                    if(iHeight < 3 && iWidth < 3){
                        DBWindowController.showAlert("Высота и ширина не может быть меньше 3 клетов", "Невозможно создать топологию");
                    }
                    else if(iWidth < 3){
                        DBWindowController.showAlert("Ширина не может быть меньше 3 клетов", "Невозможно создать топологию");
                    }
                    else if(iHeight < 3){
                        DBWindowController.showAlert("Высота не может быть меньше 3 клетов", "Невозможно создать топологию");
                    }
                    else {
                        clearTopology();
                        topology = new Topology(iHeight, iWidth);
                        addConstructorField(iHeight, iWidth);
                        primaryStage.close();
                    }
                }
            });
            gp.add(commit, 1, 2);
            height.setFocusTraversable(true);
            primaryStage.setScene(new Scene(gp));
            primaryStage.showAndWait();
        });


        Label dbLabel = new Label("БД");
        dbLabel.setOnMouseClicked(event -> {
            Stage primaryStage = new Stage();
            primaryStage.initOwner(menuBar.getScene().getWindow());
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle("Работа с справочниками");
            primaryStage.setScene(new Scene(root));
            primaryStage.showAndWait();
        });
        dbButton.setGraphic(dbLabel);

        load.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Выберите файл");
            File tFile = fc.showOpenDialog(null);
            if(tFile != null) {
                try {
                    clearTopology();
                    topology = XMLParser.getTopologyFromFile(tFile.getName());
                    renderTopology();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    clearTopology();
                    DBWindowController.showAlert("Некорректная топология", "Невозможно открыть топологию");
                }
            }
        });

        save.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Сохранить топологию");
            File sFile = fc.showSaveDialog(null);
            if(sFile != null){
                String fName = sFile.getName();
                int t1 = fName.indexOf(".xml");
                if(fName.indexOf(".xml") > 0){
                    XMLWriter.write(topology, sFile.getName());
                }
                else {
                    XMLWriter.write(topology, sFile.getName() + ".xml");
                }
            }
        });

        close.setOnAction(event -> {
            Stage st = (Stage)menuBar.getScene().getWindow();
            st.close();
        });

        help.setAccelerator(new KeyCodeCombination(KeyCode.F1));
        help.setOnAction(event1 -> {
            Thread helpBrowser = new Thread(() -> {
                if(Desktop.isDesktopSupported())
                {
                    try {
                        File indexHTML = new File("res/web/index.html");
                        String test = indexHTML.getCanonicalPath();
                        URI test1 = URI.create("file://" + test);
                        Desktop.getDesktop().browse(test1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            helpBrowser.start();
        });

        Label runLabel = new Label("Запустить");
        runLabel.setOnMouseClicked(event -> {
            DBWindowController.showAlert("В разработке", "Не возможно открыть");
        });
        runButton.setGraphic(runLabel);

        authors.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Авторы");
            alert.setTitle("Об авторах");
            alert.setContentText("Курсовой проект по дисциплине \"Программная инженерия\".\nВыполнили студенты:\nРыков И.С." +
                    "\nРабкина А.А.\nГрязнов А.Г.\nЕпифанцев А.А.\nРуководитель: Зеленк Л.С.\n\nСамарский Университет  2016г");
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            alert.showAndWait();
        });

        clear.setOnAction(event -> {
            int width = topology.getWidth();
            int height = topology.getHeight();
            clearTopology();
            topology = new Topology(height, width);
            try {
                renderTopology();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
    }

    private void addConstructorField(int rowCount, int columnCount){
        icList.clear();
        constructorField = new GridPane();
        double gridWidth = 0;
        double gridHeight = 0;
        ContextMenu cm = new ContextMenu();
        for(int i = 0; i < columnCount; i++){
            ColumnConstraints column = new ColumnConstraints(50);
            constructorField.getColumnConstraints().add(column);
            gridWidth += 50;
        }
        for(int i = 0; i < rowCount; i++){
            RowConstraints row = new RowConstraints(50);
            constructorField.getRowConstraints().add(row);
            gridHeight += 50;
        }
        for(int i = 0; i < rowCount; i++){
            for(int j = 0; j < columnCount; j++){
                ImageCell ic = new ImageCell(i, j, 50, 50);
                ic.getCanvas().setOnDragEntered(event -> {
                    String sourceText = ((ListCell<String>)event.getGestureSource()).getText();
                    if(!sourceText.equals("Въезд") && !sourceText.equals("Выезд") &&
                            !(topology.getTopologyItem(ic.getRowNum() + 1, ic.getColumnNum()) instanceof Entry) &&
                            !(topology.getTopologyItem(ic.getRowNum() + 1, ic.getColumnNum()) instanceof Exit)) {
                        if (!ic.isSet() && ic.getRowNum() != rowCount - 1 ) {
                            ic.setTarget();
                        }
                    }
                    else {
                        if(ic.getRowNum() == rowCount - 1 && !ic.isSet()){
                            ic.setTarget();
                        }
                    }
                    event.consume();
                });
                ic.getCanvas().setOnDragExited(event -> {
                    if(!event.isDropCompleted() && !ic.isSet()) {
                        ic.clearTarget();
                    }
                    event.consume();
                });
                ic.getCanvas().setOnDragOver(event -> {
                    if(!((ListCell<String>)event.getGestureSource()).getText().equals("Въезд") &&
                            !((ListCell<String>)event.getGestureSource()).getText().equals("Выезд") &&
                            ic.getRowNum() != rowCount - 1 &&
                            !(topology.getTopologyItem(ic.getRowNum() + 1, ic.getColumnNum()) instanceof Entry) &&
                            !(topology.getTopologyItem(ic.getRowNum() + 1, ic.getColumnNum()) instanceof Exit)) {
                        if (event.getDragboard().hasImage() && !ic.isSet()) {
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        }
                    }
                    else {
                        if(ic.getRowNum() == rowCount - 1 && ((ListCell<String>)event.getGestureSource()).getText().equals("Въезд") && !in && !ic.isSet()){
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        }
                        else if(ic.getRowNum() == rowCount - 1 && ((ListCell<String>)event.getGestureSource()).getText().equals("Выезд") && !out && !ic.isSet()){
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        }
                    }
                    event.consume();
                });
                ic.getCanvas().setOnDragDropped((DragEvent event) -> {
                    boolean success = false;
                    Dragboard db = event.getDragboard();
                    if(db.hasImage()) {
                        ic.clearTarget();
                        Image img = db.getImage();
                        ic.setPicture(img);
                        success = true;
                        String type = ((ListCell<String>)event.getGestureSource()).getText();
                        TopologyItem item = null;
                        switch (type){
                            case "Въезд":
                                in = true;
                                ((ListCell<String>) event.getGestureSource()).setDisable(true);
                                item = new Entry();
                                break;
                            case "Выезд":
                                out = true;
                                ((ListCell<String>) event.getGestureSource()).setDisable(true);
                                item = new Exit();
                                break;
                            case "ТРК":
                                dispenserCount++;
                                if(dispenserCount == 5){
                                    ((ListCell<String>) event.getGestureSource()).setDisable(true);
                                }
                                item = new Dispenser();
                                break;
                            case "Касса":
                                office = true;
                                item = new Office();
                                ((ListCell<String>) event.getGestureSource()).setDisable(true);
                                break;
                            case "Резервуар":
                                tankCount++;
                                if(tankCount == 5){
                                    ((ListCell<String>) event.getGestureSource()).setDisable(true);
                                }
                                item = new Tank();
                                break;
                        }
                        itemList.getSelectionModel().clearSelection();
                        topology.setTopologyItem(item, ic.getRowNum(), ic.getColumnNum());
                    }
                    removeEditing();
                    event.setDropCompleted(success);
                    event.consume();
                });
                ic.getCanvas().setOnMouseClicked(event -> {
                    if(event.getButton() == MouseButton.SECONDARY && ic.isSet()){
                        ArrayList<MenuItem> menuList = new ArrayList<MenuItem>();
                        MenuItem delete = new MenuItem("Удалить");
                        delete.setOnAction(event1 -> {
                            ic.removeImage();
                            TopologyItem item = topology.getTopologyItem(ic.getRowNum(), ic.getColumnNum());
                            if(item instanceof Entry){
                                in = false;
                                refItemList.get(1).setDisable(false);
                            }
                            else if(item instanceof Exit) {
                                out = false;
                                refItemList.get(2).setDisable(false);
                            }
                            else if(item instanceof Office){
                                office = false;
                                refItemList.get(5).setDisable(false);
                            }
                            else if(item instanceof Dispenser){
                                dispenserCount--;
                                if(dispenserCount < 5){
                                    refItemList.get(3).setDisable(false);
                                }
                            }
                            else if(item instanceof Tank){
                                tankCount--;
                                if(tankCount < 5){
                                    ListCell<String> test = refItemList.get(3);
                                    refItemList.get(4).setDisable(false);
                                }
                            }
                            topology.setTopologyItem(null, ic.getRowNum(), ic.getColumnNum());
                        });
                        menuList.add(delete);
                        if(topology.getTopologyItem(ic.getRowNum(), ic.getColumnNum()) instanceof Dispenser
                                || topology.getTopologyItem(ic.getRowNum(), ic.getColumnNum()) instanceof Tank) {
                            MenuItem settings = new MenuItem("Настройки");
                            settings.setOnAction(event1 -> {
                                showSettings(ic);
                            });
                            menuList.add(0, settings);
                        }
                        cm.getItems().setAll(menuList);
                        if(!cm.isShowing()) {
                            cm.show(ic.getCanvas(), event.getScreenX(), event.getScreenY());
                        }
                    }
                    else {
                        System.out.println("FOR DEBUG");
                    }
                });
                icList.add(ic);
                constructorField.add(ic.getCanvas(), j, i);
            }
        }
        constructorField.setGridLinesVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        constructor.setPrefWidth(dim.getWidth() - itemList.getPrefWidth() - settings.getPrefWidth());
        constructor.setPrefHeight(dim.getHeight() - 50);
        constructorField.setLayoutX(constructor.getPrefWidth()/2 > gridWidth/2 ? constructor.getPrefWidth()/2- gridWidth/2 : 0);
        constructorField.setLayoutY(constructor.getPrefHeight()/2 > gridHeight/2 ? constructor.getPrefHeight()/2 - gridHeight/2 : 0);
        constructor.getChildren().add(constructorField);
        constructor.setPrefWidth(500);
    }

    private void removeEditing(){
        for(ImageCell ic: icList){
            if(ic.isEdit()){
                ic.removeEditing();
                settings.getChildren().clear();
            }
        }
    }

    private void showSettings(ImageCell ic){
        ic.setEditing();
        this.settings.setHgap(10);
        this.settings.setVgap(10);
        TopologyItem currentTopologyItem = topology.getTopologyItem(ic.getRowNum(), ic.getColumnNum());
        DBHelper dbh = new DBHelper();
        ChoiceBox<FuelRecord> curFuelType= new ChoiceBox<>();
        ObservableList<FuelRecord> fuelList = null;
        Button commit = new Button("OK");
        try {
            fuelList = dbh.getFuelList();
            curFuelType.setItems(fuelList);

            this.settings.add(curFuelType, 1, 0);
            this.settings.add(new Label("Тип топлива"), 0, 0);
            String text = "";
            String labelText = "";
            if(currentTopologyItem instanceof Dispenser){
                FuelRecord fr = null;
                for(FuelRecord forItem: fuelList){
                    if(forItem.getRecordId() == ((Dispenser)currentTopologyItem).getFuelID()) {
                        fr = forItem;
                    }
                }
                curFuelType.setValue(fr);
                text = "40 м/с";
                labelText = "Скорость заправки";
                commit.setOnAction(event2 -> {
                    int fuelID = (curFuelType.getValue() != null) ? curFuelType.getValue().getRecordId() : -1;
                    ((Dispenser) currentTopologyItem).setFuelID(fuelID);
                    this.settings.getChildren().clear();
                    ic.removeEditing();
                });
            }
            else if(currentTopologyItem instanceof Tank){
                FuelRecord fr = null;
                for(FuelRecord forItem: fuelList){
                    if(forItem.getRecordId() == ((Tank)currentTopologyItem).getFuelID()) {
                        fr = forItem;
                    }
                }
                curFuelType.setValue(fr);
                text = "20 куб.м";
                labelText = "Объем резервуара";
                commit.setOnAction(event2 -> {
                    int fuelID = (curFuelType.getValue() != null) ? curFuelType.getValue().getRecordId() : -1;
                    ((Tank) currentTopologyItem).setFuelID(fuelID);
                    this.settings.getChildren().clear();
                    ic.removeEditing();
                });
            }
            TextField speed = new TextField(text);
            speed.setPrefWidth(curFuelType.getWidth());
            speed.setDisable(true);
            this.settings.add(speed, 1, 1);
            this.settings.add(new Label(labelText), 0, 1);
            this.settings.add(commit, 1, 2);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void renderTopology() throws IOException {
        addConstructorField(topology.getHeight(), topology.getWidth());
        for(int i = 0; i < topology.getHeight(); i++){
            for(int j = 0; j < topology.getWidth(); j++){
                TopologyItem item;
                if((item = topology.getTopologyItem(i, j)) != null){
                    String pic = "";
                    if(item instanceof Entry){
                        if(!in) {
                            pic = "in-small.png";
                            in = true;
                        }
                        else throw new IOException("Недопустимая конфигурация топологии");
                    }
                    else if(item instanceof Exit){
                        if(!out) {
                            pic = "out-small.png";
                            out = true;
                        }
                        else throw new IOException("Недопустимая конфигурация топологии");
                    }
                    else if(item instanceof Dispenser){
                        pic = "dispenser-small.png";
                    }
                    else if(item instanceof Tank){
                        pic = "tank-small.png";
                    }
                    else if(item instanceof Office){
                        if(!office) {
                            pic = "office-small.png";
                            office = true;
                        }
                        else throw new IOException("Недопустимая конфигурация топологии");//todo написать свой эксепшен
                    }
                    icList.get(i*topology.getHeight() + j).setPicture(new Image(getClass().getResourceAsStream("images/" + pic)));
                }
            }
        }
    }

    private void clearTopology(){
        constructor.getChildren().clear();
        dispenserCount = 0;
        tankCount = 0;
        in = false;
        out = false;
        office = false;
        icList.clear();
        topology = null;
        for(ListCell<String> item: refItemList){
            if(item != null){
                item.setDisable(false);
            }
        }
    }

    private void chekTopologySize(TextField field, String mutcher){
        field.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty()) {
                    if (!newValue.matches(mutcher)) {
                        field.setText(oldValue);
                    } else {
                        try {
                            int check = Integer.valueOf(newValue);
                            if (check > 10) {
                                field.setText(oldValue);
                            } else {
                                field.setText(String.valueOf(check));
                            }
                        } catch (NumberFormatException e) {
                            field.setText(oldValue);
                        }
                    }
                }
            }
        });

    }
}
