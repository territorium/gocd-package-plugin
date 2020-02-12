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

package cd.go.task.setup.model;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Get the request for a task config.
 * 
 * <pre>
 * {
 *   "URL": {
 *     "secure": false,
 *     "value": "http://localhost.com",
 *     "required": true
 *   },
 *   "USERNAME": {
 *     "secure": false,
 *     "value": "user",
 *     "required": false
 *   },
 *   "PASSWORD": {
 *     "secure": true,
 *     "value": "password",
 *     "required": false
 *   }
 * }
 * </pre>
 */
public class TaskConfig {

  private final Map<String, Entry> config = new HashMap<>();


  /**
   * Get all names of the configuration
   */
  public final Set<String> getNames() {
    return config.keySet();
  }

  /**
   * Gets the configuration value
   */
  public final String getValue(String name) {
    return config.containsKey(name) ? config.get(name).value : null;
  }

  /**
   * Return <code>true</code> if the property should be secured.
   */
  public final boolean isSecure(String name) {
    return config.containsKey(name) ? config.get(name).secure : false;
  }

  /**
   * Return <code>true</code> if the property is required.
   */
  public final boolean isRequired(String name) {
    return config.containsKey(name) ? config.get(name).required : false;
  }

  /**
   * Parses the configuration from the text.
   *
   * @param text
   */
  public final void parse(String text) {
    parse(Json.createReader(new StringReader(text)).readObject());
  }

  /**
   * Parses the configuration from the text.
   *
   * @param text
   */
  public final void parse(JsonObject json) {
    for (String name : json.keySet()) {
      JsonObject config = json.getJsonObject(name);
      String value = config.getString("value");
      boolean secure = config.getBoolean("secure");
      boolean required = config.getBoolean("required");
      this.config.put(name, new Entry(value, secure, required));
    }
  }

  public static TaskConfig of(GoPluginApiRequest request) {
    TaskConfig taskRequest = new TaskConfig();
    taskRequest.parse(request.requestBody());
    return taskRequest;
  }

  /**
   * The {@link Entry} class.
   */
  private class Entry {

    private final String  value;
    private final boolean secure;
    private final boolean required;

    /**
     * Constructs an instance of {@link Entry}.
     *
     * @param value
     * @param secure
     * @param required
     */
    private Entry(String value, boolean secure, boolean required) {
      this.value = value;
      this.secure = secure;
      this.required = required;
    }
  }
}