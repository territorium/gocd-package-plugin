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

package cd.go.task.setup;

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.Collections;

import cd.go.task.setup.handler.ConfigHandler;
import cd.go.task.setup.handler.TaskHandler;
import cd.go.task.setup.handler.TemplateHandler;
import cd.go.task.setup.handler.ValidationHandler;
import cd.go.task.util.Request;

@Extension
public class SetupPlugin implements GoPlugin {

  private static final String EXTENSION = "task";
  private static final String VERSION   = "1.0";
  private static final Logger LOGGER    = Logger.getLoggerFor(SetupPlugin.class);


  private static final GoPluginIdentifier PLUGIN_IDENTIFIER =
      new GoPluginIdentifier(EXTENSION, Collections.singletonList(VERSION));


  private GoApplicationAccessor accessor;

  public GoPluginIdentifier pluginIdentifier() {
    return PLUGIN_IDENTIFIER;
  }

  @Override
  public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
    this.accessor = accessor;
  }

  @Override
  public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
    try {
      switch (request.requestName()) {
        case Request.TASK_VIEW:
          return new TemplateHandler("Qt Packages", "/task.template.html").handle(request);

        case Request.TASK_CONFIG:
          return new ConfigHandler().handle(request);

        case Request.TASK_VALIDATE:
          return new ValidationHandler().handle(request);

        case Request.TASK_EXECUTE:
          return new TaskHandler(JobConsoleLogger.getConsoleLogger()).handle(request);

        default:
          throw new UnhandledRequestTypeException(request.requestName());
      }
    } catch (Exception e) {
      LOGGER.error("Error while executing request " + request.requestName(), e);
      throw new RuntimeException(e);
    }
  }
}
