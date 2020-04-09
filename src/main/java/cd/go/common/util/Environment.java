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

package cd.go.common.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link Environment} provides a mapping to environment variables. The {@link Environment}
 * allows to update text parts, containing parameters, with the environment variables.
 */
public class Environment {

  private static final Pattern NAMES  = Pattern.compile("\\(\\?<([a-z][a-z_0-9]*)>", Pattern.CASE_INSENSITIVE);
  private static final Pattern PARAMS = Pattern.compile("\\$([0-9]+|[a-z][a-z_0-9]*)", Pattern.CASE_INSENSITIVE);


  private final Map<String, String> environment;

  /**
   * Constructs an instance of {@link Environment}.
   */
  public Environment() {
    this(new HashMap<>());
  }

  /**
   * Constructs an instance of {@link Environment}.
   * 
   * @param environment
   */
  private Environment(Map<String, String> environment) {
    this.environment = environment;
  }

  /**
   * Set a new parameter to the {@link Environment}.
   */
  public final Map<String, String> toMap() {
    return this.environment;
  }

  /**
   * <code>true</code> if the parameter is set.
   *
   * @param name
   */
  public final boolean isSet(String name) {
    return environment.containsKey(name);
  }

  /**
   * Get a parameter by name.
   *
   * @param name
   */
  public final String get(String name) {
    return environment.get(name);
  }

  /**
   * Set a new parameter to the {@link Environment}.
   *
   * @param name
   * @param value
   */
  public final Environment set(String name, String value) {
    environment.put(name, value);
    return this;
  }

  /**
   * Replace all parameters that have values in the environment.
   *
   * @param input
   */
  public final String replaceModuleName(String input) {
    String text = input;
    Matcher matcher = Environment.PARAMS.matcher(input);
    while (matcher.find() && environment.containsKey(matcher.group(1))) {
      String key = matcher.group(1);
      String value = environment.get(key);
      text = text.replace("$" + key, value);
    }
    return text;
  }

  /**
   * Replaces the indexed or named placeholder's with the the parameter values.
   *
   * @param pattern
   */
  public final String replaceByPattern(String pattern) {
    StringBuffer buffer = new StringBuffer();
    int offset = 0;

    Matcher matcher = Environment.PARAMS.matcher(pattern);
    while (matcher.find()) {
      String name = matcher.group(1);
      String value = environment.get(name);
      buffer.append(pattern.substring(offset, matcher.start(1) - 1));
      if (value == null) {
        buffer.append("$" + name);
      } else {
        buffer.append(value);
      }
      offset = matcher.end(1);
    }
    buffer.append(pattern.substring(offset, pattern.length()));
    return buffer.toString();
  }

  /**
   * Parses the group names from the pattern.
   *
   * @param pattern
   */
  public static Set<String> getGroupNames(String pattern) {
    Set<String> names = new HashSet<>();
    Matcher matcher = Environment.NAMES.matcher(pattern);
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

  /**
   * Constructs an instance of {@link Environment}.
   */
  public final Environment clone() {
    return Environment.of(environment);
  }

  /**
   * Constructs an instance of {@link Environment}.
   * 
   * @param params
   */
  public final Environment clone(Map<String, String> params) {
    Environment environment = this.clone();
    environment.environment.putAll(params);
    return environment;
  }

  /**
   * Constructs an instance of {@link Environment}.
   * 
   * @param environment
   */
  public static Environment of(Map<String, String> environment) {
    return new Environment(new HashMap<>(environment));
  }

  /**
   * Constructs an empty instance of {@link Environment}.
   * 
   */
  public static Environment empty() {
    return new Environment(new HashMap<>());
  }
}
