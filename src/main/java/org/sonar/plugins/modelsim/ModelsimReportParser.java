 /*
 * sonar-coverage-modelsim plugin for Sonarqube & Modelsim
 * Copyright (C) 2019 Linty Services
 * 
 * Based on :
 *  SonarQube Cobertura Plugin
 * Copyright (C) 2018-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sonar.plugins.modelsim;

import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.fs.InputFile;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.text.ParseException;

import javax.xml.stream.XMLInputFactory;
import org.codehaus.staxmate.SMInputFactory;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import static org.sonar.api.utils.ParsingUtils.parseNumber;

public class ModelsimReportParser {


  private final SensorContext context;

  private ModelsimReportParser(SensorContext context) {
    this.context = context;
  }

  /**
   * Parse a Modelsim xml report and create measures accordingly
   */
  public static void parseReport(File xmlFile, SensorContext context) {
    new ModelsimReportParser(context).parse(xmlFile);
  }

  private void parse(File xmlFile) {
    try {
      SMInputFactory inputFactory = initStax();
      SMHierarchicCursor rootCursor = inputFactory.rootElementCursor(xmlFile);
      while (rootCursor.getNext() != null) {
        collectReportMeasures(rootCursor.descendantElementCursor("code_coverage_report"));
      }
      rootCursor.getStreamReader().closeCompletely();
    }
    catch (XMLStreamException e) {
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
    NewCoverage coverage = null;
    boolean lineAdded = false;
    if (resourceExists(resource)) {
      coverage = context.newCoverage();
      coverage.onFile(resource);
    }

    SMInputCursor statement = clazz.childElementCursor("stmt");
    while (statement.getNext() != null) {
      int lineId = Integer.parseInt(statement.getAttrValue("ln"));
      try {
        if (coverage != null) {
          coverage.lineHits(lineId, (int) parseNumber(statement.getAttrValue("hits")));
          lineAdded = true;
        }
      }
      catch (ParseException e) {
        throw new XMLStreamException(e);
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
