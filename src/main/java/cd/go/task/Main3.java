
package cd.go.task;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cd.go.common.util.Archive;

public class Main3 {

  private static final String MODULE  = "2004dev";
  private static final String PATTERN = "0.0-0";
  private static final String RELEASE = "20.04";
  private static final String PACKAGE = "20.04-dev";

  public static void main(String[] args) throws Exception {
    File workingDir = new File("/home/brigl/TOL");
    Map<String, String> environment = new HashMap<String, String>();
    environment.put("MODULE", MODULE);
    environment.put("PATTERN", PATTERN);
    environment.put("RELEASE", RELEASE);
    environment.put("PACKAGE", PACKAGE);
    environment.put("QT_HOME", "/data/Software/Qt/5.12.7");

    List<File> files = Arrays.asList(new File(workingDir, "tools"));
    File tarFile = new File(workingDir, "my.tar");
    Archive.tar(tarFile, files);
  }
}
