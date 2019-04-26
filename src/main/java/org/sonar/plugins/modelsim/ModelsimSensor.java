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

import org.sonar.api.config.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Configuration;
import org.sonar.api.scan.filesystem.PathResolver;
import java.io.File;

public class ModelsimSensor implements Sensor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModelsimSensor.class);

  private FileSystem fs;
  private PathResolver pathResolver;
  private final Configuration configuration;

  public ModelsimSensor(FileSystem fs, PathResolver pathResolver, Settings settings,
                          Configuration configuration) {
    this.fs = fs;
    this.pathResolver = pathResolver;
    this.configuration=configuration;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
	  descriptor.onlyOnLanguage("vhdl").name("ModelsimSensor");
  }

  @Override
  public void execute(SensorContext context) {
    String path=configuration.get(ModelsimPlugin.MODELSIM_REPORT_PATH_PROPERTY).orElse(null);
    File report = pathResolver.relativeFile(fs.baseDir(), path);
    if (!report.isFile() || !report.exists() || !report.canRead()) {
      LOGGER.warn("Modelsim report not found at {}", report);
    } else {
      parseReport(report, context);
    }
  }

  protected void parseReport(File xmlFile, SensorContext context) {
    LOGGER.info("parsing {}", xmlFile);
    ModelsimReportParser.parseReport(xmlFile, context);
  }

}
