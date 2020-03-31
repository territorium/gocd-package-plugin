
package cd.go.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cd.go.task.installer.Packages;
import cd.go.task.installer.QtInstaller;
import cd.go.task.installer.QtInstaller.Mode;
import cd.go.task.installer.QtRepoGen;

public class InstallerTest {

  private static final String PATH_PGK  = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);
  private static final String PATH_REPO = String.join(File.separator, Packages.BUILD, Packages.BUILD_REPO);


  public static void main(String[] args) throws Exception {
    File workingDir = new File("/data/smartIO/test/installer");
    Map<String, String> environment = new HashMap<String, String>();
    environment.put("QT_HOME", "/data/Software/Qt/5.12.7");

    InstallerTest.buildRepository(workingDir, environment);
    InstallerTest.buildInstaller(workingDir, environment);
  }

  protected static void buildRepository(File workingDir, Map<String, String> environment) throws Exception {
    QtRepoGen repogen = QtRepoGen.of(workingDir, environment);
    repogen.setUpdate();
    repogen.setRepositoryPath(InstallerTest.PATH_REPO);
    repogen.setPackagePath(InstallerTest.PATH_PGK);

    Process process = repogen.build();
    process.waitFor();
    process.destroy();
  }

  protected static void buildInstaller(File workingDir, Map<String, String> environment) throws Exception {
    QtInstaller installer = QtInstaller.of(workingDir, environment);
    installer.setName("OnlineInstaller").setMode(Mode.ONLINE);
    installer.setConfig("config/config-online.xml");
    installer.setPackagePath(InstallerTest.PATH_PGK);

    Process process = installer.build();
    process.waitFor();
    process.destroy();
  }
}
