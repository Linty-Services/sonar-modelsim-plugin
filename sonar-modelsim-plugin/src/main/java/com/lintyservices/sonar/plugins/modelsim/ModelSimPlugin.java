/*
 * SonarQube Linty ModelSim :: Plugin
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
package com.lintyservices.sonar.plugins.modelsim;

import com.google.common.collect.ImmutableList;
import org.sonar.api.CoreProperties;
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public final class ModelSimPlugin implements Plugin {

  public static final String MODELSIM_REPORT_PATH = "sonar.modelsim.reportPath";

  public static final String MODELSIM_REPORT_MODE = "sonar.modelsim.modelsimReportMode";

  private static final String MODELSIM_SUB_CATEGORY = "ModelSim";

  public List<Object> getExtensions() {
    return ImmutableList.of(
      PropertyDefinition.builder(MODELSIM_REPORT_PATH)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory(MODELSIM_SUB_CATEGORY)
        .name("Report path")
        .description("Path (absolute or relative) to ModelSim XML report file.")
        .defaultValue("report.txt.xml")
        .onQualifiers(Qualifiers.PROJECT)
        .build(),
      PropertyDefinition.builder(MODELSIM_REPORT_MODE)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory(MODELSIM_SUB_CATEGORY)
        .name("Coverage type")
        .description("Type of secondary coverage: 'branch' or 'condition' (branch coverage will still be reported as"
          + " condition coverage on the Sonarqube web interface). Statement coverage is always imported.")
        .defaultValue("branch")
        .onQualifiers(Qualifiers.PROJECT)
        .build(),
      ModelSimSensor.class);
  }

  @Override
  public void define(Context context) {
    context.addExtensions(getExtensions());
  }
}
