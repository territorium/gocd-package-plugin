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

import cd.go.common.request.RequestHandler;
import cd.go.common.util.Assemply;
import cd.go.task.installer.Constants;
import cd.go.task.installer.QtInstaller;
import cd.go.task.installer.QtRepoGen;
import cd.go.task.installer.builder.PackageBuilder;
import cd.go.task.model.TaskRequest;
import cd.go.task.model.TaskResponse;

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
    String config = task.getConfig().getValue("module");
    String source = task.getConfig().getValue("source");
    String target = task.getConfig().getValue("target");
    String packagePath = task.getConfig().getValue("path");

    console.printLine("Launching command on: " + task.getWorkingDirectory());
    console.printEnvironment(task.getEnvironment().toMap());

    File workingDir = new File(task.getWorkingDirectory());
    try {
      switch (mode) {
        case "PACKAGE":
          PackageBuilder builder = PackageBuilder.of(workingDir, task.getEnvironment());
          builder.setPackagePath(packagePath);
          builder.addPackage(config, workingDir, source, target);
          builder.build();
          break;

        case "REPOSITORY":
          Process process = createRepogen(task, mode, target, toModules(task, source));
          console.readErrorOf(process.getErrorStream());
          console.readOutputOf(process.getInputStream());

          int exitCode = process.waitFor();
          process.destroy();
          return (exitCode == 0) ? TaskResponse.success("Executed the build").toResponse()
              : TaskResponse.failure("Could not execute build! Process returned with status code " + exitCode)
                  .toResponse();

        case "ASSEMBLY":
          Assemply assemply = Assemply.of(new File(workingDir, target));
          for (String entry : source.split("[,\\n]")) {
            File file = new File(workingDir, entry);
            if (file.exists()) {
              assemply.addFile(file);
            } else {
              console.printLine("File '" + entry + "' doesn't exists");
            }
          }
          assemply.build();
          return TaskResponse.success("Assemply created").toResponse();

        case "ONLINE":
        case "OFFLINE":
        case "INSTALLER":
          process = createInstaller(task, target, mode, config, toModules(task, source));
          console.readErrorOf(process.getErrorStream());
          console.readOutputOf(process.getInputStream());

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
  protected Process createRepogen(TaskRequest task, String mode, String target, List<String> modules)
      throws IOException {
    File workingDir = new File(task.getWorkingDirectory());


    QtRepoGen builder = QtRepoGen.of(workingDir, task.getEnvironment());
    builder.setUpdate();
    builder.addModules(modules);
    builder.setPackagePath(Constants.PATH_PACKAGE);
    builder.setRepositoryPath(Constants.PATH_REPOSITORY);
    return builder.build();
  }

  /**
   * Create an installer.
   * 
   * @param task
   * @param name
   * @param mode
   * @param config
   * @param modules
   */
  protected Process createInstaller(TaskRequest task, String name, String mode, String config, List<String> modules)
      throws IOException {
    File workingDir = new File(task.getWorkingDirectory());

    QtInstaller builder = QtInstaller.of(workingDir, task.getEnvironment());
    builder.setName(name).setMode(mode);
    builder.setConfig(config);
    builder.addModules(modules);
    builder.setPackagePath(Constants.PATH_PACKAGE);
    return builder.build();
  }

  /**
   * Converts the modules with it's dependencies
   *
   * @param task
   * @param text
   */
  protected List<String> toModules(TaskRequest task, String text) {
    List<String> modules = new ArrayList<>();
    if (text != null) {
      for (String name : Arrays.asList(text.split(",\\n"))) {
        modules.add(task.getEnvironment().replaceModuleName(name));
      }

      for (File file : new File(task.getWorkingDirectory(), Constants.PATH_PACKAGE).listFiles()) {
        for (String module : new ArrayList<>(modules)) {
          if (!modules.contains(file.getName()) && module.startsWith(file.getName()))
            modules.add(file.getName());
        }
      }
    }
    return modules;
  }
}
