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
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import info.tol.gocd.task.util.ConfigResponse;
import info.tol.gocd.util.request.RequestHandler;

/**
 * This message is sent by the GoCD server to the plugin to know what properties are supported by
 * this plugin that should to be stored in the cruise-config.xml file.
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
 *   },
 *   "password": {}
 * }
 * </pre>
 */
public class ConfigHandler implements RequestHandler {

  /**
   * Handles a request and provides a response.
   *
   * @param request
   */
  @Override
  public GoPluginApiResponse handle(GoPluginApiRequest request) {
    ConfigResponse config = new ConfigResponse();
    config.setValue("name", "INIT", "Release Name", "1", true, false);
    config.setValue("path", null, "Package Path", "2", false, false);
    config.setValue("module", null, "Module Name", "3", true, false);
    config.setValue("source", null, "Sources", "4", true, false);
    config.setValue("target", null, "Target Path", "5", true, false);
    return config.build();
  }
}
