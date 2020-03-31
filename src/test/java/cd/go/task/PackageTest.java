
package cd.go.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cd.go.task.installer.Packages;
import cd.go.task.installer.builder.PackageBuilder;

public class PackageTest {

  private static final String PATH_PGK = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);


  private static final String MODULE  = "2004dev";
  private static final String PATTERN = "0.0-0";
  private static final String RELEASE = "20.04";
  private static final String PACKAGE = "20.04-dev";

  public static void main(String[] args) throws Exception {
    File workingDir = new File("/data/smartIO/test/installer");
    Map<String, String> environment = new HashMap<String, String>();
    environment.put("MODULE", MODULE);
    environment.put("PATTERN", PATTERN);
    environment.put("RELEASE", RELEASE);
    environment.put("PACKAGE", PACKAGE);

    PackageTest.buildPackages(workingDir, environment);
  }

  protected static void buildPackages(File workingDir, Map<String, String> environment) throws Exception {
    PackageBuilder builder = PackageBuilder.of(workingDir, environment);
    builder.setPackagePath("packages2");

    builder.addPackage("tol.$MODULE.server_linux", workingDir,
        "download/smartIO-Server-Linux-(?<VERSION>[0-9.\\-]+).tar.gz", "");
    builder.addPackage("tol.$MODULE.server_win64", workingDir,
        "download/smartIO-Server-Win64-(?<VERSION>[0-9.\\-]+).zip", "");

    builder.addPackage("tol.$MODULE.webapp", workingDir, "download/smartIO-Server-(?<VERSION>[0-9.\\-]+).war",
        "webapps/smartio");

    builder.addPackage("tol.$MODULE.app.web", workingDir, "download/smartIO-Web-(?<VERSION>[0-9.\\-]+).zip!smartio",
        "webapps/smartIO/smartio");
    builder.addPackage("tol.$MODULE.app.android", workingDir, "download/smartIO-Android-(?<VERSION>[0-9.\\-]+).apk",
        "webapps/client/smartio-$VERSION.apk");
    builder.addPackage("tol.$MODULE.app.ios", workingDir, "download/smartIO-iOS-(?<VERSION>[0-9.\\-]+).ipa",
        "webapps/client/smartio-$VERSION.ipa");
    builder.build();
  }
}
