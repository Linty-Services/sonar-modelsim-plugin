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

/**
 * VHDL language implementation
 *
 * @since 1.3
 */
public class Vhdl  {

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

  private Vhdl(){}

}
