/*
 * SonarQube Linty ModelSim :: Plugin
 * Copyright (C) 2019-2021 Linty Services
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

import org.junit.Before;
import org.junit.Test;
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

import static org.junit.Assert.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ModelSimSensorTest {

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

  @Before
  public void setUp() {
    initMocks(this);

    when(context.fileSystem()).thenReturn(fs);
    when(fs.predicates()).thenReturn(predicates);
    when(inputFile.file()).thenReturn(file);
    when(predicates.is(file)).thenReturn(predicate);
    when(fs.inputFile(predicate)).thenReturn(inputFile);
    when(context.newCoverage()).thenReturn(newCoverage);
  }

  @Test
  public void shouldNotFailIfReportNotSpecifiedOrNotFound() throws URISyntaxException {
    ModelSimSensor sensor;

    Configuration configuration = new MapSettings().setProperty(ModelSimPlugin.REPORT_PATHS, "notFound.xml").asConfig();
    sensor = new ModelSimSensor(fs, pathResolver, configuration);
    when(pathResolver.relativeFile(any(File.class), anyString())).thenReturn(new File("notFound.xml"));
    sensor.execute(context);

    File report = getCoverageReport();
    sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    when(pathResolver.relativeFile(any(File.class), anyString())).thenReturn(report.getParentFile().getParentFile());
    sensor.execute(context);
  }

  @Test
  public void collectFileLineCoverage() throws URISyntaxException {
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
  public void testDoNotSaveMeasureOnResourceWhichDoesntExistInTheContext() throws URISyntaxException {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    when(fs.inputFile(predicate)).thenReturn(null);
    sensor.parseReport(getCoverageReport(), context, "branch");
    verify(context, never()).newCoverage();
  }

  @Test
  public void vhdlFileHasNoCoverageSoAddedAFakeOneToShowAsCovered() throws URISyntaxException {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    File nullCoverage = new File(getClass().getResource("/com/lintyservices/sonar/plugins/modelsim/ModelSimSensorTest/null-coverage.xml").toURI());
    when(context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(anyString()))).thenReturn(inputFile);
    sensor.parseReport(nullCoverage, context, "branch");
    verify(newCoverage, times(1)).onFile(inputFile);
    verify(newCoverage).lineHits(1, 1);
    verify(newCoverage, times(1)).save();
  }

  private File getCoverageReport() throws URISyntaxException {
    return new File(getClass().getResource("/com/lintyservices/sonar/plugins/modelsim/ModelSimSensorTest/commons-chain-coverage.xml").toURI());
  }

  @Test
  public void shouldExecuteOnlyOnVhdlFiles() {
    ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
    SensorDescriptor descriptor = mock(SensorDescriptor.class);
    when(descriptor.onlyOnLanguage(anyString())).thenReturn(descriptor);
    when(descriptor.onlyOnFileType(any(Type.class))).thenReturn(descriptor);
    sensor.describe(descriptor);

    verify(descriptor).name("ModelSimSensor");
    verifyNoMoreInteractions(descriptor);
  }

  @Test
  public void testInvalidXml() {
    Exception thrown = assertThrows(
      IllegalStateException.class,
      () -> {
        ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
        File badXml = new File(getClass().getResource("/com/lintyservices/sonar/plugins/modelsim/ModelSimSensorTest/badFile.xml").toURI());
        sensor.parseReport(badXml, context, "branch");
      });
  }

  @Test
  public void testInvalidReport() {
    Exception thrown = assertThrows(
      IllegalStateException.class, () -> {
        ModelSimSensor sensor = new ModelSimSensor(fs, pathResolver, new MapSettings().asConfig());
        File badReport = new File(getClass().getResource("/com/lintyservices/sonar/plugins/modelsim/ModelSimSensorTest/badFile.xml").toURI());
        when(context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(anyString()))).thenReturn(inputFile);
        sensor.parseReport(badReport, context, "branch");
      });
  }

}
