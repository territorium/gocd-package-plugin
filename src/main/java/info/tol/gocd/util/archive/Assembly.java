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

package info.tol.gocd.util.archive;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link Assembly} class.
 */
public class Assembly {

  private static final Pattern PATTERN = Pattern.compile("^([^\\{]+)(?:\\{(.+)\\})?$");

  private final File           workingDir;


  private File               archive;
  private final List<String> patterns = new ArrayList<>();

  /**
   * Constructs an instance of {@link Assembly}.
   *
   * @param workingDir
   */
  private Assembly(File workingDir) {
    this.workingDir = workingDir;
  }

  /**
   * Set the archive
   *
   * @param archive
   */
  public final Assembly setArchive(File archive) {
    this.archive = archive;
    return this;
  }

  /**
   * Set the archive
   *
   * @param archive
   */
  public final Assembly addPattern(String pattern) {
    this.patterns.add(pattern);
    return this;
  }

  /**
   * Build the archive
   *
   */
  public final void build(Consumer<String> consumer) throws IOException {
    try (ArchiveBuilder builder = Archive.builder(this.archive)) {
      for (String input : this.patterns) {
        Matcher matcher = Assembly.PATTERN.matcher(input);
        if (matcher.find()) {
          File file = new File(this.workingDir, matcher.group(1));
          if (!file.exists()) {
            throw new IOException("File '" + matcher.group(1) + "' does not exist");
          }

          if (matcher.group(2) != null) {
            builder.addFile(file, matcher.group(2));
          } else if (file.isDirectory()) {
            builder.addDirectory(file);
          } else {
            builder.addFile(file.getParentFile(), file);
          }
        } else {
          throw new IOException("Couldn't find pattern '" + input + "'");
        }
      }
    }
  }

  /**
   * Constructs an instance of {@link Assembly}.
   *
   * @param workingDir
   */
  public static Assembly of(File workingDir) {
    return new Assembly(workingDir);
  }
}
