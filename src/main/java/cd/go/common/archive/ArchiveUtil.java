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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 
 * The {@link ArchiveUtil} class provides global helper functions.
 */
abstract class ArchiveUtil {

  /**
   * Copy the file to the {@link OutputStream}.
   * 
   * @param file
   * @param stream
   */
  public static void fileToStream(File file, OutputStream stream) throws IOException {
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
  public static void streamToFile(File file, InputStream stream) throws IOException {
    try (BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(file))) {
      IOUtils.copy(stream, buffer);
    }
  }

  /**
   * Create the new file name.
   * 
   * @param target
   * @param name
   */
  public static File newFile(File target, String name) throws IOException {
    File file = new File(target, name);
    String targetPath = target.getCanonicalPath();
    String targetFilePath = file.getCanonicalPath();

    if (!targetFilePath.startsWith(targetPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + name);
    }
    return file;
  }

  /**
   * <code>true</code> if the file is a symbolik link.
   * 
   * @param file
   */
  public static boolean isSymbolicLink(File file) {
    return Files.isSymbolicLink(file.toPath());
  }


  public static boolean resolvesBelow(File source, File baseDir) throws IOException {
    return !ArchiveUtil.getRelativeSymLinkTarget(source, baseDir).startsWith("..");
  }

  public static Path getRelativeSymLinkTarget(File source, File baseDir) throws IOException {
    Path sourcePath = source.toPath();
    Path linkTarget = Files.readSymbolicLink(sourcePath);
    // link target may be relative, so we resolve it first
    Path resolvedLinkTarget = sourcePath.getParent().resolve(linkTarget);
    Path relative = baseDir.toPath().relativize(resolvedLinkTarget);
    Path normalizedSymLinkPath = relative.normalize();
    System.out.println("Computed symlink target path " + ArchiveUtil.slashify(normalizedSymLinkPath) + " for symlink "
        + source + " relative to " + baseDir);
    return normalizedSymLinkPath;
  }

  public static String slashify(Path path) {
    String pathString = path.toString();
    if (File.separatorChar == '/') {
      return pathString;
    } else {
      return pathString.replace(File.separatorChar, '/');
    }
  }
}
