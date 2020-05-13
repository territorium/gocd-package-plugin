
package cd.go.task;

import java.io.File;

import info.tol.gocd.task.qt.Constants;
import info.tol.gocd.task.qt.builder.PackageBuilder;
import info.tol.gocd.util.Environment;

public class PackageTest {

  public static void main(String[] args) throws Exception {
    File workingDir = new File("/home/brigl/Downloads/smartIO-Installer-20.01");
    Environment environment = new Environment();
    environment.set("RELEASE", "20.04");

    PackageTest.buildPackages(workingDir, environment);
  }

  protected static void buildPackages(File workingDir, Environment environment) throws Exception {
    Environment e = Constants.updateEnvironment(environment);
    PackageBuilder builder = PackageBuilder.of(workingDir, e);
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
