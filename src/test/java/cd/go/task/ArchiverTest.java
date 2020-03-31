
package cd.go.task;

import java.io.File;

import cd.go.common.archive.Archiver;

public class ArchiverTest {

  public static void main(String[] args) throws Exception {
    File workingDir = new File("/home/brigl/TOL");

    Archiver archiver = Archiver.of(new File(workingDir, "test.tar"));
    archiver.archiveDirectory(new File(workingDir, "tools"));

    archiver = Archiver.of(new File(workingDir, "test.tar.gz"));
    archiver.archiveDirectory(new File(workingDir, "tools"));
    archiver.extract(new File(workingDir, "test"));

    archiver = Archiver.of(new File(workingDir, "test.zip"));
    archiver.archiveDirectory(new File(workingDir, "tools"));
  }
}
