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

import org.junit.jupiter.api.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ModelSimPluginTest {

  public static final Version LTS_VERSION = Version.create(8, 9);
  private static final int EXTENSIONS = 3;

  @Test
  void should_contain_the_right_number_of_extensions() {
    Plugin.Context context = setupContext(SonarRuntimeImpl.forSonarQube(LTS_VERSION, SonarQubeSide.SERVER, SonarEdition.COMMUNITY));
    assertThat(context.getExtensions()).hasSize(EXTENSIONS);
  }

  @Test
  void should_contain_the_right_number_of_properties() {
    assertThat(properties()).hasSize(2);
  }

  @Test
  void should_have_modelsim_as_category_for_properties() {
    List<PropertyDefinition> properties = properties();
    assertThat(properties).isNotEmpty();
    for (PropertyDefinition propertyDefinition : properties) {
      if (propertyDefinition.key().contains("modelsim")) {
        assertThat(propertyDefinition.category()).isEqualTo("codeCoverage");
        assertThat(propertyDefinition.subCategory()).isEqualTo("ModelSim");
      }
    }
  }

  private List<PropertyDefinition> properties() {
    List extensions = setupContext(SonarRuntimeImpl.forSonarQube(LTS_VERSION, SonarQubeSide.SERVER, SonarEdition.COMMUNITY)).getExtensions();

    List<PropertyDefinition> propertiesList = new ArrayList<>();
    for (Object extension : extensions) {
      if (extension instanceof PropertyDefinition) {
        propertiesList.add((PropertyDefinition) extension);
      }
    }

    return propertiesList;
  }

  private Plugin.Context setupContext(SonarRuntime runtime) {
    Plugin.Context context = new Plugin.Context(runtime);
    new ModelSimPlugin().define(context);
    return context;
  }
}
