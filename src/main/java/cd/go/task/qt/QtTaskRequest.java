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

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class QtTaskRequest extends TaskRequest {

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
  public static GoPluginApiResponse config() {
    HashMap<String, Object> requestBuild = new HashMap<>();
    requestBuild.put("default-value", "BUILD");
    requestBuild.put("display-order", "2");
    requestBuild.put("display-name", "Build");
    requestBuild.put("required", true);

    HashMap<String, Object> requestCommand = new HashMap<>();
    requestCommand.put("display-order", "1");
    requestCommand.put("display-name", "Command:");
    requestCommand.put("required", false);

    HashMap<String, Object> requestTarget = new HashMap<>();
    requestTarget.put("display-order", "1");
    requestTarget.put("display-name", "Target:");
    requestTarget.put("required", false);

    HashMap<String, Object> response = new LinkedHashMap<>();
    response.put(QtConfig.REQUEST_BUILD, requestBuild);
    response.put(QtConfig.REQUEST_COMMAND, requestCommand);
    response.put(QtConfig.REQUEST_TARGET, requestTarget);
    return DefaultGoPluginApiResponse.success(QtTaskRequest.GSON.toJson(response));
  }

  /**
   * Get the response for a "configuration" request.
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
   * 
   * @param request
   */
  public static GoPluginApiResponse validate(GoPluginApiRequest request) {
    // Map configMap = (Map) new
    // GsonBuilder().create().fromJson(request.requestBody(),
    // Object.class);

    HashMap<String, Object> response = new HashMap<>();
    response.put("errors", new HashMap<>());
    return DefaultGoPluginApiResponse.success(QtTaskRequest.GSON.toJson(response));
  }


  /**
   * Get the response for a "configuration" request.
   * 
   * A valid request body
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
   * 
   * A response of success
   * 
   * <pre>
   * {
   *   "success": true,
   *   "message": "Finished executing task"
   * }
   * </pre>
   * 
   * A response of failure
   * 
   * <pre>
   * {
   *   "success": false,
   *   "message": "Failed to execute task. The error was: 'Server not found'"
   * }
   * </pre>
   * 
   * @param request
   */
  @SuppressWarnings({ "rawtypes" })
  public static GoPluginApiResponse execute(GoPluginApiRequest request) {
    Map<?, ?> executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);

    QtTaskExecutor executor = new QtTaskExecutor();
    QtConfig config = QtConfig.of(executionRequest);
    TaskContext context = TaskContext.of(executionRequest);
    JobConsoleLogger logger = JobConsoleLogger.getConsoleLogger();

    return executor.execute(config, context, logger);
  }
}
