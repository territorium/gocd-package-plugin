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
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.File;
import java.util.Arrays;

import info.tol.gocd.task.qt.Constants;
import info.tol.gocd.task.qt.builder.PackageBuilder;
import info.tol.gocd.task.util.TaskRequest;
import info.tol.gocd.task.util.TaskResponse;
import info.tol.gocd.util.Environment;
import info.tol.gocd.util.request.RequestHandler;

/**
 * This message is sent by the GoCD agent to the plugin to execute the task.
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
public class TaskHandler implements RequestHandler {

  private final JobConsoleLogger console;

  /**
   * Constructs an instance of {@link TaskHandler}.
   *
   * @param console
   */
  public TaskHandler(JobConsoleLogger console) {
    this.console = console;
  }

  /**
   * Handles a request and provides a response.
   *
   * @param request
   */
  @Override
  public GoPluginApiResponse handle(GoPluginApiRequest request) {
    TaskRequest task = TaskRequest.of(request);
    String release = task.getConfig().getValue("name");
    String config = task.getConfig().getValue("module");
    String source = task.getConfig().getValue("source");
    String target = task.getConfig().getValue("target");
    String packagePath = task.getConfig().getValue("path");

    Environment env = task.getEnvironment();
    env.set(Constants.ENV_RELEASE, release);

    this.console.printLine("Launching command on: " + task.getWorkingDirectory());
    this.console.printEnvironment(task.getEnvironment().toMap());

    File workingDir = new File(task.getWorkingDirectory()).getAbsoluteFile();
    try {
      PackageBuilder builder = PackageBuilder.of(workingDir, env);
      builder.setPackagePath(packagePath);
      builder.addPackage(config, source, target);
      builder.build();

      return TaskResponse.success("Executed the build").toResponse();
    } catch (Throwable e) {
      if (e.getMessage() == null) {
        Arrays.asList(e.getStackTrace()).forEach(el -> this.console.printLine(el.toString()));
      }
      return TaskResponse.failure(e.getMessage()).toResponse();
    }
  }
}
