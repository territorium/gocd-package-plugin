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

package cd.go.task.setup.handler;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import cd.go.task.util.RequestHandler;
import cd.go.task.util.Resources;

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
public class TemplateHandler implements RequestHandler {

  private String display;
  private String template;

  /**
   * Constructs an instance of {@link TemplateHandler}.
   *
   * @param display
   * @param template
   */
  public TemplateHandler(String display, String template) {
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
    object.add("displayValue", display);
    object.add("template", Resources.readString(template));
    return DefaultGoPluginApiResponse.success(object.build().toString());
  }
}