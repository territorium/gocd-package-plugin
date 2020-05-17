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

package info.tol.gocd.test;

import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import info.tol.gocd.util.Environment;

/**
 * The {@link TaskRequestBuilder} class.
 */
public class TaskRequestBuilder {

  private final File                workingDir;
  private final Environment         environment;

  private final Map<String, String> config = new HashMap<>();

  /**
   * Constructs an instance of {@link TaskRequestBuilder}.
   *
   * @param workingDir
   * @param environment
   */
  private TaskRequestBuilder(File workingDir, Environment environment) {
    this.workingDir = workingDir;
    this.environment = environment;
  }

  /**
   * Add a new configuration entry.
   *
   * @param name
   * @param value
   */
  public TaskRequestBuilder set(String name, String value) {
    config.put(name, value);
    return this;
  }

  /**
   * Creates an {@link GoPluginApiRequest}.
   */
  public final GoPluginApiRequest build() {
    JsonObjectBuilder env = Json.createObjectBuilder();
    environment.toMap().forEach((k, v) -> env.add(k, v));

    JsonObjectBuilder context = Json.createObjectBuilder();
    context.add("workingDirectory", workingDir.getAbsolutePath());
    context.add("environmentVariables", env);

    JsonObjectBuilder config = Json.createObjectBuilder();
    this.config.forEach((k, v) -> config.add(k, Json.createObjectBuilder().add("value", v)));

    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("config", config);
    builder.add("context", context);

    DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("", "", "");
    request.setRequestBody(builder.build().toString());
    return request;
  }

  /**
   *
   * @param workingDir
   * @param environment
   */
  public static TaskRequestBuilder of(File workingDir, Environment environment) {
    return new TaskRequestBuilder(workingDir, environment);
  }
}
