/**
 *
 */

package info.tol.gocd.util.archive;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * The {@link ArchiveBuilder} implements a closable that allows to add new files to the
 * {@link ArchiveBuilder}.
 */
public abstract class ArchiveBuilder implements Closeable {

  private static final Pattern PATTERN = Pattern.compile("(?:\\[([^\\]]+)\\])?([^,]+)");


  private final OutputStream stream;

  /**
   * Constructs an instance of {@link ArchiveBuilder}.
   *
   * @param stream
   */
  protected ArchiveBuilder(OutputStream stream) {
    this.stream = stream;
  }

  /**
   * Gets the {@link OutputStream}.
   */
  protected OutputStream getOutputStream() {
    return this.stream;
  }

  /**
   * Add a file to the {@link Archive} using the directory.
   *
   * @param directory
   * @param pattern
   * @param location
   */
  protected abstract void addToArchive(File directory, String pattern, String location) throws IOException;

  /**
   * Add a file to the {@link ArchiveBuilder} using the directory.
   *
   * @param directory
   * @param pattern
   */
  public void addFile(File directory, String pattern) throws IOException {
    Matcher matcher = ArchiveBuilder.PATTERN.matcher(pattern);
    while (matcher.find()) {
      addToArchive(directory, matcher.group(2), matcher.group(1));
    }
  }

  /**
   * Add a file to the {@link ArchiveBuilder} using the directory.
   *
   * @param directory
   * @param file
   */
  public final void addFile(File directory, File file) throws IOException {
    Path path = directory.toPath().relativize(file.toPath());
    addFile(directory, path.toString().replace('\\', '/'));
  }

  /**
   * Archives the files.
   *
   * @param files
   */
  public final void addFiles(List<File> files) throws IOException {
    for (File file : files) {
      addFile(file.getParentFile(), file);
    }
  }

  /**
   * Archives the files in the directory
   *
   * @param directory
   */
  public final void addDirectory(File directory) throws IOException {
    for (File file : directory.listFiles()) {
      addFile(directory, file);
    }
  }

  /**
   * Closes this stream and releases any system resources associated with it.
   */
  @Override
  public final void close() throws IOException {
    this.stream.close();
  }
}