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

import java.util.Map;

public class TaskContext {

  private final String              workingDir;
  private final Map<String, String> environment;


  private TaskContext(String workingDir, Map<String, String> environment) {
    this.workingDir = workingDir;
    this.environment = environment;
  }

  public final String getWorkingDir() {
    return workingDir;
  }

  public final Map<String, String> getEnvironment() {
    return environment;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static TaskContext of(Map<?, ?> request) {
    Map<?, ?> context = (Map) request.get("context");
    String workingDir = (String) context.get("workingDirectory");
    Map<String, String> environment = (Map) context.get("environmentVariables");
    return new TaskContext(workingDir, environment);
  }
}
