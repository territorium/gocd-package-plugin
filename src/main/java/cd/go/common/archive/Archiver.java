/**
 * 
 */

package cd.go.common.archive;

import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
public abstract class Archiver {

  private final File   file;
  private final String name;

  /**
   * Creates a .tar.gz file
   * 
   * @param file
   * @param name
   */
  public Archiver(File file, String name) {
    this.file = file;
    this.name = name;
  }

  /**
   * Get the target {@link File} for the archive.
   */
  protected final File getFile() {
    return file;
  }

  /**
   * Creates a specific {@link InputStream}.
   */
  protected InputStream getInputStream() throws IOException {
    return new BufferedInputStream(new FileInputStream(file));
  }

  /**
   * Creates a specific {@link OutputStream}.
   */
  protected OutputStream getOutputStream() throws IOException {
    return new BufferedOutputStream(new FileOutputStream(file));
  }

  /**
   * Extract to the target directory and return the last modification time.
   * 
   * @param target
   */
  public abstract LocalDateTime extract(File target) throws IOException;

  /**
   * Archives the files.
   * 
   * @param files
   */
  public abstract void archive(List<File> files) throws IOException;

  /**
   * Extract the files in a directory with the name of the file
   */
  public final void extract() throws IOException {
    extract(new File(getFile().getParentFile(), name));
  }

  /**
   * Archives the files in the directory
   * 
   * @param directory
   */
  public final void archiveDirectory(File directory) throws IOException {
    archive(Arrays.asList(directory.listFiles()));
  }

  /**
   * Copy the file to the {@link OutputStream}.
   * 
   * @param file
   * @param stream
   */
  protected static void fileToStream(File file, OutputStream stream) throws IOException {
    try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
      IOUtils.copy(buffer, stream);
    }
  }

  /**
   * Copy the file to the {@link OutputStream}.
   * 
   * @param file
   * @param stream
   */
  protected static void streamToFile(File file, InputStream stream) throws IOException {
    try (BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(file))) {
      IOUtils.copy(stream, buffer);
    }
  }

  /**
   * Get the new filename
   * 
   * @param target
   * @param name
   */
  protected static File newFile(File target, String name) throws IOException {
    File file = new File(target, name);
    String targetPath = target.getCanonicalPath();
    String targetFilePath = file.getCanonicalPath();

    if (!targetFilePath.startsWith(targetPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + name);
    }
    return file;
  }

  private static String getFilename(File file, int limit) {
    return file.getName().substring(0, file.getName().length() - limit);
  }

  /**
   * Archive the files.
   * 
   * @param file
   */
  public static Archiver of(File file) throws IOException {
    String filename = file.getName();
    if (filename.toLowerCase().endsWith(".tar")) {
      return new ArchiverTar(file, getFilename(file, 4));
    } else if (filename.toLowerCase().endsWith(".tar.gz")) {
      return new ArchiverTarGz(file, getFilename(file, 7));
    } else if (filename.toLowerCase().endsWith(".zip") || filename.toLowerCase().endsWith(".jar")
        || filename.toLowerCase().endsWith(".war")) {
      return new ArchiverZip(file, getFilename(file, 4));
    }
    throw new IOException("Unsupported compression format for " + file.getName());
  }
}