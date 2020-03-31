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

import cd.go.common.util.Environment;
import cd.go.task.installer.builder.Version;

/**
 * The {@link Constants} class.
 */
public interface Constants {

  String ENV_PATTERN      = "PATTERN";
  String ENV_VERSION      = "VERSION";
  String ENV_RELEASE      = "RELEASE";
  String ENV_RELEASE_DATE = "RELEASE_DATE";


  String PATH_PACKAGE    = String.join(File.separator, "build", "packages");
  String PATH_REPOSITORY = String.join(File.separator, "build", "repository");


  public static void updateEnvironment(Environment environment) {
    Version version = Version.parse(environment.get(ENV_RELEASE));
    environment.set("RELEASE", version.toString("00.00"));
    environment.set("PACKAGE", version.toString("00.00-0"));
    environment.set("MODULE", version.toString("00.00-0").replaceAll("[.-]", ""));
    environment.set("PATTERN", version.getBuild() == null ? "00.00.0" : "00.00-0");
  }
}
