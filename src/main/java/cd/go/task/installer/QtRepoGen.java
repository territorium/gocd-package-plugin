
package cd.go.task.installer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cd.go.common.util.Environment;

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
  protected final List<String> getCommand() {
    List<String> command = new ArrayList<String>();
    command.add(getRepositoryGenerator().getAbsolutePath());

    if (update) {
      command.add("--update");
    }

    command.addAll(super.getCommand());
    command.add(repository);
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
