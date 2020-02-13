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

package cd.go.task.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;

/**
 * The {@link PackageBuilder} is an utility to create a package for the Qt
 * installer.
 */
public class PackageBuilder {

  private File workingDir;

  /**
   * Constructs an instance of {@link PackageBuilder}.
   * 
   * @param workingDir
   */
  public PackageBuilder(File workingDir) {
    this.workingDir = workingDir;
  }

  /**
   * Build a package structure for the {@link PackageBuilder}. The method
   * expects a source file/folder, the {@link Version} information and the
   * relative installation path.
   * 
   * Update the meta/package.xml with the actual version and release date.
   *
   * @param source
   * @param version
   * @param relativePath
   */
  public void build(String name, File source, Version version, Path relativePath) throws Exception {
    Path path = workingDir.toPath().resolve(name).resolve("data").resolve(relativePath);
    path.toFile().getParentFile().mkdirs();
    Files.walkFileTree(source.toPath(), new CopyVisitor(source.toPath(), path));

    PackageInfo info = new PackageInfo(new File(workingDir, name));
    info.updatePackageInfo(version, LocalDate.now());
  }


  /**
   * The {@link CopyVisitor} copies a directory structure from source to the
   * target path.
   */
  public static class CopyVisitor extends SimpleFileVisitor<Path> {

    private final Path source;
    private final Path target;

    public CopyVisitor(Path source, Path target) {
      this.source = source;
      this.target = target;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
      Path dir = target.resolve(source.relativize(path));
      if (!Files.exists(dir)) {
        Files.createDirectory(dir);
      }
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
      Files.copy(path, target.resolve(source.relativize(path)), StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.COPY_ATTRIBUTES);
      return FileVisitResult.CONTINUE;
    }
  }
}
