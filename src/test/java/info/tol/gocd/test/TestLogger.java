/*
 * Copyright (c) 2001-2019 Territorium Online Srl / TOL GmbH. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as defined in and that are
 * subject to the Territorium Online License Version 1.0. You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at http://www.tol.info/license/
 * and read it before using this file.
 *
 * The Original Code and all software distributed under the License are distributed on an 'AS IS'
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TERRITORIUM ONLINE HEREBY
 * DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the License for
 * the specific language governing rights and limitations under the License.
 */

package info.tol.gocd.test;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.InputStream;
import java.util.Map;


/**
 * The {@link TestLogger} class.
 */
public class TestLogger extends JobConsoleLogger {

  public static final JobConsoleLogger CONSOLE = new TestLogger();

  private TestLogger() {}

  public void printLine(String line) {
    System.out.println(line);
  }

  public void readErrorOf(InputStream in) {
    throw new UnsupportedOperationException();
  }

  public void readOutputOf(InputStream in) {
    throw new UnsupportedOperationException();
  }

  public void printEnvironment(Map<String, String> environment) {
    System.out.println("Environment variables:");
    environment.forEach((k, v) -> System.out.printf("  %1$-16s = %2$s\n", k, v));
    System.out.println();
  }
}
