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

package info.tol.gocd.task.qt.builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import info.tol.gocd.util.Environment;

/**
 * The {@link PathMatcher} is an utility that get all files that match the path pattern. The
 * returned {@link PathMatcher}'s allow to replace the parameters of the input with the parameters
 * found on the matches.
 */
class PathMatcher {

  private final File        file;
  private final Environment environment;

  /**
   * Constructs an instance of {@link PathMatcher}.
   *
   * @param file
   * @param environment
   */
  PathMatcher(File file, Environment environment) {
    this.file = file;
    this.environment = environment;
  }

  /**
   * Gets the {@link File}.
   */
  public final File getFile() {
    return this.file;
  }

  /**
   * Gets the named parameter.
   *
   * @param name
   */
  public final Environment getEnvironment() {
    return this.environment;
  }

  /**
   * Replaces the indexed or named placeholder's with the the parameter values.
   *
   * @param pattern
   */
  public final String map(String pattern) {
    return this.environment.replaceByPattern(pattern);
  }

  /**
   * Get the matching {@link Path} as string.
   */
  @Override
  public String toString() {
    return getFile().toString();
  }

  /**
   * Resolve the input pattern on the working directory, to find all matching files.
   *
   * @param workingPath
   */
  public static List<PathMatcher> of(File workingDir, Environment environment, String pattern) throws IOException {
    return FileTreeMatcher.findFileTreeMatches(workingDir, environment, pattern);
  }
}
