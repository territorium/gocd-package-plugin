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

package cd.go.task.installer.mapper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link Parameter} class.
 */
abstract class Parameter {

  private static final Pattern PARAM = Pattern.compile("\\$([a-z0-9_]+)", Pattern.CASE_INSENSITIVE);

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
    Matcher matcher = PARAM.matcher(input);
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
    return Paths.get(replaceAll(path.toString(), environment));
  }
}
