
package info.tol.gocd.task.qt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import info.tol.gocd.util.Environment;

public class QtInstaller extends Qt {

  public static enum Mode {
    BOTH,
    ONLINE,
    OFFLINE;
  }


  private Mode   mode;
  private String name;
  private String config;

  /**
   * Create an installer builder
   *
   * @param workingDir
   * @param environment
   */
  public QtInstaller(File workingDir, Environment environment) {
    super(workingDir, environment);
    this.mode = Mode.BOTH;
  }

  public final QtInstaller setName(String name) {
    this.name = name;
    return this;
  }

  public final QtInstaller setMode(Mode mode) {
    this.mode = mode;
    return this;
  }

  public final QtInstaller setMode(String mode) {
    switch (mode) {
      case "ONLINE":
        setMode(Mode.ONLINE);
        break;
      case "OFFLINE":
        setMode(Mode.OFFLINE);
        break;
      default:
        setMode(Mode.BOTH);
    }
    return this;
  }

  public final QtInstaller setConfig(String config) {
    this.config = config;
    return this;
  }

  /**
   * Create the command for the Qt installer {@link Process}.
   */
  @Override
  protected final List<String> getCommand() {
    List<String> command = new ArrayList<>();
    command.add(getBinaryCreator().getAbsolutePath());

    // Set online/offline mode
    switch (this.mode) {
      case ONLINE:
        command.add("-n");
        break;
      case OFFLINE:
        command.add("-f");
        break;
      default:
    }

    command.add("-c");
    command.add(this.config);
    command.addAll(super.getCommand());
    command.add(this.name);
    return command;
  }

  /**
   * Constructs an instance of {@link Qt}.
   *
   * @param workingDir
   * @param environment
   */
  public static QtInstaller of(File workingDir, Environment environment) {
    return new QtInstaller(workingDir, environment);
  }
}
