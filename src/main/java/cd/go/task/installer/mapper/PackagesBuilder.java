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

package cd.go.task.installer.mapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import cd.go.task.installer.Packages;

/**
 * The {@link PackagesBuilder} is an utility to create a package for the Qt
 * installer.
 */
public class PackagesBuilder {

  private final Path                workingPath;
  private final Map<String, String> environment;

  /**
   * Constructs an instance of {@link PackagesBuilder}.
   * 
   * @param workingDir
   * @param environment
   */
  public PackagesBuilder(File workingDir, Map<String, String> environment) {
    this.workingPath = workingDir.toPath();
    this.environment = environment;
  }

  /**
   * Build a package structure for the {@link PackagesBuilder}. The method
   * expects a source file/folder, the {@link Version} information and the
   * relative installation path.
   * 
   * @param packageSource
   */
  public void build(String packageSource) throws IOException {
    Path sourcePath = workingPath.resolve(packageSource);
    Path targetPath = workingPath.resolve(Packages.BUILD).resolve(Packages.BUILD_PKG);

    // Create the packages folder for the build
    File target = targetPath.toFile();
    target.mkdirs();
    FileTreeCopying.copyFileTree(sourcePath, targetPath, environment);

    // Update the meta files
    for (File m : targetPath.toFile().listFiles()) {
      for (File file : new File(m, Packages.META).listFiles()) {
        String data = new String(Files.readAllBytes(file.toPath()));
        try (Writer writer = new FileWriter(file)) {
          writer.write(Parameter.replaceAll(data, environment));
        }
      }
    }
  }

  /**
   * Constructs an instance of {@link PackagesBuilder}.
   * 
   * @param workingDir
   * @param environment
   */
  public static PackagesBuilder of(File workingDir, Map<String, String> environment) {
    return new PackagesBuilder(workingDir, environment);
  }
}
