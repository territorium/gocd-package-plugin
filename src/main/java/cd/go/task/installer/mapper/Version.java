/*
 * Copyright (c) 2001-2020 Territorium Online Srl / TOL GmbH. All Rights Reserved.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link Version} implements the syntax of version. The version supports major, minor, patch
 * and build number. Major and minor are mandatory, patch and build are optional. All elements are
 * numbers, except the build number, which is a text without spaces.
 *
 * The {@link Version} expects following format: MAJOR.MINOR.PATCH-BUILD. e.g:
 *
 * <pre>
 *   19.12
 *   19.12.1
 *   19.12.1-rc1
 *   19.12-rc1
 *   19.04
 *   19.4
 * </pre>
 */
public final class Version implements Comparable<Version> {

  private static final Pattern PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:-([a-zA-Z0-9_\\-]+))?");
  private static final Pattern FORMAT  = Pattern.compile("([0]+)\\.([0]+)(?:\\.([0]+))?(?:-([0]+))?");


  private final int    major;
  private final int    minor;
  private final int    patch;
  private final String build;

  /**
   * Constructs an instance of {@link Version}.
   *
   * @param major
   * @param minor
   * @param patch
   * @param build
   */
  public Version(int major, int minor, int patch, String build) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.build = build;
  }

  /**
   * Gets the major number.
   */
  public final int getMajor() {
    return major;
  }

  /**
   * Gets the minor number.
   */
  public final int getMinor() {
    return minor;
  }

  /**
   * Gets the patch number.
   */
  public final int getPatch() {
    return patch;
  }

  /**
   * Gets the build text.
   */
  public final String getBuild() {
    return build;
  }

  /**
   * Compares this {@link Version} with the specified {@link Version} for order.
   *
   * @param other
   */
  @Override
  public final int compareTo(Version other) {
    if (getMajor() != other.getMajor()) { // Major version
      return getMajor() > other.getMajor() ? -1 : 1;
    } else if (getMinor() != other.getMinor()) { // Minor version
      return getMinor() > other.getMinor() ? -1 : 1;
    } else if (getPatch() != other.getPatch()) { // Patch version
      return getPatch() > other.getPatch() ? -1 : 1;
    }
    return 0;
  }

  /**
   * Returns a string representation of the version.
   */
  @Override
  public final String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(String.format("%s.%s", getMajor(), getMinor()));
    if (getPatch() > -1) {
      buffer.append(".");
      buffer.append(getPatch());
    }
    if (getBuild() != null) {
      buffer.append("-");
      buffer.append(getBuild());
    }
    return buffer.toString();
  }

  /**
   * Returns a string representation of the version, using the provided format.
   *
   * @param format
   */
  public final String toString(String format) {
    Matcher matcher = Version.FORMAT.matcher(format);
    if (!matcher.find()) {
      return toString();
    }

    StringBuffer buffer = new StringBuffer();
    String text = "%0" + matcher.group(1).length() + "d.%0" + matcher.group(2).length() + "d";
    buffer.append(String.format(text, getMajor(), getMinor()));
    if (matcher.group(3) != null) {
      text = ".%0" + matcher.group(3).length() + "d";
      buffer.append(String.format(text, getPatch() < 0 ? 0 : getPatch()));
    }
    if ((matcher.group(4) != null) && (getBuild() != null)) {
      buffer.append("-");
      buffer.append(getBuild());
    }
    return buffer.toString();
  }

  /**
   * Creates a new instance of {@link Version}
   *
   * @param major
   * @param minor
   */
  public static Version of(int major, int minor) {
    return Version.of(major, minor, -1, null);
  }

  /**
   * Creates a new instance of {@link Version}
   *
   * @param major
   * @param minor
   * @param patch
   */
  public static Version of(int major, int minor, int patch) {
    return Version.of(major, minor, patch, null);
  }

  /**
   * Creates a new instance of {@link Version}
   *
   * @param major
   * @param minor
   * @param build
   */
  public static Version of(int major, int minor, String build) {
    return Version.of(major, minor, -1, build);
  }

  /**
   * Creates a new instance of {@link Version}
   *
   * @param major
   * @param minor
   * @param patch
   * @param build
   */
  public static Version of(int major, int minor, int patch, String build) {
    return new Version(major, minor, patch, build);
  }

  /**
   * Parses a new instance of {@link Version}
   *
   * @param text
   */
  public static Version parse(String text) throws IllegalArgumentException {
    if (text == null) {
      return null;
    }

    Matcher matcher = Version.PATTERN.matcher(text);
    if (!matcher.find()) {
      throw new IllegalArgumentException("'" + text + "' is not a valid version");
    }

    int major = Integer.parseInt(matcher.group(1));
    int minor = Integer.parseInt(matcher.group(2));
    int patch = (matcher.group(3) == null) ? -1 : Integer.parseInt(matcher.group(3));
    String build = matcher.group(4);
    return Version.of(major, minor, patch, build);
  }
}