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

import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// TODO: execute your task and setup stdout/stderr to pipe the streams to GoCD
public class QtTaskExecutor {

  private enum Build {
    QMAKE,
    MAKE,
    TEST
  }

  public GoPluginApiResponse execute(QtConfig config, TaskContext context, JobConsoleLogger console) {
    boolean isBuild = (Qt.MODE_BUILD.equalsIgnoreCase(config.getBuild())) && config.getTarget() != null;
    try {
      if (isBuild) {
        TaskResponse response = process(config, context, console, Build.QMAKE, null);
        if (response.responseCode() == DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
          String targets = config.getTarget();
          for (String target : targets.split(",")) {
            response = process(config, context, console, Build.MAKE, target.trim());
            if (response.responseCode() != DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
              return response.toResponse();
            }
          }
          if (targets.endsWith(",")) {
            response = process(config, context, console, Build.MAKE, "");
            if (response.responseCode() != DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
              return response.toResponse();
            }
          }
        }
        return response.toResponse();
      }
      return process(config, context, console, Build.TEST, config.getTarget()).toResponse();
    } catch (Exception e) {
      return TaskResponse.failure(e, "Failed to invoke the build!").toResponse();
    }
  }


  /**
   * Process a single build.
   * 
   * @param config
   * @param context
   * @param console
   * @param build
   * @param target
   */
  private TaskResponse process(QtConfig config, TaskContext context, JobConsoleLogger console, Build build,
      String target) throws IOException, InterruptedException {
    ProcessBuilder builder = createCommand(context, config, build, target);
    builder.directory(new File(context.getWorkingDir()));
    builder.environment().putAll(context.getEnvironment());
    updateEnvironment(builder, context, config);

    console.printLine("Launching command: " + builder.command());
    console.printEnvironment(builder.environment());

    Process process = builder.start();
    console.readErrorOf(process.getErrorStream());
    console.readOutputOf(process.getInputStream());

    int exitCode = process.waitFor();
    process.destroy();
    return (exitCode == 0) ? TaskResponse.success("Executed the build")
        : TaskResponse.failure("Could not execute build! Process returned with status code " + exitCode);
  }


  /**
   * Create the {@link ProcessBuilder} with the command.
   *
   * @param context
   * @param config
   * @param build
   * @param target
   */
  private ProcessBuilder createCommand(TaskContext context, QtConfig config, Build build, String target) {
    boolean isWindows = Util.isWindows();
    List<String> args = new ArrayList<String>();
    if (isWindows) {
      args.add("vcvarsall.bat");
      args.add("x86_amd64");
      args.add("&");
    }

    String qtHome = context.getEnvironment().get(Qt.QT_HOME);
    switch (build) {
      case QMAKE:
        Path path = Paths.get(Qt.getArch(context, config), "bin", "qmake");

        args.add(new File(qtHome, path.toString()).getAbsolutePath());
        args.add("-spec");
        args.add(Qt.getSpec(context, config));

        // Adding CONFIG+=
        Qt.getConfig(context, config).forEach(c -> args.add("CONFIG+=" + c));

        args.add(config.getCommand());
        break;

      case TEST:
        path = Paths.get(Qt.getArch(context, config), "bin");
        String bin = new File(qtHome, path.toString()).getAbsolutePath();
        if (isWindows) {
          args.add("set PATH=" + bin + ";%PATH%");
          args.add("&");
        } else {
          args.add("LD_LIBRARY_PATH=$LD_LIBRARY_PATH:" + bin);
        }

        String testCase = config.getCommand();
        if (testCase == null || testCase.trim().isEmpty()) {
          testCase = config.getTarget();
        }
        if (isWindows) {
          testCase += ".exe";
        }

        String spec = Qt.getSpec(context, config);
        Path test = Paths.get(context.getWorkingDir(), "build", spec, "bin");
        args.add(new File(test.toFile(), testCase).getAbsolutePath());
        args.add("-xunitxml");
        break;

      case MAKE:
        args.add(isWindows ? "jom" : "make");
        if (target != null && !target.isEmpty()) {
          args.add(target);
        }
        break;
    }

    String cmd = isWindows ? "cmd" : "sh";
    String cmdArg = isWindows ? "/c" : "-c";
    String argument = String.join(" ", args);
    return new ProcessBuilder(cmd, cmdArg, argument);
  }

  /**
   * Create the {@link ProcessBuilder} with the command.
   *
   * @param builder
   * @param context
   * @param config
   */
  private void updateEnvironment(ProcessBuilder builder, TaskContext context, QtConfig config) {
    String release = context.getEnvironment().get(Qt.RELEASE);

    builder.environment().put(Qt.QT_ARCH, Qt.getArch(context, config));
    builder.environment().put(Qt.QT_SPEC, Qt.getSpec(context, config));

    if (context.getEnvironment().containsKey(Qt.QT_REPOSITORY) && release != null) {
      Path repository = Paths.get(context.getEnvironment().get(Qt.QT_REPOSITORY));
      builder.environment().put(Qt.QT_REPO, repository.resolve(release).toString());
    }

    String spec = Qt.getSpec(context, config);
    Path buildPath = new File(context.getWorkingDir()).getAbsoluteFile().toPath().resolve("build");
    Path base = buildPath.resolve(spec);

    builder.environment().put(Qt.QT_BUILD, buildPath.toString());
    builder.environment().put(Qt.QML2_IMPORT_PATH, base.resolve("qml").toString());
    builder.environment().put(Qt.QT_PLUGIN_PATH, base.resolve("plugins").toString());
  }
}
