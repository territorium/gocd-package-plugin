/*
 * Copyright (c) 2001-2019 Territorium Online Srl / TOL GmbH. All Rights
 * Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as
 * defined in and that are subject to the Territorium Online License Version
 * 1.0. You may not use this file except in compliance with the License. Please
 * obtain a copy of the License at http://www.tol.info/license/ and read it
 * before using this file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS
 * OR IMPLIED, AND TERRITORIUM ONLINE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the
 * License for the specific language governing rights and limitations under the
 * License.
 */

package cd.go.task.qt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@link Qt} class.
 */
public class Qt {

  public static final String MODE_BUILD       = "BUILD";
  public static final String MODE_TEST        = "TEST";

  public static final String QT_HOME          = "QT_HOME";
  public static final String QT_ARCH          = "QT_ARCH";
  public static final String QT_SPEC          = "QT_SPEC";
  public static final String QT_CONFIG        = "QT_CONFIG";

  public static final String QT_REPO          = "QT_REPO";
  public static final String QT_BUILD         = "QT_BUILD";
  public static final String QT_PLUGIN_PATH   = "QT_PLUGIN_PATH";
  public static final String QML2_IMPORT_PATH = "QML2_IMPORT_PATH";

  public static final String QT_REPOSITORY    = "QT_REPOSITORY";
  public static final String RELEASE          = "RELEASE";

  /**
   * Get the Qt Architecture.
   *
   * @param context
   * @param config
   */
  public static final String getArch(TaskContext context, QtConfig config) {
    // return ARCHS.get(config.getPlatform().toUpperCase());
    return context.getEnvironment().get(Qt.QT_ARCH);

  }

  /**
   * Get the Qt Specification.
   *
   * @param context
   * @param config
   */
  public static final String getSpec(TaskContext context, QtConfig config) {
    // return SPECS.get(config.getPlatform().toUpperCase());
    return context.getEnvironment().get(Qt.QT_SPEC);
  }

  /**
   * Get the Qt Specification.
   *
   * @param context
   * @param config
   */
  public static final List<String> getConfig(TaskContext context, QtConfig config) {
    List<String> configs = new ArrayList<>();
    String env = context.getEnvironment().get(Qt.QT_CONFIG);
    if (env != null && !env.trim().isEmpty()) {
      Arrays.asList(env.split(",")).forEach(c -> configs.add(c.trim()));
    }
    return configs;
  }
}
