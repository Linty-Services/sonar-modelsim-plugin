/*
 * Copyright (C) 2019-2025 Linty Services
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

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelSimSensor implements Sensor {

  private static final Logger LOG = Loggers.get(ModelSimSensor.class);

  private final FileSystem fs;
  private final PathResolver pathResolver;
  private final Configuration configuration;

  public ModelSimSensor(FileSystem fs, PathResolver pathResolver, Configuration configuration) {
    this.fs = fs;
    this.pathResolver = pathResolver;
    this.configuration = configuration;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("ModelSimSensor");
  }

  @Override
  public void execute(SensorContext context) {
    Set<File> reportFiles = reportFiles();
    String mode = configuration.get(ModelSimPlugin.ADDITIONAL_REPORT_TYPE).orElse(null);
    for (File reportFile : reportFiles) {
      parseReport(reportFile, context, mode);
    }
  }

  protected void parseReport(File xmlFile, SensorContext context, String mode) {
    LOG.info("[ModelSim] Parsing {}", xmlFile);
    ModelSimReportParser.parseReport(xmlFile, context, mode);
  }

  private Set<File> reportFiles() {
    String reportPathsProperty = configuration.get(ModelSimPlugin.REPORT_PATHS).orElse(null);
    Set<String> reportPaths = new HashSet<>();
    if (reportPathsProperty != null) {
      reportPaths = Arrays.stream(reportPathsProperty.split(",")).map(String::trim).collect(Collectors.toSet());
    }

    Set<File> reportFiles = new HashSet<>();
    for (String path : reportPaths) {
      File reportFile = pathResolver.relativeFile(fs.baseDir(), path);
      if (reportFile == null || !reportFile.exists()) {
        LOG.warn("[ModelSim] Cannot find \"{}\" report", path);
      } else if (!reportFile.canRead()) {
        LOG.warn("[ModelSim] Cannot read \"{}\" report", path);
      } else if (reportFile.isDirectory()) {
        reportFiles.addAll(
          Arrays.stream(reportFile.listFiles())
            .filter(f -> f.isFile() && f.getName().endsWith(".xml"))
            .collect(Collectors.toSet()));
      } else if (reportFile.isFile()) {
        reportFiles.add(reportFile);
      }
    }
    return reportFiles;
  }

}
