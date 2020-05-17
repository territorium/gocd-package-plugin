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
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.tol.gocd.util.Environment;
import info.tol.gocd.util.Version;
import info.tol.gocd.util.archive.Archive;

/**
 * The {@link PackageData} provides information about the data/ folder of a package. The
 * {@link PackageData} uses the module name and the data file to generate the structure on the
 * target folder
 */
final class PackageData {

  private static final Pattern ARCHIVES = Pattern.compile("(([^#]+)(?:\\.zip|\\.tar(?:\\.gz)?|\\.war))(?:[!#](.+))?");
  private static final Pattern REPLACER = Pattern.compile("^([^/]*)/([^/]+)/([^/]+)/(.*)$");
  private static final Pattern RELEASE  = Pattern.compile("(?:\\$\\{|\\{\\{\\$)RELEASE;([0.-]+)\\}\\}?");


  private final String      name;
  private final String      module;
  private final String      source;
  private final String      target;

  private final File        workingDir;
  private final Environment environment;

  /**
   * Constructs an instance of {@link PackageData}.
   *
   * @param name
   * @param source
   * @param target
   * @param builder
   */
  public PackageData(String name, String source, String target, PackageBuilder builder) {
    this.name = PackageData.toName(name, builder.getEnvironment());
    this.module = name;
    this.source = source;
    this.target = target;
    this.workingDir = builder.getWorkingDir();
    this.environment = builder.getEnvironment();
  }

  /**
   * Removes the pattern from the module name.
   *
   * @param name
   */
  private static String toName(String name, Environment environment) {
    Matcher m = PackageData.REPLACER.matcher(name);
    if (!m.find()) {
      return name;
    }

    String value = environment.replaceByPattern(m.group(3)).replaceAll("[.-]", "");
    return String.format("%s%s%s", m.group(1), value, m.group(4));
  }

  /**
   * Removes the pattern from the module name.
   *
   * @param data
   */
  public String remap(String data) {
    Matcher matcher = PackageData.REPLACER.matcher(this.module);
    if (!matcher.find()) {
      return data;
    }

    String pattern = String.format("%s(%s)", matcher.group(1).replace(".", "\\."), matcher.group(2));
    String value = this.environment.replaceByPattern(matcher.group(3));
    Version version = Version.parse(value);

    matcher = PackageData.RELEASE.matcher(data);
    if (matcher.find()) {
      data = matcher.replaceFirst(version.toString(matcher.group(1)));
    }

    data = this.environment.replaceByPattern(data);
    value = value.replaceAll("[.-]", "");

    int offset = 0;
    StringBuffer buffer = new StringBuffer();
    matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(data);
    while (matcher.find()) {
      buffer.append(data.substring(offset, matcher.start(1)));
      buffer.append(value);
      offset = matcher.end(1);
    }
    buffer.append(data.substring(offset, data.length()));
    return buffer.toString();
  }

  /**
   * Gets the package name.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Gets the source data file.
   */
  public final File getWorkingDir() {
    return this.workingDir;
  }

  /**
   * Gets the source location.
   */
  public final String getSource() {
    return this.source;
  }

  /**
   * Gets the target location.
   */
  public final String getTarget(String suffix) {
    String location = (this.target == null) ? "" : this.target;
    if (suffix != null)
      location = Paths.get(location, suffix).toString();
    return location;
  }

  /**
   * Build the /data folder for the package.
   *
   * @param workingDir
   * @param environment
   */
  public final void build(File workingDir, Environment environment) throws IOException {
    String sources = getSource();
    Environment env = new Environment();
    LocalDate releaseDate = null;

    for (String source : sources.split("\n")) {
      String suffix = null;
      if (source.contains(";")) {
        suffix = source.substring(source.indexOf(';') + 1);
        source = source.substring(0, source.indexOf(';'));
      }

      // Check archives that can be uncompressed (.zip, .tar, .tar.gz, .war)
      Matcher match = PackageData.ARCHIVES.matcher(source);
      if (match.find()) {
        for (PathMatcher matcher : PathMatcher.of(getWorkingDir(), environment, match.group(1))) {
          Archive.of(matcher.getFile()).extract();
        }
        source = match.group(2);
        if (match.group(3) != null) {
          source += "/" + match.group(3);
        }
      }

      Path workingPath = workingDir.toPath().resolve(getName()).resolve(PackageBuilder.DATA);
      for (PathMatcher matcher : PathMatcher.of(getWorkingDir(), environment, source)) {
        String target = getTarget(suffix);
        if (target.isEmpty() && !matcher.getFile().isDirectory()) {
          target = matcher.getFile().getName();
        }

        // Copy the data to the build
        Path path = workingPath.resolve(matcher.map(target));
        path.toFile().getParentFile().mkdirs();
        LocalDate date = FileTreeCopying.copyFileTree(matcher.getFile().toPath(), path);
        env.add(matcher.getEnvironment());

        if (releaseDate == null || releaseDate.isBefore(date))
          releaseDate = date;
      }
    }

    // TODO Release date should be calculated.
    if (releaseDate == null)
      releaseDate = LocalDate.now();

    // Change the package info
    PackageInfo info = new PackageInfo(env);
    info.updatePackageInfo(getName(), releaseDate, workingDir);

  }
}
