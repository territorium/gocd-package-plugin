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

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Arrays;

@Extension
public class QtPlugin implements GoPlugin {

  public static final Logger LOGGER = Logger.getLoggerFor(QtPlugin.class);

  // private GoApplicationAccessor accessor;

  public GoPluginIdentifier pluginIdentifier() {
    return new GoPluginIdentifier("task", Arrays.asList("1.0"));
  }

  @Override
  public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
    // this.accessor = accessor;
  }

  @Override
  public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
    switch (request.requestName()) {
      case TaskRequest.REQUEST_ICON:
        return TaskRequest.icon("image/png", "/plugin-icon.png");
      case TaskRequest.REQUEST_VIEW:
        return TaskRequest.view("Qt Task", "/task.template.html");
      case TaskRequest.REQUEST_CONFIG:
        return QtTaskRequest.config();
      case TaskRequest.REQUEST_VALIDATE:
        return QtTaskRequest.validate(request);
      case TaskRequest.REQUEST_EXECUTE:
        return QtTaskRequest.execute(request);
      default:
        throw new UnhandledRequestTypeException(request.requestName());
    }
  }
}
