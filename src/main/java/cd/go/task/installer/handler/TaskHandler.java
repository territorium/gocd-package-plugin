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

package cd.go.task.installer.handler;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import cd.go.task.installer.Packages;
import cd.go.task.installer.Qt;
import cd.go.task.installer.builder.PackageBuilder;
import cd.go.task.model.TaskRequest;
import cd.go.task.model.TaskResponse;
import cd.go.task.util.RequestHandler;

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
    String mode = task.getConfig().getValue("mode");
    String modulePath = task.getConfig().getValue("path");
    String moduleName = task.getConfig().getValue("module");
    String source = task.getConfig().getValue("source");
    String target = task.getConfig().getValue("target");

    // console.printLine("Launching command on: " + task.getWorkingDirectory());
    // console.printEnvironment(task.getEnvironment());

    File workingDir = new File(task.getWorkingDirectory());
    try {
      switch (mode) {
        case "REPOSITORY":
          File repogen = new File(Qt.of(task.getEnvironment()).getInstallerBin(), "repogen");
          String packages = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);
          String repository = String.join(File.separator, Packages.BUILD, Packages.BUILD_REPO);

          List<String> command = Arrays.asList(repogen.getAbsolutePath(), "--update", "-p", packages, repository);
          ProcessBuilder builder = new ProcessBuilder(command);
          builder.directory(new File(task.getWorkingDirectory()));
          builder.environment().putAll(task.getEnvironment());

          Process process = builder.start();
          console.readErrorOf(process.getErrorStream());
          console.readOutputOf(process.getInputStream());

          int exitCode = process.waitFor();
          process.destroy();
          return (exitCode == 0) ? TaskResponse.success("Executed the build").toResponse()
              : TaskResponse.failure("Could not execute build! Process returned with status code " + exitCode)
                  .toResponse();

        default:
          PackageBuilder builder2 = PackageBuilder.of(workingDir, task.getEnvironment());
          builder2.setPackagePath(modulePath);
          builder2.addPackage(moduleName, new File(workingDir, source), target);
          builder2.build();
          break;
      }
    } catch (Throwable e) {
      return TaskResponse.failure(e.getMessage()).toResponse();
    }
    return TaskResponse.success("Executed the build").toResponse();
  }
}
