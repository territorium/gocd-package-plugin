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

package cd.go.task.installer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The {@link Qt} is a helper class working on the environment to the QT HOME directory.
 */
public abstract class Qt {

  private static final String QT_HOME = "QT_HOME";


  private final File                home;
  private final File                workingDir;
  private final Map<String, String> environment;


  private String             packages;
  private final List<String> modules = new ArrayList<>();


  /**
   * Constructs an instance of {@link Qt}.
   *
   * @param workingDir
   * @param environment
   */
  protected Qt(File workingDir, Map<String, String> environment) {
    this.home = new File(environment.get(Qt.QT_HOME));
    this.workingDir = workingDir;
    this.environment = environment;
  }

  /**
   * Get the Qt HOME directory
   */
  protected final File getQtHome() {
    return home;
  }

  /**
   * Get the Qt BASE directory
   */
  protected final File getQtBase() {
    return getQtHome().getParentFile();
  }

  /**
   * @return the workingDir
   */
  protected final File getWorkingDir() {
    return workingDir;
  }

  /**
   * @return the environment
   */
  protected final Map<String, String> getEnvironment() {
    return environment;
  }

  /**
   * Get the QtInstallerFramework binary
   */
  protected final File getInstallerBin() {
    Path path = getQtBase().toPath().resolve("Tools").resolve("QtInstallerFramework");
    for (File file : path.toFile().listFiles()) {
      return path.resolve(file.getName()).resolve("bin").toFile();
    }
    return path.resolve("3.0").resolve("bin").toFile();
  }

  /**
   * Get the Qt repository generator
   */
  protected final File getRepositoryGenerator() {
    String repogen = Qt.isWindows() ? "repogen.exe" : "repogen";
    return new File(getInstallerBin(), repogen);
  }

  /**
   * Get the Qt binary creator
   */
  protected final File getBinaryCreator() {
    String binarycreator = Qt.isWindows() ? "binarycreator.exe" : "binarycreator";
    return new File(getInstallerBin(), binarycreator);
  }

  private static boolean isWindows() {
    String osName = System.getProperty("os.name");
    return osName != null && osName.toLowerCase().contains("windows");
  }

  /**
   * Set the package path.
   */
  public final Qt setPackagePath(String packages) {
    this.packages = packages;
    return this;
  }

  /**
   * Add the a list of modules.
   * 
   * @param modules
   */
  public final Qt addModules(List<String> modules) {
    this.modules.addAll(modules);
    return this;
  }

  /**
   * Create an abstract command for the Qt {@link Process}.
   */
  protected List<String> getCommand() {
    List<String> command = new ArrayList<String>();

    // Define include modules
    if (!modules.isEmpty()) {
      command.add("-i");
      command.add(String.join(",", modules));
    }
    command.add("-p");
    command.add(packages);
    return command;
  }

  /**
   * Build the Qt {@link Process}.
   * 
   * @throws IOException
   */
  public final Process build() throws IOException {
    ProcessBuilder builder = new ProcessBuilder(getCommand());
    builder.directory(new File(getWorkingDir().getAbsolutePath()));
    builder.environment().putAll(getEnvironment());
    return builder.start();
  }
}
