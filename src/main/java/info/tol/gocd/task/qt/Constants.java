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

package info.tol.gocd.task.qt;

import java.io.File;

import info.tol.gocd.task.qt.builder.Version;
import info.tol.gocd.util.Environment;

/**
 * The {@link Constants} class.
 */
public interface Constants {

  String ENV_VERSION     = "VERSION";

  String ENV_RELEASE     = "RELEASE";
  String ENV_PACKAGE     = "PACKAGE";
  String ENV_MODULE      = "MODULE";

  String PATH_PACKAGE    = String.join(File.separator, "build", "packages");
  String PATH_REPOSITORY = String.join(File.separator, "build", "repository");


  /**
   * This is a very dirty implementation to replace the module names of the module pathes and the
   * release/package variables in package.xml
   *
   * @param environment
   */
  public static Environment updateEnvironment(Environment environment) {
    Environment e = environment.clone();
    if (e.isSet(Constants.ENV_RELEASE)) {
      Version version = Version.parse(e.get(Constants.ENV_RELEASE));
      e.set(Constants.ENV_RELEASE, version.toString("00.00"));
      e.set(Constants.ENV_PACKAGE, version.toString("00.00-0"));
      e.set(Constants.ENV_MODULE, version.toString("00.00-0").replaceAll("[.-]", ""));
    }
    return e;
  }
}
