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

package info.tol.gocd.util.request;

public interface Request {

  String MATERIAL_CONFIG           = "scm-configuration";
  String MATERIAL_VIEW             = "scm-view";
  String MATERIAL_VALIDATE         = "validate-scm-configuration";
  String MATERIAL_CHECK_CONNECTION = "check-scm-connection";
  String MATERIAL_REVISION         = "latest-revision";
  String MATERIAL_REVISION_SINCE   = "latest-revisions-since";
  String MATERIAL_CHECKOUT         = "checkout";

  String TASK_VIEW                 = "view";
  String TASK_CONFIG               = "configuration";
  String TASK_VALIDATE             = "validate";
  String TASK_EXECUTE              = "execute";
}
