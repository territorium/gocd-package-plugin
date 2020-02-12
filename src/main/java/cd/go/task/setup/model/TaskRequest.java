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

package cd.go.task.setup.model;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;

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
public class TaskRequest {

  private String                    workingDirectory;

  private final TaskConfig          config      = new TaskConfig();
  private final Map<String, String> environment = new HashMap<>();

  /**
   * Gets the working directory.
   */
  public final String getWorkingDirectory() {
    return workingDirectory;
  }

  /**
   * Gets the environment variables
   */
  public final Map<String, String> getEnvironment() {
    return environment;
  }

  /**
   * Gets the environment variables
   */
  public final TaskConfig getConfig() {
    return config;
  }


  public final void parse(String text) {
    JsonObject json = Json.createReader(new StringReader(text)).readObject();
    JsonObject context = json.getJsonObject("context");
    JsonObject environment = context.getJsonObject("environmentVariables");

    this.config.parse(json.getJsonObject("config"));
    this.workingDirectory = context.getString("workingDirectory");

    for (String name : environment.keySet()) {
      this.environment.put(name, environment.getString(name));
    }
  }

  public static TaskRequest of(GoPluginApiRequest request) {
    TaskRequest taskRequest = new TaskRequest();
    taskRequest.parse(request.requestBody());
    return taskRequest;
  }


  public static void main(String... args) {
    String text =
        "{\"context\":{\"workingDirectory\":\"pipelines/smartIO-Installer-Develop\",\"environmentVariables\":{\"GO_MATERIAL_WEBAPP_HAS_CHANGED\":\"false\",\"GO_SERVER_URL\":\"https://cd:8154/go\",\"QT_REPOSITORY\":\"/data/QtRepository\",\"ANDROID_NDK_ROOT\":\"/opt/android/android-ndk-r18b\",\"GO_AGENT_RESOURCES\":\"Android,Java,Linux,Qt,Unix\",\"GO_PIPELINE_LABEL\":\"11\",\"ANDROID_SDK_ROOT\":\"/data/Software/Android/android-sdk-linux\",\"GO_STAGE_NAME\":\"package\",\"GO_DEPENDENCY_LOCATOR_WEBAPP\":\"smartIO-Web-Develop/73/build/1\",\"GO_PIPELINE_GROUP_NAME\":\"Tools-RC\",\"STORE_PASS\":\"******\",\"GO_PIPELINE_NAME\":\"smartIO-Installer-Develop\",\"QT_OVERLAY\":\"/data/QtBuild\",\"GO_STAGE_COUNTER\":\"1\",\"QT_HOME\":\"/data/Software/Qt/5.12.4\",\"GO_ENVIRONMENT_NAME\":\"Qt-5.12.4-Linux\",\"GO_PIPELINE_COUNTER\":\"11\",\"ANDROID_NDK_PLATFORM\":\"android-16\",\"GO_JOB_NAME\":\"package_prod\",\"GO_TRIGGER_USER\":\"cd\",\"GO_DEPENDENCY_LABEL_WEBAPP\":\"73\"}},\"config\":{\"Target\":{\"secure\":false,\"value\":\"cd\",\"required\":false},\"Command\":{\"secure\":false,\"value\":\"cd\",\"required\":false},\"Build\":{\"secure\":false,\"value\":\"BUILD\",\"required\":false}}}";
    TaskRequest taskRequest = new TaskRequest();
    taskRequest.parse(text);

    System.out.println();
  }
}