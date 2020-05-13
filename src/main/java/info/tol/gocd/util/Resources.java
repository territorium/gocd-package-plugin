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

package info.tol.gocd.util;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class Resources {

  private static final int EOF = -1;

  public static String readString(String resourceFile) {
    InputStream stream = Resources.class.getResourceAsStream(resourceFile);
    try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
      try (StringWriter writer = new StringWriter()) {
        Resources.copy(reader, writer);
        return writer.toString();
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not find resource " + resourceFile, e);
    }
  }

  public static byte[] readBytes(String resourceFile) {
    try (InputStream is = Resources.class.getResourceAsStream(resourceFile)) {
      return Resources.readFully(is);
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
    return (text != null) && (value != null) && text.toLowerCase().contains(value.toLowerCase());
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


  /**
   * Copies chars from a <code>Reader</code> to a <code>Writer</code>. <p> This method buffers the
   * input internally, so there is no need to use a <code>BufferedReader</code>. <p> Large streams
   * (over 2GB) will return a chars copied value of <code>-1</code> after the copy has completed
   * since the correct number of chars cannot be returned as an int. For large streams use the
   * <code>copyLarge(Reader, Writer)</code> method.
   */
  private static int copy(final Reader input, final Writer output) throws IOException {
    char[] buffer = new char[1024 * 4];
    long count = 0;
    int n;
    while (Resources.EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    if (count > Integer.MAX_VALUE) {
      return -1;
    }
    return (int) count;
  }
}
