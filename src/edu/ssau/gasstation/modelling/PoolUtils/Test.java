/*
 * Copyright (c) 2016 SSI Schaefer Noell GmbH
 *
 * $Id: Test 19:32 $ / $HeadURL:  $
 *
 */

package edu.ssau.gasstation.modelling.PoolUtils;

import javax.xml.stream.XMLStreamException;

import edu.ssau.gasstation.XMLHelper.XMLParser;
import edu.ssau.gasstation.topology.Topology;

/**
 * @author <a href="mailto:ivan.rykov@ssi-schaefer.ru">ivan.rykov</a>
 * @version $Revision: 1227 $, $Date: 2014-08-08 09:02:22 +0400 (Fri, 08 Aug 2014) $, $Author: versiya-g.nikolaev $
 */
public class Test {
  public static void main(String[] args) throws XMLStreamException {
    Topology topology = XMLParser.getTopologyFromFile("base.xml");
    CarController ctrl = new CarController(topology);
    ctrl.start();
  }
}
