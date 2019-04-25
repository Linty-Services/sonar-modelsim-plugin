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
package com.s2ceh.sonar.plugins.vhdl;

import org.sonar.api.config.Settings;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import org.sonar.api.resources.AbstractLanguage;

/**
 * VHDL language implementation
 *
 * @since 1.3
 */
public class Vhdl extends AbstractLanguage {

  /**
   * VHDL key
   */
  public static final String KEY = "vhdl";

  /**
   * VHDL name
   */
  public static final String NAME = "VHDL";

  /**
   * Key of the file suffix parameter
   */
  public static final String FILE_SUFFIXES_KEY = "sonar.vhdl.file.suffixes";

  /**
   * Default VHDL files knows suffixes
   */
  public static final String DEFAULT_FILE_SUFFIXES = ".vhdl,.vhd";

  /**
   * Settings of the plugin.
   */
  private final Settings settings;

  /**
   * Default constructor
   */
  public Vhdl(Settings settings) {
    super(KEY, NAME);
    this.settings = settings;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.sonar.api.resources.AbstractLanguage#getFileSuffixes()
   */
  @Override
  public String[] getFileSuffixes() {
    String[] suffixes = Arrays.stream(settings.getStringArray(Vhdl.FILE_SUFFIXES_KEY)).filter(s -> s != null && !s.trim().isEmpty()).toArray(String[]::new);
    if (suffixes.length == 0) {
      suffixes = Iterables.toArray(Splitter.on(',').split(DEFAULT_FILE_SUFFIXES), String.class);
    }
    return suffixes;
  }

}
