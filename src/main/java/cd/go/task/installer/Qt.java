/*
 * Copyright (c) 2001-2019 Territorium Online Srl / TOL GmbH. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as defined in and that are
 * subject to the Territorium Online License Version 1.0. You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at http://www.tol.info/license/
 * and read it before using this file.
 *
 * The Original Code and all software distributed under the License are distributed on an 'AS IS'
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TERRITORIUM ONLINE HEREBY
 * DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the License for
 * the specific language governing rights and limitations under the License.
 */

package cd.go.task.installer;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

/**
 * The {@link Qt} class.
 */
public class Qt {

  private static final String QT_HOME = "QT_HOME";


  private final Map<String, String> environment;

  /**
   * Constructs an instance of {@link Qt}.
   *
   * @param environment
   */
  private Qt(Map<String, String> environment) {
    this.environment = environment;
  }

  /**
   * Constructs an instance of {@link Qt}.
   *
   * @param environment
   */
  public static Qt of(Map<String, String> environment) {
    return new Qt(environment);
  }

  /**
   * Get the Qt HOME directory
   */
  public final File getQtHome() {
    return new File(environment.get(Qt.QT_HOME));
  }

  /**
   * Get the Qt BASE directory
   */
  public final File getQtBase() {
    return getQtHome().getParentFile();
  }


  /**
   * Get the QtInstallerFramework binary
   */
  public final File getInstallerBin() {
    Path path = getQtBase().toPath().resolve("Tools").resolve("QtInstallerFramework");
    for (File file : path.toFile().listFiles()) {
      return path.resolve(file.getName()).resolve("bin").toFile();
    }
    return path.resolve("3.0").resolve("bin").toFile();
  }
}
