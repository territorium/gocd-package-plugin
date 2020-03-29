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

package cd.go.common.request;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import cd.go.common.util.Resources;

/**
 * This message is sent by the GoCD server to the plugin to get an AngularJS based HTML template to
 * allow the task to be configured.
 *
 * <pre>
 * {
 *   "displayValue": "MavenTask",
 *   "template": "<div class=\"form_item_block\">...</div>"
 * }
 * </pre>
 *
 * @param display
 * @param template
 */
public class ViewHandler implements RequestHandler {

  private static final String DISPLAY  = "displayValue";
  private static final String TEMPLATE = "template";


  private final String display;
  private final String template;

  /**
   * Constructs an instance of {@link ViewHandler}.
   *
   * @param display
   * @param template
   */
  public ViewHandler(String display, String template) {
    this.display = display;
    this.template = template;
  }

  /**
   * Handles a request and provides a response.
   *
   * @param request
   */
  @Override
  public GoPluginApiResponse handle(GoPluginApiRequest request) {
    JsonObjectBuilder object = Json.createObjectBuilder();
    object.add(ViewHandler.DISPLAY, display);
    object.add(ViewHandler.TEMPLATE, Resources.readString(template));
    return DefaultGoPluginApiResponse.success(object.build().toString());
  }
}