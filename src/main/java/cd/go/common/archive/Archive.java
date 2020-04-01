/**
 * 
 */

package cd.go.common.archive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 * Gzipped Tar archiver which preserves
 * 
 * <pre>
 * <ul>
 *   <li>POSIX file permissions</li>
 *   <li>Symbolic links (if the link target points inside the archive)</li>
 *   <li>Last modification timestamp</li>
  </ul>
 * </pre>
 * 
 * In the archive as found in the filesystem for files to be archived. It uses GNU tar format
 * extensions for archive entries with path length > 100.
 */
public abstract class Archive {

  private final File   file;
  private final String name;

  /**
   * Creates a .tar.gz file
   * 
   * @param file
   * @param name
   */
  public Archive(File file, String name) {
    this.file = file;
    this.name = name;
  }

  /**
   * Get the archive {@link File}.
   */
  protected final File getFile() {
    return file;
  }

  /**
   * Creates a specific {@link InputStream}.
   */
  protected InputStream getInputStream() throws IOException {
    return new BufferedInputStream(new FileInputStream(getFile()));
  }

  /**
   * Creates a specific {@link OutputStream}.
   */
  protected OutputStream getOutputStream() throws IOException {
    return new BufferedOutputStream(new FileOutputStream(getFile()));
  }

  /**
   * Extract to the target directory and return the last modification time.
   * 
   * @param target
   */
  public abstract LocalDateTime extract(File target) throws IOException;

  /**
   * Extract the files in a directory with the name of the file
   */
  public final void extract() throws IOException {
    extract(new File(getFile().getParentFile(), name));
  }

  /**
   * Creates an {@link Archive.ArchiveBuilder} to adding new files to the archive.
   */
  public abstract ArchiveBuilder builder() throws IOException;

  /**
   * Archive the files.
   * 
   * @param file
   */
  public static Archive of(File file) throws IOException {
    String filename = file.getName();
    if (filename.toLowerCase().endsWith(".tar")) {
      return new ArchiveTar(file, filename.substring(0, filename.length() - 4));
    } else if (filename.toLowerCase().endsWith(".tar.gz")) {
      return new ArchiveTarGz(file, filename.substring(0, filename.length() - 7));
    } else if (filename.toLowerCase().endsWith(".zip") || filename.toLowerCase().endsWith(".jar")
        || filename.toLowerCase().endsWith(".war")) {
      return new ArchiveZip(file, filename.substring(0, filename.length() - 4));
    }
    throw new IOException("Unsupported compression format for " + file.getName());
  }

  /**
   * Creates an {@link Archive} {@link ArchiveBuilder} for the file.
   * 
   * @param file
   */
  public static ArchiveBuilder builder(File file) throws IOException {
    return Archive.of(file).builder();
  }
}