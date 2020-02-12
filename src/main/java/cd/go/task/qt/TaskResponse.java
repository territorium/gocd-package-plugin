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
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;

public class TaskResponse {

  private final boolean   success;
  private final String    message;
  private final Throwable throwable;

  private TaskResponse(boolean success, String message, Throwable throwable) {
    this.success = success;
    this.message = message;
    this.throwable = throwable;
  }

  public int responseCode() {
    return success ? DefaultGoApiResponse.SUCCESS_RESPONSE_CODE : DefaultGoApiResponse.INTERNAL_ERROR;
  }

  public final GoPluginApiResponse toResponse() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("success", success);
    result.put("message", message);
    result.put("exception", throwable);
    return new DefaultGoPluginApiResponse(responseCode(), QtTaskRequest.GSON.toJson(result));
  }

  public static TaskResponse success(String message) {
    return new TaskResponse(true, message, null);
  }

  public static TaskResponse failure(String message) {
    return new TaskResponse(false, message, null);
  }

  public static TaskResponse failure(Throwable throwable, String message) {
    return new TaskResponse(false, message, throwable);
  }
}
