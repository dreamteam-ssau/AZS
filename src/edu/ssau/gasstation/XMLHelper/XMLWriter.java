
package edu.ssau.gasstation.XMLHelper;


import java.io.FileWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import edu.ssau.gasstation.topology.Dispenser;
import edu.ssau.gasstation.topology.Entry;
import edu.ssau.gasstation.topology.Exit;
import edu.ssau.gasstation.topology.Office;
import edu.ssau.gasstation.topology.Tank;
import edu.ssau.gasstation.topology.Topology;
import edu.ssau.gasstation.topology.TopologyItem;

public class XMLWriter {

  public static void write(Topology topology, String fileName) {
    try {
      TopologyItem[][] items = topology.getTopology();
      XMLOutputFactory output = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = output.createXMLStreamWriter(new FileWriter(fileName));
      writer.writeStartDocument("1.0");
      writer.writeCharacters("\n");
      writer.writeStartElement("Topology");
      writer.writeCharacters("\n");
      writer.writeStartElement("Size");
      writer.writeCharacters("\n");
      writer.writeStartElement("Vertical");
      writer.writeCharacters(topology.getHeight()+"");
      writer.writeEndElement();
      writer.writeCharacters("\n");
      writer.writeStartElement("Horizontal");
      writer.writeCharacters(topology.getWidth()+"");
      writer.writeEndElement();
      writer.writeCharacters("\n");
      writer.writeEndElement();
      writer.writeCharacters("\n");
      writer.writeStartElement("Objects");
      writer.writeCharacters("\n");
      for (int i = 0; i < items.length; i++) {
        for (int j = 0; j < items[i].length; j++) {
          if(items[i][j]==null) continue;

          if(items[i][j] instanceof Dispenser) {
            Dispenser disp = (Dispenser) items[i][j];
            writer.writeStartElement("FillingStation");
            writer.writeCharacters("\n");
            writer.writeStartElement("x");
            writer.writeCharacters(i+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeStartElement("y");
            writer.writeCharacters(j+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeStartElement("FuelType");
            writer.writeCharacters(disp.getFuelID()+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndElement();
            writer.writeCharacters("\n");
          } else if(items[i][j] instanceof Office) {
            writer.writeStartElement("CashDesc");
            writer.writeCharacters("\n");
            writer.writeStartElement("x");
            writer.writeCharacters(i+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeStartElement("y");
            writer.writeCharacters(j+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndElement();
            writer.writeCharacters("\n");
          } else if(items[i][j] instanceof Entry) {
            writer.writeStartElement("Entrance");
            writer.writeCharacters("\n");
            writer.writeStartElement("x");
            writer.writeCharacters(i+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeStartElement("y");
            writer.writeCharacters(j+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndElement();
            writer.writeCharacters("\n");
          } else if(items[i][j] instanceof Exit) {
            writer.writeStartElement("Exit");
            writer.writeCharacters("\n");
            writer.writeStartElement("x");
            writer.writeCharacters(i+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeStartElement("y");
            writer.writeCharacters(j+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndElement();
          } else if(items[i][j] instanceof Tank) {
            Tank tank = (Tank) items[i][j];
            writer.writeStartElement("FuelTank");
            writer.writeCharacters("\n");
            writer.writeStartElement("x");
            writer.writeCharacters(i+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeStartElement("y");
            writer.writeCharacters(j+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeStartElement("FuelType");
            writer.writeCharacters(tank.getFuelID()+"");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndElement();
            writer.writeCharacters("\n");
          }
        }
      }
      writer.writeEndElement();
      writer.writeCharacters("\n");
      writer.writeEndElement();
      writer.writeCharacters("\n");
      writer.writeEndDocument();
      writer.flush();



    } catch (Exception e) {
      e.printStackTrace();
    }
  }




}
