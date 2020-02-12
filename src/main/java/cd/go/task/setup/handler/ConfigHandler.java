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
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import cd.go.task.setup.model.ConfigResponse;
import cd.go.task.util.RequestHandler;


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
    config.setValue("Module", null, "Module Name", "1", true, false);
    return config.build();
  }

}
