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
import java.util.Collections;
import java.util.Map;

import cd.go.task.installer.Packages;

/**
 * The {@link PackageData} provides information about the data/ folder of a package. The
 * {@link PackageData} uses the module name and the data file to generate the structure on the
 * target folder
 */
final class PackageData {

  private final String name;
  private final File   data;
  private final String target;

  /**
   * Constructs an instance of {@link PackageData}.
   *
   * @param name
   * @param data
   * @param target
   */
  public PackageData(String name, File data, String target) {
    this.name = name;
    this.data = data;
    this.target = target;
  }

  /**
   * Gets the package name.
   */
  public final String getName() {
    return name;
  }

  /**
   * Gets the source data file.
   */
  public final File getData() {
    return data;
  }

  /**
   * Gets the target location.
   */
  public final String getTarget() {
    return target;
  }

  public final void build(File workingDir, Map<String, String> environment) throws IOException {
    Path workingPath = workingDir.toPath().resolve(getName()).resolve(Packages.DATA);
    for (PathMatcher matcher : PathMatcher.of(getData(), environment)) {
      // Copy the data to the build
      Path path = workingPath.resolve(matcher.map(getTarget()));
      path.toFile().getParentFile().mkdirs();
      FileTreeCopying.copyFileTree(matcher.getFile().toPath(), path, Collections.emptyMap());

      // Change the package info
      PackageInfo info = new PackageInfo(workingDir, matcher.getEnvironment());
      info.updatePackageInfo(getName());
    }
  }
}
