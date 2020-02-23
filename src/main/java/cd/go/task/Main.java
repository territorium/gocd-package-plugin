

package cd.go.task;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;

import cd.go.task.installer.mapper.PackagesBuilder;
import cd.go.task.installer.mapper.PackageBuilder;
import cd.go.task.installer.mapper.Version;

public class Main {

  public static void main(String[] args) throws Exception {
    File file = new File("/data/smartIO/develop/installer");
    PackagesBuilder builder = PackagesBuilder.of(file, Collections.singletonMap("RELEASE", "RC1"));
    builder.build("packages2");

    File source = new File("/data/smartIO/develop/installer/test");
    PackageBuilder data = PackageBuilder.of(file, Collections.singletonMap("RELEASE", "RC1"));
    data.build("tol.$RELEASE.linux", source, Version.of(20, 4), Paths.get("."));
  }

}
