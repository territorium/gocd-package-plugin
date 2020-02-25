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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The {@link FileTreeMatcher} class.
 */
class FileTreeMatcher extends SimpleFileVisitor<Path> {

  private final Path                workingPath;
  private final Map<String, String> environment;


  private final Pattern           pattern;
  private final Set<String>       names;
  private final List<PathMatcher> mappers = new ArrayList<>();


  /**
   * Constructs an instance of {@link FileTreeMatcher}.
   *
   * @param workingDir
   * @param pattern
   */
  private FileTreeMatcher(File workingDir, Map<String, String> environment, String pattern) {
    this.workingPath = workingDir.toPath();
    this.environment = environment;
    this.pattern = Pattern.compile("^" + pattern + "$");
    this.names = Parameter.getGroupNames(pattern);
  }

  /**
   * Gets the list of {@link PathMatcher}.
   */
  public final List<PathMatcher> getMappers() {
    return mappers;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
    return visitPath(path, attrs);
  }

  @Override
  public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
    return visitPath(path, attrs);
  }

  private FileVisitResult visitPath(Path path, BasicFileAttributes attrs) {
    String input = workingPath.relativize(path).toString();
    Matcher matcher = pattern.matcher(input);
    if (matcher.find()) {
      Map<String, String> e = new HashMap<>(environment);
      e.putAll(Parameter.getParameters(matcher, names));
      mappers.add(new PathMatcher(path.toFile(), e));
      return FileVisitResult.SKIP_SUBTREE;
    }
    return FileVisitResult.CONTINUE;
  }

  /**
   * Copy the file tree using the environment variables.
   *
   * @param file
   * @param environment
   */
  public static List<PathMatcher> findFileTreeMatches(File file, Map<String, String> environment) throws IOException {
    // Split the file into workingPath and pattern
    Path path = file.toPath();
    Path workingPath = Paths.get(File.separator);
    Path patternPath = null;
    for (int i = 0; i < path.getNameCount(); i++) {
      Path p = path.getName(i);
      if (patternPath != null) {
        patternPath = patternPath.resolve(p);
      } else if (Pattern.compile(p.toString()).matcher("").groupCount() > 0) {
        patternPath = p;
      } else {
        workingPath = workingPath.resolve(p);
      }
    }

    FileTreeMatcher visitor = new FileTreeMatcher(workingPath.toFile(), environment, patternPath.toString());
    Files.walkFileTree(workingPath, visitor);
    return visitor.getMappers();
  }
}
