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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import cd.go.common.util.Environment;

/**
 * The {@link FileTreeCopying} copies a directory structure from source to the target path.
 */
final class FileTreeCopying extends SimpleFileVisitor<Path> {

  private final Path        source;
  private final Path        target;
  private final Environment environment;

  /**
   *
   * Constructs an instance of {@link FileTreeCopying}.
   *
   * @param source
   * @param target
   * @param environment
   * @param cleanPath
   */
  private FileTreeCopying(Path source, Path target, Environment environment) {
    this.source = source;
    this.target = target;
    this.environment = environment;
  }

  private final Path toPath(Path path) {
    Path relativePath = source.relativize(path);
    String pathName = environment.replace(relativePath.toString());
    return target.resolve(Paths.get(pathName));
  }

  @Override
  public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
    Path dir = toPath(path);
    if (!Files.exists(dir)) {
      Files.createDirectory(dir);
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
    Files.copy(path, toPath(path), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    return FileVisitResult.CONTINUE;
  }

  /**
   * Copy the file tree using the environment variables.
   *
   * @param source
   * @param target
   * @param environment
   */
  public static void copyFileTree(Path source, Path target) throws IOException {
    copyFileTree(source, target, Environment.empty());
  }

  /**
   * Copy the file tree using the environment variables.
   *
   * @param source
   * @param target
   * @param environment
   */
  public static void copyFileTree(Path source, Path target, Environment environment) throws IOException {
    Files.walkFileTree(source, new FileTreeCopying(source, target, environment));
  }
}
