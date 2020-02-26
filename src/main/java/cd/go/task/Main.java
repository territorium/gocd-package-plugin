
package cd.go.task;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cd.go.task.installer.Packages;
import cd.go.task.installer.Qt;
import cd.go.task.installer.builder.PackageBuilder;

public class Main {

	private static final String RELEASE = "20.01";

	public static void main(String[] args) throws Exception {
		File workingDir = new File("/data/smartIO/develop/installer");
		Map<String, String> environment = new HashMap<String, String>();
		environment.put("RELEASE", RELEASE);
		environment.put("QT_HOME", "/data/Software/Qt/5.12.3");

//		Main.buildPackages(workingDir, environment);
//		Main.buildRepository(workingDir, environment);
		Main.buildInstaller(workingDir, environment);
	}

	protected static void buildPackages(File workingDir, Map<String, String> environment) throws Exception {
		PackageBuilder builder = PackageBuilder.of(workingDir, environment);
		builder.setPackagePath("packages2");
		builder.addPackage("tol.$RELEASE.linux",
				new File(workingDir, "download/smartIO-Server-Linux-(?<VERSION>[0-9.\\-]+)"), "$RELEASE");
		builder.addPackage("tol.$RELEASE.windows",
				new File(workingDir, "download/smartIO-Server-Win64-(?<VERSION>[0-9.\\-]+)"), "$RELEASE");

		builder.addPackage("tol.$RELEASE.client.web",
				new File(workingDir, "download/smartIO-Web-(?<VERSION>[0-9.\\-]+)/smartio"),
				"$RELEASE/webapps/client/smartio-$VERSION");
		builder.addPackage("tol.$RELEASE.client.android",
				new File(workingDir, "download/smartIO-Android-(?<VERSION>[0-9.\\-]+).apk"),
				"$RELEASEwebapps/client/smartio-$VERSION.apk");
		builder.addPackage("tol.$RELEASE.client.ios",
				new File(workingDir, "download/smartIO-iOS-(?<VERSION>[0-9.\\-]+).ipa"),
				"$RELEASE/webapps/client/smartio-$VERSION.ipa");
		builder.addPackage("tol.$RELEASE.server",
				new File(workingDir, "download/smartIO-Server-(?<VERSION>[0-9.\\-]+)"), "$RELEASE/webapps/smartio");
		builder.build();
	}

	protected static void buildRepository(File workingDir, Map<String, String> environment) throws Exception {
		File repogen = Qt.of(environment).getRepogen();
		String packages = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);
		String repository = String.join(File.separator, Packages.BUILD, Packages.BUILD_REPO);

		List<String> command = Arrays.asList(repogen.getAbsolutePath(), "--update", "-p", packages, repository);
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(workingDir.getAbsolutePath()));
		builder.environment().putAll(environment);

		Process process = builder.start();
		int exitCode = process.waitFor();
		process.destroy();
	}

	protected static void buildInstaller(File workingDir, Map<String, String> environment) throws Exception {
		File repogen = Qt.of(environment).getBinaryCreator();
		String packages = String.join(File.separator, Packages.BUILD, Packages.BUILD_PKG);
		String config = "config/config_online.xml";

		// "-n" online only
		// "-f" offline only
		List<String> command = Arrays.asList(repogen.getAbsolutePath(), "-n", "-c", config, "-p", packages,
				"Installer");
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(workingDir.getAbsolutePath()));
		builder.environment().putAll(environment);

		Process process = builder.start();
		int exitCode = process.waitFor();
		process.destroy();
	}

}
