
package cd.go.task;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cd.go.task.installer.Packages;
import cd.go.task.installer.Qt;
import cd.go.task.installer.builder.PackageBuilder;

public class Main2 {

  private static final String MODULE  = "2004dev";
  private static final String PATTERN = "0.0-0";
  private static final String RELEASE = "20.04";
  private static final String PACKAGE = "20.04-dev";

  public static void main(String[] args) throws Exception {
    File workingDir = new File("/data/smartIO/test");
    Map<String, String> environment = new HashMap<String, String>();
    environment.put("MODULE", MODULE);
    environment.put("PATTERN", PATTERN);
    environment.put("RELEASE", RELEASE);
    environment.put("PACKAGE", PACKAGE);
    environment.put("QT_HOME", "/data/Software/Qt/5.12.7");

    Main2.buildPackages(workingDir, environment);
    // Main.buildRepository(workingDir, environment);
    // Main.buildInstaller(workingDir, environment);
  }

  protected static void buildPackages(File workingDir, Map<String, String> environment) throws Exception {
    PackageBuilder builder = PackageBuilder.of(workingDir, environment);
    builder.setPackagePath("installer/packages2");
    builder.addPackage("com.microsoft.vcredist", workingDir, "msvc/vc_redist.x64.exe", "vc_redist.x64.exe");
    builder.addPackage("tol.tools_win64", workingDir, "qt/qt-5.12.4-msvc-lite.zip", "tools");
    builder.addPackage("tol.tools_win64", workingDir, "qt/qt-5.12.4-msvc-lite.zip", "tools");
    builder.build();
  }

  protected static void buildRepository(File workingDir, Map<String, String> environment) throws Exception {
    File repogen = Qt.of(environment).getRepositoryGenerator();
    String packages = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);
    String repository = String.join(File.separator, Packages.BUILD, Packages.BUILD_REPO);

    List<String> command = Arrays.asList(repogen.getAbsolutePath(), "--update", "-p", packages, repository);
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(new File(workingDir.getAbsolutePath()));
    builder.environment().putAll(environment);

    Process process = builder.start();
    process.waitFor();
    process.destroy();
  }

  protected static void buildInstaller(File workingDir, Map<String, String> environment) throws Exception {
    File repogen = Qt.of(environment).getBinaryCreator();
    String packages = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);
    String config = "config/config-online.xml";

    // "-n" online only
    // "-f" offline only
    List<String> command =
        Arrays.asList(repogen.getAbsolutePath(), "-c", config, "-n", "-p", packages, "OnlineInstaller");
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.directory(new File(workingDir.getAbsolutePath()));
    builder.environment().putAll(environment);

    Process process = builder.start();
    process.waitFor();
    process.destroy();
  }
}
