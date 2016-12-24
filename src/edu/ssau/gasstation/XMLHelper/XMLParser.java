package edu.ssau.gasstation.XMLHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import edu.ssau.gasstation.topology.Dispenser;
import edu.ssau.gasstation.topology.Entry;
import edu.ssau.gasstation.topology.Exit;
import edu.ssau.gasstation.topology.Office;
import edu.ssau.gasstation.topology.Tank;
import edu.ssau.gasstation.topology.Topology;
import edu.ssau.gasstation.topology.TopologyItem;

enum States {
  Start, Topology, Objects, Finish
}

//edu/ssau/gasstation/parseHelper/XMLParser.java
enum StatesSize { //I do not know what for this variable exists in my code
  Start
}


public class XMLParser {
  static Topology result = null;
  static ArrayList<ElementOfTopology> items = new ArrayList<>();
  static XMLStreamReader xmlr = null;
  static boolean correct = true;
  private static final boolean DEBUG = false;

  public static Topology getTopologyFromFile(String fileName) throws XMLStreamException {
    //final String fileName = "result.xml";
    if (DEBUG) {
      fileName = "result.xml";
    }
    States state = States.Start; //in which of tags we are

    try {
      xmlr = XMLInputFactory.newInstance().createXMLStreamReader(fileName, new FileInputStream(fileName));

      while (xmlr.hasNext()) {
        skip();
        if (xmlr.isStartElement()) {
          String element = xmlr.getLocalName();
          switch (state) {
            case Start:
              if (element.equalsIgnoreCase("topology")) {
                state = States.Topology;
              } else {
                error(element, "Topology");
              }
              break;
            case Topology:
              if (element.equalsIgnoreCase("size")) {
                boolean vertical = false;
                boolean horizontal = false;
                int verticalValue = 0;
                int horizontalValue = 0;
                StatesSize statesCurrent = StatesSize.Start;
                while (xmlr.hasNext()) {
                  if (vertical && horizontal) {
                    result = new Topology(verticalValue, horizontalValue);
                    if (verticalValue < 2 || verticalValue > 10 || horizontalValue < 2 || horizontalValue > 10) {
                      correct = false;
                    }
                    boolean isOk = false;
                    while (xmlr.hasNext()) {
                      //xmlr.next();
                      skip();
                      if (!xmlr.isEndElement() || !xmlr.getLocalName().equalsIgnoreCase("size")) {
                        error(xmlr.getLocalName(), "closed Size");
                      } else {
                        break;
                      }
                    }

                    break;
                  }
                  xmlr.next();
                  if (!xmlr.isEndElement() && !xmlr.isStartElement() && xmlr.getText().trim().equalsIgnoreCase("") && xmlr.hasNext()) {
                    xmlr.next();
                  }
                  try {
                    element = xmlr.getLocalName();
                  } catch (Exception e) {
                    element = xmlr.getText();
                  }

                  switch (statesCurrent) {
                    case Start:
                      if (element.equalsIgnoreCase("vertical")) {
                        if (vertical) {
                          exists(xmlr.getLocalName());
                        }
                        if (xmlr.hasNext()) {
                          xmlr.next();
                          if (!xmlr.isEndElement() && !xmlr.isStartElement()) {
                            try {
                              verticalValue = Integer.parseInt(xmlr.getText());
                              vertical = true;
                            } catch (Exception e) {
                              error(xmlr.getLocalName(), "any number");
                            }
                          } else {
                            error(xmlr.getLocalName(), "any number");
                          }
                          if (xmlr.hasNext()) {
                            xmlr.next();
                            if (xmlr.isEndElement() && xmlr.getLocalName().equalsIgnoreCase("vertical")) {
                              statesCurrent = StatesSize.Start;
                            } else {
                              error(xmlr.getLocalName(), "closed Vertical");
                            }
                          } else {
                            error(xmlr.getLocalName(), "closed Vertical");
                          }
                        }
                      } else if (element.equalsIgnoreCase("horizontal")) {
                        if (horizontal) {
                          exists(xmlr.getLocalName());
                        }
                        if (xmlr.hasNext()) {
                          xmlr.next();
                          if (!xmlr.isEndElement() && !xmlr.isStartElement()) {
                            try {
                              horizontalValue = Integer.parseInt(xmlr.getText());
                              horizontal = true;
                            } catch (Exception e) {
                              error(xmlr.getLocalName(), "any number");
                            }
                          } else {
                            error(xmlr.getLocalName(), "any number");
                          }
                          if (xmlr.hasNext()) {
                            xmlr.next();
                            if (xmlr.isEndElement() && xmlr.getLocalName().equalsIgnoreCase("horizontal")) {
                              statesCurrent = StatesSize.Start;
                            } else {
                              error(xmlr.getLocalName(), "closed Horizontal");
                            }
                          } else {
                            error(xmlr.getLocalName(), "closed Horizontal");
                          }
                        }
                      } else {
                        error(element, "opened Vertical or Horizontal");
                      }
                      break;
                  }

                }
              } else if (element.equalsIgnoreCase("objects")) {
                state = States.Objects;

              } else {
                error(element, "Size or Objects");
              }
              break;
            case Objects:
              if (element.equalsIgnoreCase("FillingStation") ||
                element.equalsIgnoreCase("FuelTank")) {
                boolean x = false, y = false, id = false;
                int xValue = 0, yValue = 0, IDValue = 0;
                while (xmlr.hasNext()) {
                  skip();
                  if (x && y && id) {
                    if (element.equalsIgnoreCase("FillingStation")) {
                      if (xmlr.isEndElement() && xmlr.getLocalName().equalsIgnoreCase("FillingStation")) {
                        items.add(new ElementOfTopology(new Dispenser(IDValue), xValue, yValue));
                        break;
                      } else {
                        error(xmlr.getLocalName(), "closed FillingStation");
                      }
                    } else if (element.equalsIgnoreCase("FuelTank")) {
                      if (xmlr.isEndElement() && xmlr.getLocalName().equalsIgnoreCase("FuelTank")) {
                        items.add(new ElementOfTopology(new Tank(IDValue), xValue, yValue));
                        break;
                      } else {
                        error(xmlr.getLocalName(), "closed FuelTank");
                      }
                    }
                  }
                  if (xmlr.isStartElement()) {
                    if (xmlr.getLocalName().equalsIgnoreCase("x")) {
                      if (x) {
                        exists(xmlr.getLocalName());
                      }
                      skip();
                      if (!xmlr.isEndElement() && !xmlr.isStartElement()) {
                        try {
                          xValue = Integer.parseInt(xmlr.getText());
                          x = true;
                        } catch (Exception e) {
                          error(xmlr.getText(), "any number");
                        }
                      }
                      skip();
                      if (!xmlr.isEndElement() || !xmlr.getLocalName().equalsIgnoreCase("x")) {
                        error(xmlr.getLocalName(), "closed x");
                      }
                    } else if (xmlr.getLocalName().equalsIgnoreCase("y")) {
                      if (y) {
                        exists(xmlr.getLocalName());
                      }
                      skip();
                      if (!xmlr.isEndElement() && !xmlr.isStartElement()) {
                        try {
                          yValue = Integer.parseInt(xmlr.getText());
                          y = true;
                        } catch (Exception e) {
                          error(xmlr.getText(), "any number");
                        }
                      }
                      skip();
                      if (!xmlr.isEndElement() || !xmlr.getLocalName().equalsIgnoreCase("y")) {
                        error(xmlr.getLocalName(), "closed y");
                      }

                    } else if (xmlr.getLocalName().equalsIgnoreCase("FuelType")) {
                      if (id) {
                        exists(xmlr.getLocalName());
                      }
                      skip();
                      if (!xmlr.isEndElement() && !xmlr.isStartElement()) {
                        try {
                          IDValue = Integer.parseInt(xmlr.getText());
                          id = true;
                        } catch (Exception e) {
                          error(xmlr.getText(), "any number");
                        }
                      }
                      skip();
                      if (!xmlr.isEndElement() || !xmlr.getLocalName().equalsIgnoreCase("FuelType")) {
                        error(xmlr.getLocalName(), "closed ID");
                      }
                    } else {
                      error(xmlr.getLocalName(), "opened x or y or ID");
                    }
                  } else {
                    error(xmlr.getLocalName(), "opened x or y or ID");
                  }
                }
              } else if (element.equalsIgnoreCase("CashDesc") ||
                element.equalsIgnoreCase("Entrance") ||
                element.equalsIgnoreCase("Exit")) {
                boolean x = false, y = false;
                int xValue = 0, yValue = 0;
                while (xmlr.hasNext()) {
                  skip();
                  if (x && y) {
                    if (element.equalsIgnoreCase("CashDesc")) {
                      if (xmlr.isEndElement() && xmlr.getLocalName().equalsIgnoreCase("CashDesc")) {
                        items.add(new ElementOfTopology(new Office(), xValue, yValue));
                        break;
                      } else {
                        error(xmlr.getLocalName(), "closed CashDesc");
                      }
                    } else if (element.equalsIgnoreCase("Entrance")) {
                      if (xmlr.isEndElement() && xmlr.getLocalName().equalsIgnoreCase("Entrance")) {
                        items.add(new ElementOfTopology(new Entry(), xValue, yValue));
                        break;
                      } else {
                        error(xmlr.getLocalName(), "closed Entrance");
                      }
                    } else if (element.equalsIgnoreCase("Exit")) {
                      if (xmlr.isEndElement() && xmlr.getLocalName().equalsIgnoreCase("Exit")) {
                        items.add(new ElementOfTopology(new Exit(), xValue, yValue));
                        break;
                      } else {
                        error(xmlr.getLocalName(), "closed Exit");
                      }
                    }
                  }
                  if (xmlr.isStartElement()) {
                    if (xmlr.getLocalName().equalsIgnoreCase("x")) {
                      if (x) {
                        exists(xmlr.getLocalName());
                      }
                      skip();
                      if (!xmlr.isEndElement() && !xmlr.isStartElement()) {
                        try {
                          xValue = Integer.parseInt(xmlr.getText());
                          x = true;
                        } catch (Exception e) {
                          error(xmlr.getText(), "any number");
                        }
                      }
                      skip();
                      if (!xmlr.isEndElement() || !xmlr.getLocalName().equalsIgnoreCase("x")) {
                        error(xmlr.getLocalName(), "closed x");
                      }
                    } else if (xmlr.getLocalName().equalsIgnoreCase("y")) {
                      if (y) {
                        exists(xmlr.getLocalName());
                      }
                      skip();
                      if (!xmlr.isEndElement() && !xmlr.isStartElement()) {
                        try {
                          yValue = Integer.parseInt(xmlr.getText());
                          y = true;
                        } catch (Exception e) {
                          error(xmlr.getText(), "any number");
                        }
                      }
                      skip();
                      if (!xmlr.isEndElement() || !xmlr.getLocalName().equalsIgnoreCase("y")) {
                        error(xmlr.getLocalName(), "closed y");
                      }
                    } else {
                      error(xmlr.getLocalName(), "opened x or y");
                    }
                  } else {
                    error(xmlr.getLocalName(), "opened x or y or");
                  }
                }
              } else if (element.equalsIgnoreCase("Entrance")) {

              }
              break;
          }

          System.out.println(xmlr.getLocalName());
        } else if (xmlr.isEndElement()) {
          switch (state) {
            case Objects:
              if (xmlr.getLocalName().equalsIgnoreCase("Objects")) {
                state = States.Topology;
              } else {
                error(xmlr.getLocalName(), "closed Objects");
              }
              break;
            case Topology:
              if (xmlr.getLocalName().equalsIgnoreCase("Topology")) {
                state = States.Finish;
              } else {
                error(xmlr.getLocalName(), "closed Topology");
              }
              break;
          }
          System.out.println("/" + xmlr.getLocalName());
        } else if (xmlr.hasText() && xmlr.getText().trim().length() > 0) {
          System.out.println("   " + xmlr.getText());
        }
      }
    } catch (FileNotFoundException | XMLStreamException ex) {
      ex.printStackTrace();
    }
    if (state != States.Finish) {
      System.err.println("Unexpected error");
      correct = false;
    }
    checkItems();
    TopologyItem ti = result.getTopologyItem(1, 1);
    if (correct) {
        items.clear();
      return result;
    }
    else throw new XMLStreamException("Топология не корректна");
  }

  private static void checkItems() {
    int width = result.getWidth();
    int height = result.getHeight();
    result = new Topology(height, width);

    for (ElementOfTopology e : items) {
      if (e.x > width || e.y > height || e.x < 0 || e.y < 0) {
        correct = false;
        System.err.println("Incorrect coordinates of " + e.toString());
      } else {
        result.setTopologyItem(e.item, e.x, e.y);
      }
      //TODO count Tanks, Offices, Dispensers, Exits, Enterances
    }
  }

  static private boolean checkCount(Topology result) {
    int tanks = 0;//min 1 max 5
    int offices = 0;//1
    int dispensers = 0;//min 1
    int exits = 0;//1
    int enterances = 0;//1
    TopologyItem[][] items = result.getTopology();
    HashSet<Integer> fuelIds = new HashSet<>();
    ArrayList<Dispenser> disps = new ArrayList<>(); //
    for (int i = 0; i < items.length; i++) {
      for (int j = 0; j < items[i].length; j++) {
        if (items[i][j] == null)
          continue;
        else if (items[i][j] instanceof Dispenser) {
          disps.add((Dispenser) items[i][j]);
          dispensers++;
        } else if (items[i][j] instanceof Entry) {
          enterances++;
        } else if (items[i][j] instanceof Exit) {
          exits++;
        } else if (items[i][j] instanceof Office) {
          offices++;
        } else if (items[i][j] instanceof Tank) {
          Tank currentTank = (Tank) items[i][j];
          fuelIds.add(currentTank.getFuelID());
          tanks++;
        } else {
          //TODO undefined object
          return false;
        }
      }
    }
    for (Dispenser d : disps) {
      if (!fuelIds.contains(d.getFuelID())) {
        //TODO unknown fuelType
        return false;
      }
    }
    if (offices != 1 ||
      dispensers != 1 ||
      exits != 1 ||
      (tanks > 5 || tanks == 0) ||
      enterances != 1
      ) {
      //TODO incorrect counts of items
      return false;
    }
    return true;
  }

  static private void skip() {
    try {
      while (xmlr.hasNext()) {
        xmlr.next();
        try {
          if (xmlr.isEndElement() || xmlr.isStartElement())
            break;
          if (xmlr.getText().trim().equals(""))
            continue;
          else
            break;
        } catch (Exception e) {
        }
      }
    } catch (XMLStreamException e) {
      e.printStackTrace();
    }
  }

  private static void error(String tag, String expectedString) {
    correct = false;
    System.err.println("Tag: " + tag + " but expected tag: " + expectedString);
  }

  private static void exists(String tag) {
    correct = false;
    System.err.println(tag + " already exists");
  }

  static class ElementOfTopology {
    TopologyItem item;
    int x, y;

    public ElementOfTopology(TopologyItem item, int x, int y) {
      this.item = item;
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return "ElementOfTopology{" +
        "item=" + item +
        ", x=" + x +
        ", y=" + y +
        '}';
    }
  }
}
