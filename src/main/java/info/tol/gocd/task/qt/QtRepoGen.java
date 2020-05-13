
package info.tol.gocd.task.qt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import info.tol.gocd.util.Environment;

public class QtRepoGen extends Qt {

  private boolean update;
  private String  repository;

  /**
   * Create an installer builder
   *
   * @param workingDir
   * @param environment
   */
  public QtRepoGen(File workingDir, Environment environment) {
    super(workingDir, environment);
    this.update = false;
  }

  public final QtRepoGen setUpdate() {
    this.update = true;
    return this;
  }

  public final QtRepoGen setRepositoryPath(String repository) {
    this.repository = repository;
    return this;
  }

  /**
   * Create the command for the Qt repogen {@link Process}.
   */
  @Override
  protected final List<String> getCommand() {
    List<String> command = new ArrayList<>();
    command.add(getRepositoryGenerator().getAbsolutePath());

    if (this.update) {
      command.add("--update");
    }

    command.addAll(super.getCommand());
    command.add(this.repository);
    return command;
  }

  /**
   * Constructs an instance of {@link Qt}.
   *
   * @param workingDir
   * @param environment
   */
  public static QtRepoGen of(File workingDir, Environment environment) {
    return new QtRepoGen(workingDir, environment);
  }
}
