/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package info.tol.gocd.task.qt.handler;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import info.tol.gocd.util.request.RequestHandler;

/**
 * This message is sent by the GoCD server to the plugin to validate if the settings entered by the
 * user are valid, so that the server may persist those settings in the cruise-config.xml file.
 *
 * A valid request body
 *
 * <pre>
 * {
 * "RegistryURL": "https://index.docker.io/v1/",
 * "Username": "boohoo"
 * }
 * </pre>
 *
 * An error response body
 *
 * <pre>
 * [
 *     {
 *         "key": "SCM_URL",
 *         "message": "SCM URL not specified"
 *     },
 *     {
 *         "key": "RANDOM",
 *         "message": "Unsupported key(s) found : RANDOM. Allowed key(s) are : SCM_URL, USERNAME, PASSWORD"
 *     }
 * ]
 * </pre>
 */
public class ValidateHandler implements RequestHandler {

  private final Map<String, Predicate<String>> rules    = new HashMap<>();
  private final Map<String, String>            messages = new HashMap<>();

  /**
   * Add an validation rule.
   *
   * @param target
   * @param predicate
   * @param message
   */
  public final ValidateHandler addRule(String target, Predicate<String> predicate, String message) {
    this.rules.put(target, predicate);
    this.messages.put(target, message);
    return this;
  }

  /**
   * Handles a request and provides a response.
   *
   * @param request
   */
  @Override
  public GoPluginApiResponse handle(GoPluginApiRequest request) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("errors", Json.createObjectBuilder());
    return DefaultGoPluginApiResponse.success(builder.build().toString());
  }
}
