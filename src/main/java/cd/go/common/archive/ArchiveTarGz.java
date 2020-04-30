/**
 *
 */

package cd.go.common.archive;


import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Gzipped Tar archiver which preserves
 *
 * <ul> <li>POSIX file permissions</li> <li>Symbolic links (if the link target points inside the
 * archive)</li> <li>Last modification timestamp</li> </ul>
 *
 * in the archive as found in the filesystem for files to be archived. It uses GNU tar format
 * extensions for archive entries with path length > 100.
 */
class ArchiveTarGz extends ArchiveTar {

  /**
   * Creates a .tar.gz file
   *
   * @param file
   * @param name
   */
  public ArchiveTarGz(File file, String name) {
    super(file, name);
  }

  /**
   * Creates a GZip {@link InputStream}.
   */
  @Override
  protected final InputStream getInputStream() throws IOException {
    return new GzipCompressorInputStream(super.getInputStream());
  }

  /**
   * Creates a GZip {@link OutputStream}.
   */
  @Override
  protected final OutputStream getOutputStream() throws IOException {
    return new GzipCompressorOutputStream(super.getOutputStream());
  }
}
