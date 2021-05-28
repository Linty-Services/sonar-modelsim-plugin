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

import com.google.common.collect.ImmutableList;
import org.sonar.api.CoreProperties;
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public final class ModelSimPlugin implements Plugin {

  public static final String REPORT_PATH = "sonar.modelsim.reportPath";

  public static final String ADDITIONAL_REPORT_TYPE = "sonar.modelsim.additionalReportType";

  private static final String SUB_CATEGORY = "ModelSim";

  public List<Object> getExtensions() {
    return ImmutableList.of(
      PropertyDefinition.builder(REPORT_PATH)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory(SUB_CATEGORY)
        .name("Report Path")
        .description("Path (absolute or relative) to ModelSim XML report file.")
        .defaultValue("report.xml")
        .onQualifiers(Qualifiers.PROJECT)
        .build(),
      PropertyDefinition.builder(ADDITIONAL_REPORT_TYPE)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory(SUB_CATEGORY)
        .name("Additional Coverage Type")
        .description("Statement coverage is always imported. Choose additional coverage to import on top of it: 'branch' or 'condition'. "
          + "This additional coverage is imported as 'condition coverage' on the SonarQube web interface.")
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
