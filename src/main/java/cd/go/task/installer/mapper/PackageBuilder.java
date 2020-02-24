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

package cd.go.task.installer.mapper;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;

import cd.go.task.installer.Packages;

/**
 * The {@link PackageBuilder} class.
 */
public class PackageBuilder {

  private final Path targetPath;

  /**
   * Constructs an instance of {@link PackageBuilder} for the working directory.
   *
   * @param workingDir
   */
  private PackageBuilder(File workingDir) {
    targetPath = workingDir.toPath().resolve(Packages.BUILD).resolve(Packages.BUILD_PKG);
  }

  /**
   * Build a package structure for the {@link PackagesBuilder}. The method expects a source
   * file/folder, the {@link Version} information and the relative installation path.
   *
   * Update the meta/package.xml with the actual version and release date.
   *
   * @param module
   * @param source
   * @param version
   * @param relativePath
   */
  public void build(String module, File source, Version version, Path relativePath) throws Exception {
    // Copy the data to the build
    Path path = targetPath.resolve(module).resolve(Packages.DATA).resolve(relativePath);
    path.toFile().getParentFile().mkdirs();
    FileTreeCopying.copyFileTree(source.toPath(), path, Collections.emptyMap());

    // Change the package info
    PackageInfo info = new PackageInfo(targetPath.resolve(module));
    info.updatePackageInfo(version, LocalDate.now());
  }


  /**
   * Constructs an instance of {@link PackagesBuilder}.
   *
   * @param workingDir
   */
  public static PackageBuilder of(File workingDir) {
    return new PackageBuilder(workingDir);
  }
}
