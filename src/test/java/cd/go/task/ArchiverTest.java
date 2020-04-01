
package cd.go.task;

import java.io.File;

import cd.go.common.archive.Archive;
import cd.go.common.archive.ArchiveBuilder;

public class ArchiverTest {


  public static void main(String[] args) throws Exception {
    File workingDir = new File("/data/smartIO/test");

    try (ArchiveBuilder c = Archive.builder(new File(workingDir, "test.tar"))) {
      c.addDirectory(new File(workingDir, "qt-5.12.7-gcc-tools"));
    }

    try (ArchiveBuilder c = Archive.builder(new File(workingDir, "test.tar.gz"))) {
      c.addDirectory(new File(workingDir, "qt-5.12.7-gcc-tools"));
      // c.addFile(workingDir, "Monitor");
      // c.addFile(new File(workingDir, "qt-5.12.7-gcc-tools"), "");
    }

    try (ArchiveBuilder c = Archive.builder(new File(workingDir, "test.zip"))) {
      c.addDirectory(new File(workingDir, "qt-5.12.4-msvc-tools"));
      c.addFile(workingDir, "[bin]Monitor.exe");
      // c.addFile(new File(workingDir, "qt-5.12.4-msvc-tools"), "");
    }
  }
}
