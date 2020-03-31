
package cd.go.task;

import java.io.File;

import cd.go.common.util.Environment;
import cd.go.task.installer.Constants;
import cd.go.task.installer.QtInstaller;
import cd.go.task.installer.QtInstaller.Mode;
import cd.go.task.installer.QtRepoGen;

public class InstallerTest {

  public static void main(String[] args) throws Exception {
    File workingDir = new File("/data/smartIO/test/installer");
    Environment environment = new Environment();
    environment.set("QT_HOME", "/data/Software/Qt/5.12.7");

    InstallerTest.buildRepository(workingDir, environment);
    InstallerTest.buildInstaller(workingDir, environment);
  }

  protected static void buildRepository(File workingDir, Environment environment) throws Exception {
    QtRepoGen repogen = QtRepoGen.of(workingDir, environment);
    repogen.setUpdate();
    repogen.setRepositoryPath(Constants.PATH_REPOSITORY);
    repogen.setPackagePath(Constants.PATH_PACKAGE);

    Process process = repogen.build();
    process.waitFor();
    process.destroy();
  }

  protected static void buildInstaller(File workingDir, Environment environment) throws Exception {
    QtInstaller installer = QtInstaller.of(workingDir, environment);
    installer.setName("OnlineInstaller").setMode(Mode.ONLINE);
    installer.setConfig("config/config-online.xml");
    installer.setPackagePath(Constants.PATH_PACKAGE);

    Process process = installer.build();
    process.waitFor();
    process.destroy();
  }
}
