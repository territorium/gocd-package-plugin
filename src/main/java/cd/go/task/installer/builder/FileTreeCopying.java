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
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * The {@link FileTreeCopying} copies a directory structure from source to the target path.
 */
final class FileTreeCopying extends SimpleFileVisitor<Path> {

  private final Path source;
  private final Path target;


  private Instant instant;

  /**
   *
   * Constructs an instance of {@link FileTreeCopying}.
   *
   * @param source
   * @param target
   */
  private FileTreeCopying(Path source, Path target) {
    this.source = source;
    this.target = target;
  }

  /**
   * Resolves the path.
   *
   * @param path
   */
  private final Path toPath(Path path) {
    return this.target.resolve(this.source.relativize(path));
  }

  /**
   * Resolves the path.
   *
   * @param path
   */
  private final Instant toInstant(Path path) {
    try {
      BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
      return attr.creationTime().toInstant();
    } catch (Throwable ex) {}
    return null;
  }

  /**
   * Visit a directory.
   *
   * @param path
   * @param attrs
   */
  @Override
  public final FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
    Path dir = toPath(path);
    if (!Files.exists(dir)) {
      Files.createDirectory(dir);
    }
    return FileVisitResult.CONTINUE;
  }

  /**
   * Visit a file.
   *
   * @param path
   * @param attrs
   */
  @Override
  public final FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
    Files.copy(path, toPath(path), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES,
        LinkOption.NOFOLLOW_LINKS);

    Instant current = toInstant(path);
    if ((this.instant == null) || current.isAfter(this.instant)) {
      this.instant = current;
    }
    return FileVisitResult.CONTINUE;
  }


  /**
   * Copy the file tree using the environment variables.
   *
   * @param source
   * @param target
   * @param environment
   */
  public static LocalDate copyFileTree(Path source, Path target) throws IOException {
    FileTreeCopying visitor = new FileTreeCopying(source, target);
    Files.walkFileTree(source, visitor);
    return visitor.instant == null ? LocalDate.now()
        : LocalDateTime.ofInstant(visitor.instant, ZoneOffset.UTC).toLocalDate();
  }
}