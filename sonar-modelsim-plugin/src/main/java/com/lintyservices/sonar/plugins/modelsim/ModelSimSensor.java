/*
 * This confidential and proprietary software may be used only as authorized
 * by a licensing agreement from Linty Services.
 * (c) Copyright 2016-2026 Linty Services
 * ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorized copies.
 */
package com.lintyservices.sonar.plugins.modelsim;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.scan.filesystem.PathResolver;

public class ModelSimSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(ModelSimSensor.class);

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
      reportPaths =
          Arrays.stream(reportPathsProperty.split(","))
              .map(String::trim)
              .collect(Collectors.toSet());
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
