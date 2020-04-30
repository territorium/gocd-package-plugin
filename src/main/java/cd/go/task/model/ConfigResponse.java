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

package cd.go.task.model;

import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * Get the response for a "configuration" request.
 *
 * <pre>
 * {
 *   "url": {
 *     "default-value": "",
 *     "secure": false,
 *     "required": true
 *   },
 *   "user": {
 *     "default-value": "bob",
 *     "secure": true,
 *     "required": true
 *   }
 * }
 * </pre>
 */
public class ConfigResponse {

  private final Map<String, Value> values = new HashMap<>();

  /**
   * Set a new environment value.
   *
   * @param name
   * @param value
   */
  public final void setValue(String name, String value) {
    setValue(name, value, false);
  }

  /**
   * Set a new environment value.
   *
   * @param name
   * @param value
   * @param required
   */
  public final void setValue(String name, String value, boolean required) {
    this.values.put(name, new Value(value, null, null, required, false));
  }

  /**
   * Set a new environment value.
   *
   * @param name
   * @param value
   * @param required
   */
  public final void setValue(String name, String value, String display, String order, boolean required,
      boolean secure) {
    this.values.put(name, new Value(value, order, display, required, secure));
  }

  /**
   * Builds the instance as {@link GoPluginApiResponse}.
   */
  public final GoPluginApiResponse build() {
    JsonObjectBuilder object = Json.createObjectBuilder();
    for (String name : this.values.keySet()) {
      Value value = this.values.get(name);

      JsonObjectBuilder config = Json.createObjectBuilder();
      if (value.value != null) {
        config.add("default-value", value.value);
      }
      if (value.display != null) {
        config.add("display-name", value.display);
      }
      if (value.order != null) {
        config.add("display-order", value.order);
      }
      config.add("secure", value.secure);
      config.add("required", value.required);

      object.add(name, config);
    }
    return DefaultGoPluginApiResponse.success(object.build().toString());
  }

  /**
   * The {@link Value} class.
   */
  private class Value {

    private final String  value;
    private final String  order;
    private final String  display;
    private final boolean secure;
    private final boolean required;

    /**
     * Constructs an instance of {@link Value}.
     *
     * @param value
     * @param order
     * @param display
     * @param required
     * @param secure
     */
    private Value(String value, String order, String display, boolean required, boolean secure) {
      this.value = value;
      this.order = order;
      this.display = display;
      this.secure = secure;
      this.required = required;
    }
  }
}