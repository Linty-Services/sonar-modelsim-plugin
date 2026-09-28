/*
 * Copyright (C) 2019-2026 Linty Services
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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.lintyservices.testharness.Orchestrator;
import com.sonar.orchestrator.junit5.OrchestratorExtension;
import com.sonar.orchestrator.locator.FileLocation;

class PluginTest {

  @RegisterExtension
  static final OrchestratorExtension ORCHESTRATOR = Orchestrator.createOrchestratorExtensionBuilder()
    .addPlugin(FileLocation.byWildcardMavenFilename(new File("../sonar-modelsim-plugin/target"), "sonar-modelsim-plugin-*.jar"))
    .build();

  @Test
  void sonar_modelsim_plugin_is_installed() {
    String body = ORCHESTRATOR.getServer()
      .newHttpCall("api/plugins/installed")
      .setAdminCredentials()
      .execute()
      .getBodyAsString();
    assertThat(body).contains("\"key\":\"modelsim\"");
  }
}
