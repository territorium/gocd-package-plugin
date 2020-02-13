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

package cd.go.task.util.mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link PathBuilder} is an utility that get all files that match the input
 * pattern. The returned {@link PathBuilder}'s allow to replace the parameters
 * of an input with the parameters found on the matches.
 */
public class PathBuilder {

  private static final Pattern NAMES      = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z_0-9]*)>");
  private static final Pattern PARAMETERS = Pattern.compile("\\$([0-9]+|[a-zA-Z][a-zA-Z_0-9]*)");


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
   * Resolve the input pattern on the working directory, to find all matching
   * files.
   */
  public final List<Match> build(String pattern) throws IOException {
    PathVisitor visitor = new PathVisitor(workingDir, pattern);
    Files.walkFileTree(workingDir, visitor);
    return visitor.mappers;
  }

  /**
   * Resolve the input pattern on the working directory, to find all matching
   * files.
   *
   * @param workingDir
   */
  public static PathBuilder of(File workingDir) {
    return new PathBuilder(workingDir.toPath());
  }

  public class Match {

    private final File                file;
    private final Map<String, String> params;

    /**
     * Constructs an instance of {@link PathBuilder}.
     *
     * @param file
     * @param params
     */
    private Match(File file, Map<String, String> params) {
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
    public final String getParamater(String name) {
      return params.get(name);
    }

    /**
     * Gets the parameter by name.
     * 
     * @param name
     * @param params
     */
    protected final String getParameter(String name, Map<String, String> params) {
      return params.containsKey(name) ? params.get(name) : this.params.get(name);
    }

    /**
     * Map the input using the current instance.
     * 
     * @param input
     */
    public String map(String input) {
      return map(input, Collections.emptyMap());
    }

    /**
     * Replaces the indexed or named placeholder's with the the parameter
     * values. The indexed values are provided by the {@link PathBuilder} self,
     * the named values are provided from outside.
     *
     * @param input
     * @param params
     */
    public String map(String input, Map<String, String> params) {
      StringBuffer buffer = new StringBuffer();
      int offset = 0;

      Matcher matcher = PathBuilder.PARAMETERS.matcher(input);
      while (matcher.find()) {
        String value = getParameter(matcher.group(1), params);
        buffer.append(input.substring(offset, matcher.start(1) - 1));
        if (value != null) {
          buffer.append(value);
        }
        offset = matcher.end(1);
      }
      buffer.append(input.substring(offset, input.length()));
      return buffer.toString();
    }
  }

  private class PathVisitor extends SimpleFileVisitor<Path> {

    private final Pattern     pattern;
    private final Set<String> names;
    private final List<Match> mappers;

    /**
     * Constructs an instance of {@link PathVisitor}.
     *
     * @param workingDir
     * @param pattern
     */
    private PathVisitor(Path workingDir, String pattern) {
      this.pattern = Pattern.compile("^" + pattern + "$");
      this.names = getGroupNames(pattern);
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
        mappers.add(new Match(path.toFile(), getParameters(matcher, names)));
        return FileVisitResult.SKIP_SUBTREE;
      }
      return FileVisitResult.CONTINUE;
    }
  }

  /**
   * Parses the group names from the pattern.
   *
   * @param pattern
   */
  public static Set<String> getGroupNames(String pattern) {
    Set<String> names = new HashSet<>();
    Matcher matcher = NAMES.matcher(pattern);
    while (matcher.find())
      names.add(matcher.group(1));
    return names;
  }

  /**
   * Get the indexed parameters from the matcher.
   *
   * @param matcher
   * @param names
   */
  private static Map<String, String> getParameters(Matcher matcher, Set<String> names) {
    Map<String, String> params = new HashMap<>();
    params.put(Integer.toString(0), matcher.group(0));
    for (int index = 0; index < matcher.groupCount(); index++) {
      params.put(Integer.toString(index + 1), matcher.group(index + 1));
    }
    for (String name : names) {
      params.put(name, matcher.group(name));
    }
    return params;
  }
}