/*
 * Linty ModelSim / QuestaSim :: Integration Tests
 * Copyright (C) 2019-2023 Linty Services
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
package com.lintyservices.sonar.plugins.modelsim.its;

import com.sonar.orchestrator.build.SonarScannerInstaller;
import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.version.Version;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.nio.file.Path;

public class InitOrchestratorExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

  public static OrchestratorExtension ORCHESTRATOR;
  private static final String SONAR_SCANNER_VERSION = "5.0.1.3006";

  @BeforeAll
  public void beforeAll(ExtensionContext context) {
    initOrchestrator();
    installScanner(ORCHESTRATOR, "target");
  }

  @Override
  public void close() {
    ORCHESTRATOR.stop();
  }

  private static void initOrchestrator() {
    ORCHESTRATOR = OrchestratorExtension
      .builderEnv()
      .useDefaultAdminCredentialsForBuilds(true)
      .setSonarVersion(System.getProperty("sonar.runtimeVersion", "LATEST_RELEASE"))
      .setOrchestratorProperty("orchestrator.artifactory.url", "https://repo1.maven.org/maven2")
      .addPlugin(FileLocation.byWildcardMavenFilename(
        new File("../sonar-modelsim-plugin/target"),
        "sonar-modelsim-plugin-*.jar")
      )
      .build();

    ORCHESTRATOR.start();
  }

  private static void installScanner(OrchestratorExtension orchestrator, String path) {
    new SonarScannerInstaller(orchestrator.getConfiguration().locators())
      .install(Version.create(SONAR_SCANNER_VERSION), null, Path.of(path).toFile(), false);
  }
}
