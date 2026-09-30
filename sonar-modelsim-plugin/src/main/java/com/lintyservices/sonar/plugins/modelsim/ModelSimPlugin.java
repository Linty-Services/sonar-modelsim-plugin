/*
 * This confidential and proprietary software may be used only as authorized
 * by a licensing agreement from Linty Services.
 * (c) Copyright 2016-2026 Linty Services
 * ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorized copies.
 */
package com.lintyservices.sonar.plugins.modelsim;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sonar.api.CoreProperties;
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;

public final class ModelSimPlugin implements Plugin {

  public static final String REPORT_PATHS = "sonar.modelsim.reportPaths";

  public static final String ADDITIONAL_REPORT_TYPE = "sonar.modelsim.additionalReportType";

  private static final String SUB_CATEGORY = "ModelSim";

  public List<Object> getExtensions() {
    return ImmutableList.of(
        PropertyDefinition.builder(REPORT_PATHS)
            .category(CoreProperties.CATEGORY_CODE_COVERAGE)
            .subCategory(SUB_CATEGORY)
            .name("Report Paths")
            .description(
                "Comma-separated list of paths (either files or directories) to ModelSim XML report files.\n"
                    + "If the list contains a directory, all .xml files in this directory will be considered as ModelSim XML reports.")
            .onConfigScopes(PropertyDefinition.ConfigScope.PROJECT)
            .build(),
        PropertyDefinition.builder(ADDITIONAL_REPORT_TYPE)
            .category(CoreProperties.CATEGORY_CODE_COVERAGE)
            .subCategory(SUB_CATEGORY)
            .name("Additional Coverage Type")
            .description(
                "Statement coverage is always imported. Choose additional coverage to import on top of it: 'branch' or 'condition'. "
                    + "This additional coverage is imported as 'condition coverage' on the SonarQube web interface.")
            .defaultValue("branch")
            .onConfigScopes(PropertyDefinition.ConfigScope.PROJECT)
            .build(),
        ModelSimSensor.class);
  }

  @Override
  public void define(Context context) {
    context.addExtensions(getExtensions());
  }
}
