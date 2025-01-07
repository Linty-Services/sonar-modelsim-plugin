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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.scan.filesystem.PathResolver;

import java.io.File;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class ModelSimSensorTest {

  @Mock
  private SensorContext context;
  @Mock
  private PathResolver pathResolver;
  @Mock
  private InputFile inputFile;
  @Mock
  private File file;
  @Mock
  private FileSystem fs;
  @Mock
  private FilePredicates predicates;
  @Mock
  private FilePredicate predicate;
  @Mock
  private NewCoverage newCoverage;

  @BeforeEach
  void setUp() {
    initMocks(this);

    when(context.fileSystem()).thenReturn(fs);
    when(fs.predicates()).thenReturn(predicates);
    when(inputFile.file()).thenReturn(file);
    when(predicates.is(file)).thenReturn(predicate);
    when(fs.inputFile(predicate)).thenReturn(inputFile);
    when(context.newCoverage()).thenReturn(newCoverage);
  }

  @Test
  void do_not_fail_if_report_not_found() {
    ModelSimSensor sensor;

    Configuration configuration = new MapSettings().setProperty(ModelSimPlugin.REPORT_PATHS, "notFound.xml").asConfig();
    sensor = new ModelSimSensor(fs, pathResolver, configuration);
    when(pathResolver.relativeFile(any(File.class), anyString())).thenReturn(new File("notFound.xml"));
    sensor.execute(context);
  }

  @Test
  void do_not_fail_if_report_property_not_set() throws URISyntaxException {
    File report = getCoverageReport();
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    when(pathResolver.relativeFile(any(File.class), anyString())).thenReturn(report.getParentFile().getParentFile());
    sensor.execute(context);
  }

  @Test
  void collect_line_coverage() throws URISyntaxException {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    when(context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(anyString()))).thenReturn(inputFile);
    sensor.parseReport(getCoverageReport(), context, "branch");
    verify(context, times(2)).newCoverage();
    verify(newCoverage, times(2)).onFile(inputFile);
    verify(newCoverage).lineHits(31, 4961097);
    verify(newCoverage).lineHits(35, 3307396);
    verify(newCoverage, times(2)).save();
  }


  @Test
  void do_not_save_measure_on_files_that_do_not_exist() throws URISyntaxException {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    when(fs.inputFile(predicate)).thenReturn(null);
    sensor.parseReport(getCoverageReport(), context, "branch");
    verify(context, never()).newCoverage();
  }

  @Test
  void vhdlFileHasNoCoverageSoAddedAFakeOneToShowAsCovered() throws URISyntaxException {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    File nullCoverage = new File(getClass().getResource("/com/lintyservices/sonar/plugins/modelsim/ModelSimSensorTest/null-coverage.xml").toURI());
    when(context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(anyString()))).thenReturn(inputFile);
    sensor.parseReport(nullCoverage, context, "branch");
    verify(newCoverage, times(1)).onFile(inputFile);
    verify(newCoverage).lineHits(1, 1);
    verify(newCoverage, times(1)).save();
  }

  @Test
  void execute_on_vhdl_files_only() {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    SensorDescriptor descriptor = mock(SensorDescriptor.class);
    when(descriptor.onlyOnLanguage(anyString())).thenReturn(descriptor);
    when(descriptor.onlyOnFileType(any(Type.class))).thenReturn(descriptor);
    sensor.describe(descriptor);

    verify(descriptor).name("ModelSimSensor");
    verifyNoMoreInteractions(descriptor);
  }

  @Test
  void invalid_xml() {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());

    Exception thrown = Assertions.assertThrows(
      IllegalStateException.class,
      () -> {
        File badXml = new File(getClass().getResource("/com/lintyservices/sonar/plugins/modelsim/ModelSimSensorTest/badFile.xml").toURI());
        sensor.parseReport(badXml, context, "branch");
      });

    Assertions.assertEquals("XML is not valid", thrown.getMessage());
  }

  @Test
  void invalid_report() {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());

    Exception thrown = Assertions.assertThrows(
      IllegalStateException.class, () -> {
        File badReport = new File(getClass().getResource("/com/lintyservices/sonar/plugins/modelsim/ModelSimSensorTest/badFile.xml").toURI());
        when(context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(anyString()))).thenReturn(inputFile);
        sensor.parseReport(badReport, context, "branch");
      }
    );

    Assertions.assertEquals("XML is not valid", thrown.getMessage());
  }

  private File getCoverageReport() throws URISyntaxException {
    return new File(getClass().getResource("/com/lintyservices/sonar/plugins/modelsim/ModelSimSensorTest/commons-chain-coverage.xml").toURI());
  }
}
