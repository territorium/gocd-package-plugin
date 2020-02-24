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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link Parameter} class.
 */
abstract class Parameter {

  static final Pattern NAMES  = Pattern.compile("\\(\\?<([a-z][a-z_0-9]*)>", Pattern.CASE_INSENSITIVE);
  static final Pattern PARAMS = Pattern.compile("\\$([0-9]+|[a-z][a-z_0-9]*)", Pattern.CASE_INSENSITIVE);

  /**
   * Constructs an instance of {@link Parameter}.
   */
  private Parameter() {}

  /**
   * Replace all parameters that have values in the environment.
   *
   * @param input
   * @param environment
   */
  public static String replaceAll(String input, Map<String, String> environment) {
    String text = input;
    Matcher matcher = Parameter.PARAMS.matcher(input);
    while (matcher.find() && environment.containsKey(matcher.group(1))) {
      String key = matcher.group(1);
      text = text.replace("$" + key, environment.get(key));
    }
    return text;
  }

  /**
   * Replace all parameters that have values in the environment.
   *
   * @param path
   * @param environment
   */
  public static Path replaceAll(Path path, Map<String, String> environment) {
    return Paths.get(Parameter.replaceAll(path.toString(), environment));
  }

  /**
   * Parses the group names from the pattern.
   *
   * @param pattern
   */
  public static Set<String> getGroupNames(String pattern) {
    Set<String> names = new HashSet<>();
    Matcher matcher = Parameter.NAMES.matcher(pattern);
    while (matcher.find()) {
      names.add(matcher.group(1));
    }
    return names;
  }

  /**
   * Get the indexed parameters from the matcher.
   *
   * @param matcher
   * @param names
   */
  public static Map<String, String> getParameters(Matcher matcher, Set<String> names) {
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
