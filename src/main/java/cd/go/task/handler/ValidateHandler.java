/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package cd.go.task.handler;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import cd.go.task.util.RequestHandler;

/**
 * This message is sent by the GoCD server to the plugin to validate if the
 * settings entered by the user are valid, so that the server may persist those
 * settings in the cruise-config.xml file.
 * 
 * A valid request body
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
 * 
 * An error response body
 * 
 * <pre>
 * {
 *   "errors": {
 *     "URL": "URL is not well formed",
 *     "USERNAME": "Invalid character present"
 *   }
 * }
 * </pre>
 * 
 * An valid response body
 * 
 * <pre>
 * {
 *   "errors": {}
 * }
 * </pre>
 */
public class ValidateHandler implements RequestHandler {

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
