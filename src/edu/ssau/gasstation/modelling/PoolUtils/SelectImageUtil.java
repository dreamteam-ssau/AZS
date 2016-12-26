package edu.ssau.gasstation.modelling.PoolUtils;

import edu.ssau.gasstation.topology.*;
import javafx.scene.image.Image;

public class SelectImageUtil {
  public static Image getImageByItemType (TopologyItem item, Topology topology) {
    Image result;
    if(item instanceof Tank) {
      result = new Image("edu/ssau/gasstation/GUI/controllers/images/tank-small.png");
    } else if(item instanceof Entry) {
      result = new Image("edu/ssau/gasstation/GUI/controllers/images/in-small.png");
    } else if(item instanceof Office) {
      result = new Image("edu/ssau/gasstation/GUI/controllers/images/office-small.png");
    } else if(item instanceof Exit) {
      result = new Image("edu/ssau/gasstation/GUI/controllers/images/out-small.png");
    } else if(item instanceof Dispenser) {
      result = new Image("edu/ssau/gasstation/GUI/controllers/images/dispenser-small.png");
    } else if(item instanceof edu.ssau.gasstation.topology.Refueller) {
      result = new Image("edu/ssau/gasstation/GUI/controllers/images/car.png");
    } else if (item instanceof Collector){
      result = new Image("edu/ssau/gasstation/GUI/controllers/images/car.png");
    } else {
      result = new Image("edu/ssau/gasstation/GUI/controllers/images/car.png");
    }
    return result;
  }
}
