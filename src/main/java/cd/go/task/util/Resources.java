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

package cd.go.task.util;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Resources {

  public static String readString(String resourceFile) {
    try (InputStreamReader reader =
        new InputStreamReader(Resources.class.getResourceAsStream(resourceFile), StandardCharsets.UTF_8)) {
      return IOUtils.toString(reader);
    } catch (IOException e) {
      throw new RuntimeException("Could not find resource " + resourceFile, e);
    }
  }

  public static byte[] readBytes(String resourceFile) {
    try (InputStream is = Resources.class.getResourceAsStream(resourceFile)) {
      return readFully(is);
    } catch (IOException e) {
      throw new RuntimeException("Could not find resource " + resourceFile, e);
    }
  }

  public static boolean isWindows() {
    String osName = System.getProperty("os.name");
    boolean isWindows = Resources.containsIgnoreCase(osName, "windows");
    JobConsoleLogger.getConsoleLogger()
        .printLine("OS detected: '" + osName + "'. Is Windows: " + (isWindows ? "yes" : "no"));
    return isWindows;
  }


  private static boolean containsIgnoreCase(String text, String value) {
    return text != null && value != null && text.toLowerCase().contains(value.toLowerCase());
  }

  private static byte[] readFully(InputStream input) throws IOException {
    byte[] buffer = new byte[8192];
    int bytesRead;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    while ((bytesRead = input.read(buffer)) != -1) {
      output.write(buffer, 0, bytesRead);
    }
    return output.toByteArray();
  }
}
