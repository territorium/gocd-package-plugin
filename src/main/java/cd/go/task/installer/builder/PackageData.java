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

package cd.go.task.installer.builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import cd.go.task.installer.Packages;
import cd.go.task.util.Environment;

/**
 * The {@link PackageData} provides information about the data/ folder of a package. The
 * {@link PackageData} uses the module name and the data file to generate the structure on the
 * target folder
 */
final class PackageData {

  private final String name;
  private final File   workingDir;
  private final String source;
  private final String target;

  /**
   * Constructs an instance of {@link PackageData}.
   *
   * @param name
   * @param data
   * @param target
   */
  public PackageData(String name, File workingDir, String source, String target) {
    this.name = name;
    this.workingDir = workingDir;
    this.source = source;
    this.target = target;
  }

  /**
   * Gets the package name.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Gets the source data file.
   */
  public final File getWorkingDir() {
    return this.workingDir;
  }

  /**
   * Gets the source location.
   */
  public final String getSource() {
    return this.source;
  }

  /**
   * Gets the target location.
   */
  public final String getTarget(Environment environment) {
    String basePath = environment.get(Packages.PACKAGE);
    String target = (this.target == null) ? "" : this.target;
    return (basePath == null) ? target : Paths.get(basePath, target).toString();
  }

  /**
   * Build the /data folder for the package.
   *
   * @param workingDir
   * @param environment
   */
  public final void build(File workingDir, Environment environment) throws IOException {
    Path workingPath = workingDir.toPath().resolve(getName()).resolve(Packages.DATA);
    for (PathMatcher matcher : PathMatcher.of(getWorkingDir(), environment, getSource())) {
      // Copy the data to the build
      Path path = workingPath.resolve(matcher.map(getTarget(environment)));
      path.toFile().getParentFile().mkdirs();
      FileTreeCopying.copyFileTree(matcher.getFile().toPath(), path);

      // Change the package info
      PackageInfo info = new PackageInfo(workingDir, matcher.getEnvironment());
      info.updatePackageInfo(getName());
    }
  }
}
