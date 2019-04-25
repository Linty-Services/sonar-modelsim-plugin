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

import static org.mockito.Matchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ModelsimSensorTest {

  private ModelsimSensor sensor;
  
  private MapSettings settings;
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
    Configuration configuration=new MapSettings().asConfig();
    settings = new MapSettings();
    sensor = new ModelsimSensor(fs, pathResolver, settings, configuration);

    when(context.fileSystem()).thenReturn(fs);
    when(fs.predicates()).thenReturn(predicates);
    when(inputFile.file()).thenReturn(file);
    when(predicates.is(file)).thenReturn(predicate);
    when(fs.inputFile(predicate)).thenReturn(inputFile);

    when(context.newCoverage()).thenReturn(newCoverage);
  }

  @Test
  public void shouldNotFailIfReportNotSpecifiedOrNotFound() throws URISyntaxException {
    when(pathResolver.relativeFile(any(File.class), anyString()))
            .thenReturn(new File("notFound.xml"));

    settings.setProperty(ModelsimPlugin.MODELSIM_REPORT_PATH_PROPERTY, "notFound.xml");
    sensor.execute(context);


    File report = getCoverageReport();
    settings.setProperty(ModelsimPlugin.MODELSIM_REPORT_PATH_PROPERTY, report.getParent());
    when(pathResolver.relativeFile(any(File.class), anyString()))
            .thenReturn(report.getParentFile().getParentFile());
    sensor.execute(context);
  }

  @Test
  public void collectFileLineCoverage() throws URISyntaxException {
	 
	when(context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(anyString()))).thenReturn(inputFile);
	sensor.parseReport(getCoverageReport(), context);
    verify(context, times(2)).newCoverage();
    verify(newCoverage, times(2)).onFile(inputFile);
    verify(newCoverage).lineHits(31,4961097);
    verify(newCoverage).lineHits(35,3307396);
    verify(newCoverage, times(2)).save();
  }


  @Test
  public void testDoNotSaveMeasureOnResourceWhichDoesntExistInTheContext() throws URISyntaxException {
    when(fs.inputFile(predicate)).thenReturn(null);
    sensor.parseReport(getCoverageReport(), context);
    verify(context, never()).newCoverage();
  }

  @Test
  public void vhdlFileHasNoCoverageSoAddedAFakeOneToShowAsCovered() throws URISyntaxException {
	File nullCoverage =  new File(getClass().getResource("/org/sonar/plugins/modelsim/ModelsimSensorTest/null-coverage.xml").toURI());
	when(context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(anyString()))).thenReturn(inputFile);
	sensor.parseReport(nullCoverage, context);
    verify(newCoverage, times(1)).onFile(inputFile);
    verify(newCoverage).lineHits(1,1);
    verify(newCoverage, times(1)).save();
  }

  private File getCoverageReport() throws URISyntaxException {
    return new File(getClass().getResource("/org/sonar/plugins/modelsim/ModelsimSensorTest/commons-chain-coverage.xml").toURI());
  }

  @Test
  public void shouldExecuteOnlyOnVhdlFiles() {
    SensorDescriptor descriptor = mock(SensorDescriptor.class);
    when(descriptor.onlyOnLanguage(anyString())).thenReturn(descriptor);
    when(descriptor.onlyOnFileType(any(Type.class))).thenReturn(descriptor);
    sensor.describe(descriptor );

    verify(descriptor).onlyOnLanguage("vhdl");
    verify(descriptor).name("ModelsimSensor");
    verifyNoMoreInteractions(descriptor);
  }
  
  @Test (expected = IllegalStateException.class)
  public void testInvalidXml() throws URISyntaxException {
	File badXml =  new File(getClass().getResource("/org/sonar/plugins/modelsim/ModelsimSensorTest/badFile.xml").toURI());
	sensor.parseReport(badXml, context);
  }

}
