package edu.ssau.gasstation.GUI.controllers;

import edu.ssau.gasstation.DB.DBHelper;
import edu.ssau.gasstation.GUI.components.ImageCell;
import edu.ssau.gasstation.GUI.model.FuelRecord;
import edu.ssau.gasstation.XMLHelper.XMLParser;
import edu.ssau.gasstation.XMLHelper.XMLWriter;
import edu.ssau.gasstation.car.*;
import edu.ssau.gasstation.modelling.*;
import edu.ssau.gasstation.topology.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private ArrayList<Integer> dispenserFuel = new ArrayList<>();
    private ArrayList<Integer> tankFuel = new ArrayList<>();

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
            chekTopologySize(width, "\\d{0,2}", 10);
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
            chekTopologySize(height, "\\d{0,2}", 10);
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
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Топология", "*.xml"));
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
            int exI = -1;
            int exJ = -1;
            dispenserFuel.clear();
            tankFuel.clear();
            try {
                for (int i = 0; i < topology.getHeight(); i++) {
                    for (int j = 0; j < topology.getWidth(); j++) {
                        if(topology.getTopologyItem(i, j) instanceof Tank){
                            int id;
                            if ((id = ((Tank) topology.getTopologyItem(i, j)).getFuelID()) != -1) {
                                tankFuel.add(id);
                            } else {
                                exI = i;
                                exJ = j;
                                throw new IllegalArgumentException("Необходимо указать тип топлива резервуара");
                            }
                        }
                        else if (topology.getTopologyItem(i, j) instanceof Dispenser) {
                            int id;
                            if ((id = ((Dispenser) topology.getTopologyItem(i, j)).getFuelID()) != -1) {
                                if(tankFuel.contains(id)) {
                                    dispenserFuel.add(id);
                                }
                                else{
                                    exI = i;
                                    exJ = j;
                                    throw new IllegalArgumentException("На топологии не существует резервуара с данным типом топлива");
                                }
                            } else {
                                exI = i;
                                exJ = j;
                                throw new IllegalArgumentException("Необходимо указать тип топлива ТРК");
                            }
                        }
                    }
                }

                if (office) {
                    if (in) {
                        if (out) {
                            if (tankCount > 0) {
                                if (dispenserCount > 0) {
                                    Stage preRunStage = new Stage();
                                    Pane determPane = new Pane();
                                    GridPane randomPane = new GridPane();
                                    determPane.setStyle("-fx-border-color: black; -fx-padding: 10px");
                                    determPane.setPrefSize(690, 120);
                                    randomPane.setStyle("-fx-border-color: black; -fx-padding: 10px");
                                    randomPane.setPrefSize(690, 120);
                                    GridPane linTime = new GridPane();
                                    linTime.add(new Label("Период генерации автомобилей"), 0, 0);
                                    TextField time = new TextField();
                                    checkTextField(time, "(\\d{1,2})(\\.\\d{0,3})?", -1);
                                    linTime.add(time, 1, 0);
                                    linTime.setPadding(new Insets(10));
                                    linTime.setHgap(10);
                                    determPane.getChildren().add(linTime);
                                    GridPane zr = new GridPane();
                                    GridPane normal = new GridPane();
                                    normal.getStylesheets().add("edu/ssau/gasstation/GUI/view/style.css");
                                    normal.getStyleClass().add("myGrid");
                                    Label lDisp = new Label("σ\u00B2");
                                    TextField tDisp = new TextField();
                                    checkTextField(tDisp, "(\\d{1,2})(\\.\\d{0,3})?", -1);
                                    normal.add(lDisp, 0, 0);
                                    normal.add(tDisp, 1, 0);
                                    normal.add(new Label("μ"), 0, 1);
                                    TextField mo = new TextField();
                                    checkTextField(mo, "(\\d{1,2})(\\.\\d{0,3})?", -1);
                                    normal.add(mo, 1, 1);
                                    GridPane exp = new GridPane();
                                    exp.getStylesheets().add("edu/ssau/gasstation/GUI/view/style.css");
                                    exp.getStyleClass().add("myGrid");
                                    exp.add(new Label("λ"), 0, 0);
                                    TextField intens = new TextField();
                                    checkTextField(intens, "(\\d{1,2})(\\.\\d{0,3})?", -1);
                                    exp.add(intens, 1, 0);
                                    exp.setDisable(true);
                                    GridPane ravn = new GridPane();
                                    ravn.getStylesheets().add("edu/ssau/gasstation/GUI/view/style.css");
                                    ravn.getStyleClass().add("myGrid");
                                    ravn.add(new Label("a"), 0, 0);
                                    ravn.add(new Label("b"), 0, 1);
                                    TextField a = new TextField();
                                    TextField b = new TextField();
                                    checkTextField(a, "(\\d{1,2})(\\.\\d{0,3})?", -1);
                                    checkTextField(b, "(\\d{1,2})(\\.\\d{0,3})?", -1);
                                    ravn.setDisable(true);
                                    ravn.add(a, 1, 0);
                                    ravn.add(b, 1, 1);
                                    zr.add(ravn, 2, 1);
                                    zr.add(exp, 1, 1);
                                    zr.add(normal, 0, 1);
                                    ToggleGroup tZR = new ToggleGroup();
                                    RadioButton rNormal = new RadioButton("Нормальный ЗР");
                                    rNormal.setOnAction(event1 -> {
                                        normal.setDisable(false);
                                        exp.setDisable(true);
                                        ravn.setDisable(true);
                                    });
                                    rNormal.setToggleGroup(tZR);
                                    rNormal.setSelected(true);
                                    RadioButton rExp = new RadioButton("Экспоненциальный ЗР");
                                    rExp.setOnAction(event1 -> {
                                        exp.setDisable(false);
                                        normal.setDisable(true);
                                        ravn.setDisable(true);
                                    });
                                    rExp.setToggleGroup(tZR);
                                    RadioButton rRavn = new RadioButton("Равномерный ЗР");
                                    rRavn.setOnAction(event1 -> {
                                        ravn.setDisable(false);
                                        exp.setDisable(true);
                                        normal.setDisable(true);
                                    });
                                    rRavn.setToggleGroup(tZR);
                                    zr.add(rNormal, 0, 0);
                                    zr.add(rExp, 1, 0);
                                    zr.add(rRavn, 2, 0);
                                    zr.setHgap(20);
                                    zr.setVgap(3);
                                    randomPane.add(zr, 0, 0);
                                    VBox mainBox = new VBox();
                                    mainBox.setPadding(new Insets(10));
                                    HBox hb = new HBox();
                                    Label type = new Label("Тип потока: ");
                                    type.setPadding(new Insets(5, 0, 5, 5));
                                    hb.getChildren().add(type);
                                    ToggleGroup group = new ToggleGroup();
                                    RadioButton determ = new RadioButton("Детерменированный");
                                    determ.setOnAction(event1 -> mainBox.getChildren().set(1, determPane));
                                    RadioButton random = new RadioButton("Случайный");
                                    random.setOnAction(event1 -> mainBox.getChildren().set(1, randomPane));
                                    determ.setToggleGroup(group);
                                    random.setToggleGroup(group);
                                    determ.setPadding(new Insets(5, 0, 5, 10));
                                    random.setPadding(new Insets(5, 0, 5, 10));
                                    determ.setSelected(true);
                                    hb.getChildren().addAll(determ, random);
                                    GridPane fuelAndOffice = new GridPane();
                                    fuelAndOffice.add(new Label("Минимальный уровень топлива в резервуаре"), 0, 0);
                                    fuelAndOffice.add(new Label("Лимит кассы"), 0, 1);
                                    fuelAndOffice.setHgap(10);
                                    fuelAndOffice.setVgap(10);
                                    HBox fuelLine = new HBox();
                                    VBox fuelButtons = new VBox();
                                    TextField minFuel = new TextField("30");
                                    chekTopologySize(minFuel, "\\d{0,3}", 95);
                                    minFuel.setStyle("-fx-pref-height: 32px");
                                    fuelLine.getChildren().addAll(minFuel);
                                    Button minFuelUp = new Button();
                                    minFuelUp.setPadding(new Insets(0, 0, 0, 0));
                                    minFuelUp.setText("▲");
                                    minFuelUp.setOnAction(event1 -> {
                                        int row;
                                        if ((row = Integer.valueOf(minFuel.getText())) < 95) {
                                            minFuel.setText(String.valueOf(row + 5));
                                        }
                                    });
                                    Button minFuelDown = new Button();
                                    minFuelDown.setPadding(new Insets(0, 0, 0, 0));
                                    minFuelDown.setText("▼");
                                    minFuelDown.setOnAction(event1 -> {
                                        int row;
                                        if ((row = Integer.valueOf(minFuel.getText())) > 30) {
                                            minFuel.setText(String.valueOf(row - 5));
                                        }
                                    });
                                    fuelButtons.getChildren().addAll(minFuelUp, minFuelDown);
                                    fuelLine.getChildren().add(fuelButtons);
                                    fuelAndOffice.add(fuelLine, 1, 0);
                                    fuelAndOffice.add(new Label("%"), 2, 0);
                                    fuelAndOffice.setPadding(new Insets(5));
                                    TextField money = new TextField("700 тыс.руб");
                                    money.setDisable(true);
                                    fuelAndOffice.add(money, 1, 1);
                                    Button ok = new Button("Ok");
                                    ok.setOnAction(event1 -> {
                                        Flow flow = null;
                                        if (determ.isSelected()) {
                                            if (!time.getText().equals("")) {
                                                flow = new DeterministicFlow(Double.valueOf(time.getText()));
                                            } else
                                                DBWindowController.showAlert("Необходимо указать период генерации автомобилей", "Необходимо заполнить все поля");
                                        } else {
                                            if (rNormal.isSelected()) {
                                                if (!tDisp.getText().equals("")) {
                                                    if (!mo.getText().equals("")) {
                                                        flow = new RandomFlow(new NormalDistribution(Double.valueOf(mo.getText()), Double.valueOf(tDisp.getText())));
                                                    } else
                                                        DBWindowController.showAlert("Необходимо указать математическое ожидание", "Необходимо заполнить все поля");
                                                } else {
                                                    if (!mo.getText().equals("")) {
                                                        DBWindowController.showAlert("Необходимо указать дисперсию", "Необходимо заполнить все поля");
                                                    } else
                                                        DBWindowController.showAlert("Необходимо указать дисперсию и математическое ожидание", "Необходимо заполнить все поля");
                                                }
                                            } else if (rExp.isSelected()) {
                                                if (!intens.getText().equals("")) {
                                                    double rate = Double.valueOf(intens.getText());
                                                    if (rate < 1 && rate > 0) {
                                                        if (rate > 0.5) {
                                                            rate -= 0.3;
                                                        }
                                                        flow = new RandomFlow(new ExponentialDistribution(rate));
                                                    } else {
                                                        DBWindowController.showAlert("Интенсивность должна быть в промежутке от 0 до 1", "Некорректное значение интенсивности");
                                                    }
                                                } else
                                                    DBWindowController.showAlert("Необходимо указать интенсивность", "Необходимо заполнить все поля");
                                            } else {
                                                if (!a.getText().equals("")) {
                                                    if (!b.getText().equals("")) {
                                                        double left, right;
                                                        if ((left = Double.valueOf(a.getText())) < (right = Double.valueOf(b.getText()))) {
                                                            flow = new RandomFlow(new UniformDistribution(left, right));
                                                        } else
                                                            DBWindowController.showAlert("Левая граница должна быть меньше правой", "Некорректные данные");
                                                    } else {
                                                        DBWindowController.showAlert("Необходмсо указать правую границу", "Необходимо заполнить все поля");
                                                    }
                                                } else {
                                                    if (!b.getText().equals("")) {
                                                        DBWindowController.showAlert("Необходимо указать левую границу", "Необходимо заполнить все поля");
                                                    } else
                                                        DBWindowController.showAlert("Необходимо указать правую и левую границы", "Необходимо заполнить все поля");
                                                }


                                            }

                                        }
                                        CarPool cp = new CarPool(flow);
                                        try {
                                            ArrayList<edu.ssau.gasstation.car.Car> pool = cp.getCarPool(10);
                                            System.out.println("POOL");
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        //todo открывать окно моделирования
                                        System.out.println("FLOW");
                                    });
                                    Button cancel = new Button("Отмена");
                                    cancel.setOnAction(event1 -> preRunStage.close());
                                    GridPane buttons = new GridPane();
                                    buttons.setHgap(5);
                                    buttons.add(ok, 1, 0);
                                    buttons.add(cancel, 0, 0);
                                    buttons.setAlignment(Pos.BOTTOM_RIGHT);
                                    mainBox.getChildren().addAll(hb, determPane, fuelAndOffice, buttons);

                                    preRunStage.setScene(new Scene(mainBox));
                                    preRunStage.show();
                                } else
                                    DBWindowController.showAlert("Необходимо разместить хотя бы одну ТРК", "Невозможно запустить");
                            } else
                                DBWindowController.showAlert("Необходимо разместить хотя бы один резервуар", "Невозможно запустить");
                        } else DBWindowController.showAlert("Необходимо разместить выезд", "Невозможно запустить");
                    } else DBWindowController.showAlert("Необходимо разместить въезд", "Невозможно запустить");
                } else {
                    if (in) {
                        if (out) {
                            if (tankCount > 0) {
                                if (dispenserCount > 0) {
                                    DBWindowController.showAlert("Необходимо разместить кассу", "Невозможно запустить");
                                } else
                                    DBWindowController.showAlert("Необходимо разместить кассу и хотя бы одну ТРК", "Невозможно запустить");
                            } else {
                                if (dispenserCount > 0) {
                                    DBWindowController.showAlert("Необходимо разместить кассу и хотя бы один резервуар", "Невозможно запустить");
                                } else
                                    DBWindowController.showAlert("Необходимо разместить кассу, хотя бы одну ТРК и хотя бы один резервуар", "Невозможно запустить");
                            }
                        } else {
                            if (tankCount > 0) {
                                if (dispenserCount > 0) {
                                    DBWindowController.showAlert("Необходимо разместить выезд и кассу", "Невозможно запустить");
                                } else
                                    DBWindowController.showAlert("Необходимо разместить выезд, кассу и хотя бы одну ТРК", "Невозможно запустить");
                            } else {
                                if (dispenserCount > 0) {
                                    DBWindowController.showAlert("Необходимо разместить выезд, кассу и хотя бы один резервуар", "Невозможно запустить");
                                } else
                                    DBWindowController.showAlert("Необходимо разместить выезд, кассу, хотя бы один резервуар и хотя бы одну ТРК", "Невозможно запустить");
                            }
                        }
                    } else {
                        if (out) {
                            if (tankCount > 0) {
                                if (dispenserCount > 0) {
                                    DBWindowController.showAlert("Необходимо разместить въезд и кассу", "Невозможно запустить");
                                } else
                                    DBWindowController.showAlert("Необходимо разместить въезд и хотя бы одну ТРК", "Невозможно запустить");
                            } else {
                                if (dispenserCount > 0) {
                                    DBWindowController.showAlert("Необходимо разместить выезд, кассу и хотя бы один резервуар", "Невозможно запустить");
                                } else
                                    DBWindowController.showAlert("Необходимо разместить выезд, кассу, хотя бы одни резервуа и хотя бы одну ТРК", "Невозможно запустить");
                            }
                        } else {
                            if (tankCount > 0) {
                                if (dispenserCount > 0) {
                                    DBWindowController.showAlert("Необходимо разместить въезд, выезд и кассу", "Невозможно запустить");
                                } else
                                    DBWindowController.showAlert("Необходимо разместить въезд, выезд, кассу и хотя бы одну ТРК", "Невозможно запустить");
                            } else {
                                if (dispenserCount > 0) {
                                    DBWindowController.showAlert("Необходимо разместить выезд, выезд, кассу и хотя бы один резервуар", "Невозможно запустить");
                                } else
                                    DBWindowController.showAlert("Необходимо разместить выезд, выезд, кассу, хотя бы одни резервуа и хотя бы одну ТРК", "Невозможно запустить");
                            }
                        }
                    }
                }

                //DBWindowController.showAlert("В разработке", "Не возможно открыть");
            } catch (IllegalArgumentException e) {
                showSettings(icList.get(exI * topology.getWidth() + exJ));
                DBWindowController.showAlert(e.getMessage(), "Невозможно запустить моделирование");
            }
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
                            refItemList.get(1).setDisable(true);
                        }
                        else throw new IOException("Недопустимая конфигурация топологии");
                    }
                    else if(item instanceof Exit){
                        if(!out) {
                            pic = "out-small.png";
                            out = true;
                            refItemList.get(2).setDisable(true);
                        }
                        else throw new IOException("Недопустимая конфигурация топологии");
                    }
                    else if(item instanceof Dispenser){
                        pic = "dispenser-small.png";
                        dispenserCount++;
                        if(dispenserCount > 4){
                            refItemList.get(3).setDisable(true);
                        }
                    }
                    else if(item instanceof Tank){
                        pic = "tank-small.png";
                        tankCount++;
                        if(tankCount > 4){
                            refItemList.get(4).setDisable(true);
                        }
                    }
                    else if(item instanceof Office){
                        if(!office) {
                            pic = "office-small.png";
                            office = true;
                            refItemList.get(5).setDisable(true);
                        }
                        else throw new IOException("Недопустимая конфигурация топологии");
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

    private void chekTopologySize(TextField field, String mutcher, int top){
        field.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty()) {
                    if (!newValue.matches(mutcher)) {
                        field.setText(oldValue);
                    } else {
                        try {
                            int check = Integer.valueOf(newValue);
                            if (check > top) {
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

    private void checkTextField(TextField field, String mutcher, int min){
        field.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty()) {
                    if (!newValue.matches(mutcher)) {
                        field.setText(oldValue);
                    } else {
                        try {
                            double check = Double.valueOf(newValue);
                            if (check < min) {
                                field.setText(oldValue);
                            } else {
                                field.setText(newValue);
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
