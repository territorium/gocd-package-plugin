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
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link PathBuilder} is an utility that get all files that match the input pattern. The
 * returned {@link PathBuilder}'s allow to replace the parameters of an input with the parameters
 * found on the matches.
 */
public class PathBuilder {

  private final Path workingDir;

  /**
   * Constructs an instance of {@link PathVisitor}.
   *
   * @param workingDir
   */
  private PathBuilder(Path workingDir) {
    this.workingDir = workingDir;
  }

  /**
   * Resolve the input pattern on the working directory, to find all matching files.
   */
  public final List<PathMatcher> build(String pattern) throws IOException {
    PathVisitor visitor = new PathVisitor(workingDir, pattern);
    Files.walkFileTree(workingDir, visitor);
    return visitor.mappers;
  }

  /**
   * Resolve the input pattern on the working directory, to find all matching files.
   *
   * @param workingDir
   */
  public static PathBuilder of(File workingDir) {
    return new PathBuilder(workingDir.toPath());
  }

  public class PathMatcher {

    private final File                file;
    private final Map<String, String> params;

    /**
     * Constructs an instance of {@link PathBuilder}.
     *
     * @param file
     * @param params
     */
    private PathMatcher(File file, Map<String, String> params) {
      this.file = file;
      this.params = params;
    }

    /**
     * Gets the {@link File}.
     */
    public final File getFile() {
      return file;
    }

    /**
     * Gets the named parameter.
     *
     * @param name
     */
    public final String getParameter(String name) {
      return params.get(name);
    }

    /**
     * Replaces the indexed or named placeholder's with the the parameter values.
     *
     * @param input
     */
    public String map(String input) {
      StringBuffer buffer = new StringBuffer();
      int offset = 0;

      Matcher matcher = Parameter.PARAMS.matcher(input);
      while (matcher.find()) {
        String name = matcher.group(1);
        String value = getParameter(name);
        buffer.append(input.substring(offset, matcher.start(1) - 1));
        if (value == null) {
          buffer.append("$" + name);
        } else {
          buffer.append(value);
        }
        offset = matcher.end(1);
      }
      buffer.append(input.substring(offset, input.length()));
      return buffer.toString();
    }
  }

  private class PathVisitor extends SimpleFileVisitor<Path> {

    private final Pattern           pattern;
    private final Set<String>       names;
    private final List<PathMatcher> mappers;

    /**
     * Constructs an instance of {@link PathVisitor}.
     *
     * @param workingDir
     * @param pattern
     */
    private PathVisitor(Path workingDir, String pattern) {
      this.pattern = Pattern.compile("^" + pattern + "$");
      names = Parameter.getGroupNames(pattern);
      mappers = new ArrayList<>();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
      return visit(path);
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
      return visit(path);
    }

    protected FileVisitResult visit(Path path) {
      String input = workingDir.relativize(path).toString();
      Matcher matcher = pattern.matcher(input);
      if (matcher.find()) {
        mappers.add(new PathMatcher(path.toFile(), Parameter.getParameters(matcher, names)));
        return FileVisitResult.SKIP_SUBTREE;
      }
      return FileVisitResult.CONTINUE;
    }
  }
}
