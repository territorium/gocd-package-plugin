/**
 * 
 */

package cd.go.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cd.go.common.archive.Archiver;

/**
 * @author brigl
 *
 */
public class Assemply {

  private final File       file;
  private final List<File> files = new ArrayList<>();;

  /**
   * Creates a new {@link Assemply}.
   * 
   * @param file
   */
  private Assemply(File file) {
    this.file = file;
  }

  /**
   * Add a file to the {@link Assemply}.
   * 
   * @param file
   */
  public final Assemply addFile(File file) {
    this.files.add(file);
    return this;
  }

  /**
   * Creates an {@link Assemply} file.
   */
  public final void build() throws IOException {
    Archiver archiver = Archiver.of(file);
    archiver.archive(files);
  }

  /**
   * Creates a new {@link Assemply}.
   * 
   * @param file
   */
  public static Assemply of(File file) {
    return new Assemply(file);
  }
}
