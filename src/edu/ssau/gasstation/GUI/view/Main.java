package edu.ssau.gasstation.GUI.view;

import edu.ssau.gasstation.GUI.controllers.ModelWindowController;
import edu.ssau.gasstation.XMLHelper.XMLParser;
import edu.ssau.gasstation.modelling.PoolUtils.CarController;
import edu.ssau.gasstation.topology.Topology;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by andrey on 05.12.16.
 */
public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
        /*Parent root = FXMLLoader.load(getClass().getResource("view/mainWindow.fxml"));
        primaryStage.setTitle("West Coast AZS");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();*/
    Platform.setImplicitExit(false);
    Topology topology = XMLParser.getTopologyFromFile("12334.xml");
    CarController ctrl = new CarController(topology);
    FXMLLoader loader = new FXMLLoader(getClass().getResource("modelWindow.fxml"));
    Parent root = loader.load();
    ModelWindowController controller = loader.getController();

    primaryStage.setTitle("Model");
    primaryStage.setScene(new Scene(root));
    primaryStage.setMaximized(true);
    primaryStage.show();
    ctrl.setController(controller);
    ctrl.start();
  }


  public static void main(String[] args) {
    launch(args);
  }
}
