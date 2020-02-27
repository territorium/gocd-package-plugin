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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cd.go.task.installer.Packages;
import cd.go.task.installer.Qt;
import cd.go.task.installer.builder.PackageBuilder;
import cd.go.task.installer.builder.Parameter;
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
    String module = task.getConfig().getValue("module");
    String source = task.getConfig().getValue("source");
    String target = task.getConfig().getValue("target");

    console.printLine("Launching command on: " + task.getWorkingDirectory());
    console.printEnvironment(task.getEnvironment());

    File workingDir = new File(task.getWorkingDirectory());
    try {
      switch (mode) {
        case "PACKAGE":
          PackageBuilder builder = PackageBuilder.of(workingDir, task.getEnvironment());
          builder.setPackagePath(modulePath);
          builder.addPackage(module, workingDir, source, target);
          builder.build();
          break;

        case "REPOSITORY":
          Process process = createRepogen(task, mode, source, target, module);
          console.readErrorOf(process.getErrorStream());
          console.readOutputOf(process.getInputStream());

          int exitCode = process.waitFor();
          process.destroy();
          return (exitCode == 0) ? TaskResponse.success("Executed the build").toResponse()
              : TaskResponse.failure("Could not execute build! Process returned with status code " + exitCode)
                  .toResponse();

        case "ONLINE":
        case "OFFLINE":
        case "INSTALLER":
          process = createInstaller(task, mode, source, target, module);
          exitCode = process.waitFor();
          process.destroy();
          return (exitCode == 0) ? TaskResponse.success("Executed the build").toResponse()
              : TaskResponse.failure("Could not execute build! Process returned with status code " + exitCode)
                  .toResponse();

        default:
          return TaskResponse.success("Nothing to do").toResponse();
      }
    } catch (Throwable e) {
      console.printLine("" + e);
      return TaskResponse.failure(e.getMessage()).toResponse();
    }
    return TaskResponse.success("Executed the build").toResponse();
  }

  /**
   * Create an repository generator.
   * 
   * @param task
   * @param mode
   * @param source
   * @param target
   * @param modules
   */
  protected Process createRepogen(TaskRequest task, String mode, String source, String target, String modules)
      throws IOException {
    String packages = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);
    String repository = String.join(File.separator, Packages.BUILD, Packages.BUILD_REPO);

    List<String> command = new ArrayList<String>();
    command.add(Qt.of(task.getEnvironment()).getRepogen().getAbsolutePath());

    if (modules != null && !modules.trim().isEmpty()) {
      command.add("-i");
      command.add(getModules(task, modules));
    }

    command.add("--update");
    command.add("-p");
    command.add(packages);
    command.add(repository);

    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(new File(task.getWorkingDirectory()));
    builder.environment().putAll(task.getEnvironment());
    return builder.start();
  }

  /**
   * Create an installer.
   * 
   * @param task
   * @param mode
   * @param source
   * @param target
   * @param modules
   */
  protected Process createInstaller(TaskRequest task, String mode, String source, String target, String modules)
      throws IOException {
    File workingDir = new File(task.getWorkingDirectory());
    String packages = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);

    List<String> command = new ArrayList<String>();
    command.add(Qt.of(task.getEnvironment()).getBinaryCreator().getAbsolutePath());
    switch (mode) {
      case "ONLINE":
        command.add("-n");
        break;
      case "OFFLINE":
        command.add("-f");
        break;
      default:
    }

    if (modules != null && !modules.trim().isEmpty()) {
      command.add("-i");
      command.add(getModules(task, modules));
    }

    command.add("-c");
    command.add(source);
    command.add("-p");
    command.add(packages);
    command.add(target);

    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(new File(workingDir.getAbsolutePath()));
    builder.environment().putAll(task.getEnvironment());
    return builder.start();
  }

  /**
   * Converts the modules with it's dependencies
   *
   * @param task
   * @param modules
   */
  protected String getModules(TaskRequest task, String modules) {
    File workingDir = new File(task.getWorkingDirectory());
    String packages = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);

    String text = Parameter.of(task.getEnvironment()).replace(modules);
    List<String> list = new ArrayList<>(Arrays.asList(text.split(",")));

    for (File file : new File(workingDir, packages).listFiles()) {
      for (String item : text.split(",")) {
        if (!list.contains(file.getName()) && item.startsWith(file.getName()))
          list.add(file.getName());
      }
    }
    return String.join(",", list);
  }
}
