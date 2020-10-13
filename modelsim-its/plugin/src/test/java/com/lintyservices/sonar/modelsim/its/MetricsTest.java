/*
 * SonarQube Linty ModelSim :: Integration Tests :: Plugin
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
package com.lintyservices.sonar.modelsim.its;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;


public class MetricsTest {

  @ClassRule
  public static final Orchestrator orchestrator = Tests.ORCHESTRATOR;

  private static final String PROJECT_KEY = "coverage-modelsim-metrics";

  @BeforeClass
  public static void init() {
    SonarScanner build = Tests.createSonarScannerBuild()
      .setProjectDir(new File("projects/metrics/"))
      .setProjectKey(PROJECT_KEY)
      .setProjectName(PROJECT_KEY);

    Tests.resetData();
    orchestrator.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);
    // Tests.setProfile("empty-profile", PROJECT_KEY);
    // orchestrator.executeBuild(build);
  }

  @Test
  public void project_measures() {
    // Just check for now that the SonarQube with the ModelSim plugin starts
    Assert.assertTrue(true);
  }
}
