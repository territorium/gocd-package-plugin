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

package cd.go.task.qt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Base64;

public abstract class TaskRequest {

  protected static final Gson GSON = new GsonBuilder().serializeNulls().create();


  public static final String REQUEST_ICON     = "icon";
  public static final String REQUEST_VIEW     = "view";
  public static final String REQUEST_CONFIG   = "configuration";
  public static final String REQUEST_VALIDATE = "validate";
  public static final String REQUEST_EXECUTE  = "execute";


  /**
   * Get the response for a icon request.
   * 
   * <pre>
   * {
   *   "displayValue": "MavenTask",
   *   "template": "<div class=\"form_item_block\">...</div>"
   * }
   * </pre>
   *
   * @param type
   * @param icon
   */
  public static GoPluginApiResponse icon(String type, String icon) {
    JsonObject response = new JsonObject();
    response.addProperty("content_type", type);
    response.addProperty("data", Base64.getEncoder().encodeToString(Util.readResourceBytes(icon)));
    return DefaultGoPluginApiResponse.success(TaskRequest.GSON.toJson(response));
  }

  /**
   * Get the response for a "view" request.
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
  public static GoPluginApiResponse view(String display, String template) {
    JsonObject response = new JsonObject();
    response.addProperty("displayValue", display);
    response.addProperty("template", Util.readResource(template));
    return DefaultGoPluginApiResponse.success(TaskRequest.GSON.toJson(response));
  }
}
