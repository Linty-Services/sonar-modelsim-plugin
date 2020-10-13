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

import com.google.common.collect.ImmutableList;
import org.sonar.api.CoreProperties;
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public final class ModelsimPlugin implements Plugin {

  public static final String MODELSIM_REPORT_PATH_PROPERTY = "sonar.modelsim.reportPath";

  public static final String MODELSIM_REPORT_MODE = "sonar.modelsim.modelsimReportMode";

  public List<Object> getExtensions() {
    return ImmutableList.of(
      PropertyDefinition.builder(MODELSIM_REPORT_PATH_PROPERTY)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory("Modelsim")
        .name("Report path")
        .description("Path (absolute or relative) to Modelsim xml report file.")
        .defaultValue("report.txt.xml")
        .onQualifiers(Qualifiers.PROJECT)
        .build(),
      PropertyDefinition.builder(MODELSIM_REPORT_MODE)
        .category(CoreProperties.CATEGORY_CODE_COVERAGE)
        .subCategory("Modelsim")
        .name("Coverage type")
        .description("Type of secondary coverage : branch or condition (branch coverage will still be reported as condition coverage by Sonarqube interface). Statement coverage is always imported")
        .defaultValue("branch")
        .onQualifiers(Qualifiers.PROJECT)
        .build(),
      ModelsimSensor.class);
  }

  @Override
  public void define(Context context) {
    context.addExtensions(getExtensions());
  }
}
