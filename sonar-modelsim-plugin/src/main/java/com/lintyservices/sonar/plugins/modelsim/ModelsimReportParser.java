/*
 * SonarQube Linty ModelSim :: Plugin
 * Copyright (C) 2019-2020 Linty Services
 * mailto:contact@linty-services.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.lintyservices.sonar.plugins.modelsim;

import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.coverage.NewCoverage;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;

import static org.sonar.api.utils.ParsingUtils.parseNumber;

public class ModelsimReportParser {

  private final SensorContext context;

  private String mode;

  private NewCoverage coverage;

  private ModelsimReportParser(SensorContext context) {
    this.context = context;
  }

  /**
   * Parse a Modelsim xml report and create measures accordingly
   */
  public static void parseReport(File xmlFile, SensorContext context, String mode) {
    new ModelsimReportParser(context).parse(xmlFile, mode);
  }

  private void parse(File xmlFile, String mode) {
    this.mode = mode;
    //System.out.println("mode : "+mode);
    try {
      SMInputFactory inputFactory = initStax();
      SMHierarchicCursor rootCursor = inputFactory.rootElementCursor(xmlFile);
      while (rootCursor.getNext() != null) {
        collectReportMeasures(rootCursor.descendantElementCursor("code_coverage_report"));
      }
      rootCursor.getStreamReader().closeCompletely();
    } catch (XMLStreamException e) {
      throw new IllegalStateException("XML is not valid", e);
    }
  }

  private static SMInputFactory initStax() {
    final XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
    xmlFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    xmlFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
    return new SMInputFactory(xmlFactory);
  }

  private void collectReportMeasures(SMInputCursor report) throws XMLStreamException {
    while (report.getNext() != null) {
      collectFileMeasures(report.descendantElementCursor("fileData"));
    }
  }

  private boolean resourceExists(InputFile file) {
    return file != null && context.fileSystem().inputFile(context.fileSystem().predicates().is(file.file())) != null;
  }

  private void collectFileMeasures(SMInputCursor clazz) throws XMLStreamException {
    while (clazz.getNext() != null) {
      String path = clazz.getAttrValue("path");
      collectFileData(clazz, path);
    }
  }

  private void collectFileData(SMInputCursor clazz, String path) throws XMLStreamException {
    InputFile resource = context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(path));
    coverage = null;
    boolean lineAdded = false;
    if (resourceExists(resource)) {
      coverage = context.newCoverage();
      coverage.onFile(resource);
    }

    SMInputCursor element = clazz.childCursor();
    while (element.getNext() != null) {
      String name = null;
      try {
        name = element.getPrefixedName();
      } catch (Exception e) {
      }
      if (mode.equalsIgnoreCase("condition") && name != null && name.equalsIgnoreCase("condition")) {
        try {
          if (coverage != null) {
            int ln = Integer.parseInt(element.getAttrValue("ln"));
            coverage.lineHits(ln, 1);
            coverage.conditions(ln, Integer.parseInt(element.getAttrValue("active")), Integer.parseInt(element.getAttrValue("hits")));
            lineAdded = true;
          }
        } catch (Exception e) {
          // throw new XMLStreamException(e);
        }
      } else if (mode.equalsIgnoreCase("branch") && name != null && (name.equalsIgnoreCase("case") || name.equalsIgnoreCase("if"))) {
        try {
          int active = Integer.parseInt(element.getAttrValue("active"));
          int hits = Integer.parseInt(element.getAttrValue("hits"));
          if (coverage != null) {
            SMInputCursor child = element.childCursor();
            child.getNext();
            child.getNext();
            int ln = Integer.parseInt(child.getAttrValue("ln"));
            coverage.lineHits(ln, 1);
            coverage.conditions(ln, active, hits);
            lineAdded = true;
          }
        } catch (Exception e) {
          // throw new XMLStreamException(e);
        }
      } else if (name != null && name.equalsIgnoreCase("stmt")) {
        try {
          int ln = Integer.parseInt(element.getAttrValue("ln"));
          if (coverage != null) {
            coverage.lineHits(ln, (int) parseNumber(element.getAttrValue("hits")));
            lineAdded = true;
          }
        } catch (Exception e) {
          throw new XMLStreamException(e);
        }
      }
    }


    if (coverage != null) {
      // If there was no lines covered or uncovered (e.g. everything is ignored), but the file exists then Sonar would report the file as uncovered
      // so adding a fake one to line number 1
      if (!lineAdded) {
        coverage.lineHits(1, 1);
      }
      coverage.save();
    }
  }

}
