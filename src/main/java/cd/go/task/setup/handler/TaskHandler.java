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

package cd.go.task.setup.handler;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cd.go.task.setup.model.TaskRequest;
import cd.go.task.setup.model.TaskResponse;
import cd.go.task.util.RequestHandler;
import cd.go.task.util.Archive;

/**
 * Get the response for a "configuration" request.
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
    String moduleName = task.getConfig().getValue("Module");

    console.printLine("Launching command on: " + task.getWorkingDirectory());
    console.printLine("Module Name:" + moduleName);
    console.printEnvironment(task.getEnvironment());

    File workingDir = new File(task.getWorkingDirectory());
    File modules = new File(workingDir, "download/" + moduleName);
    File data = new File(workingDir, "package/" + moduleName + "/data");
    data.mkdirs();

    for (File file : modules.listFiles()) {
      console.printLine("File: " + file.getAbsolutePath());
      try {
        Archive.unzip(file, data);
      } catch (IOException e) {
        console.printLine(e.toString());
      }
    }

    return TaskResponse.success("Executed the build").toResponse();
  }
}
