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

package info.tol.gocd.task.util;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import java.io.StringReader;
import java.util.function.Supplier;

import javax.json.Json;
import javax.json.JsonObject;

import info.tol.gocd.util.Environment;

/**
 * Get the request for a task execution.
 *
 * <pre>
 * {
 *   "config": {
 *     "ftp_server": {
 *       "secure": false,
 *       "value": "ftp.example.com",
 *       "required": true
 *     },
 *     "remote_dir": {
 *       "secure": false,
 *       "value": "/pub/",
 *       "required": true
 *     }
 *   },
 *   "context": {
 *     "workingDirectory": "working-dir",
 *     "environmentVariables": {
 *       "ENV1": "VAL1",
 *       "ENV2": "VAL2"
 *     }
 *   }
 * }
 * </pre>
 */
public class TaskRequest<C extends TaskConfig> {

  private String            workingDir;
  private final Environment environment = new Environment();

  private C                 config;

  /**
   * Gets the working directory.
   */
  public final String getWorkingDir() {
    return this.workingDir;
  }

  /**
   * Gets the environment variables
   */
  public final Environment getEnvironment() {
    return this.environment;
  }

  /**
   * Gets the environment variables
   */
  public final C getConfig() {
    return this.config;
  }

  public static <C extends TaskConfig> TaskRequest<C> of(GoPluginApiRequest request, Supplier<C> supplier) {
    StringReader reader = new StringReader(request.requestBody());
    JsonObject json = Json.createReader(reader).readObject();
    JsonObject context = json.getJsonObject("context");
    JsonObject environment = context.getJsonObject("environmentVariables");

    TaskRequest<C> task = new TaskRequest<>();
    task.workingDir = context.getString("workingDirectory");
    for (String name : environment.keySet()) {
      task.environment.set(name, environment.getString(name));
    }

    task.config = supplier.get();
    task.config.parse(json.getJsonObject("config"));
    return task;
  }
}