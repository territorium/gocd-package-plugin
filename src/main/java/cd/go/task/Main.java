
package cd.go.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cd.go.task.installer.builder.PackageBuilder;

public class Main {

	private static final String RELEASE = "20.01";

	public static void main(String[] args) throws Exception {
		File workingDir = new File("/data/smartIO/develop/installer");
		Map<String, String> environment = new HashMap<String, String>();
		environment.put("RELEASE", RELEASE);

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
}
