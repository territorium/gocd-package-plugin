/**
 * 
 */

package cd.go.common.archive;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Gzipped Tar archiver which preserves
 * 
 * <ul> <li>POSIX file permissions</li> <li>Symbolic links (if the link target points inside the
 * archive)</li> <li>Last modification timestamp</li> </ul>
 * 
 * in the archive as found in the filesystem for files to be archived. It uses GNU tar format
 * extensions for archive entries with path length > 100.
 */
class ArchiverZip extends Archiver {

  /**
   * Creates a .tar.gz file
   * 
   * @param file
   * @param name
   */
  public ArchiverZip(File file, String name) {
    super(file, name);
  }


  /**
   * Uncompress the provided ZIP-file to the target location
   * 
   * @param target
   */
  @Override
  public final LocalDateTime extract(File target) throws IOException {
    LocalDateTime local = null;
    try (ZipInputStream stream = new ZipInputStream(getInputStream())) {
      ZipEntry entry = stream.getNextEntry();
      while (entry != null) {
        LocalDateTime date = Instant.ofEpochMilli(entry.getTime()).atOffset(ZoneOffset.UTC).toLocalDateTime();
        if (local == null || date.isAfter(local))
          local = date;

        File newFile = newFile(target, entry.getName());
        if (entry.isDirectory()) {
          newFile.mkdirs();
        } else {
          newFile.getParentFile().mkdirs();
          streamToFile(newFile, stream);
          newFile.setLastModified(entry.getTime());
        }
        stream.closeEntry();
        entry = stream.getNextEntry();
      }
    }
    return local;
  }

  @Override
  public final void archive(List<File> files) throws IOException {
    System.out.println("Building tar: " + getFile());
    getFile().getAbsoluteFile().getParentFile().mkdirs();

    try (ZipOutputStream stream = new ZipOutputStream(getOutputStream(), Charset.defaultCharset())) {
      for (File file : files) {
        addToRecursively(file.getParentFile(), file, stream);
      }
    }
  }

  private final void addToRecursively(File tarRootDir, File source, ZipOutputStream stream) throws IOException {
    if (source.isFile()) {
      ZipEntry entry = createZipEntry(tarRootDir, source);
      stream.putNextEntry(entry);
      Archiver.fileToStream(source, stream);
    }
    if (source.isDirectory() && (!isSymbolicLink(source) || !resolvesBelow(source, tarRootDir))) {
      File[] children = source.listFiles();
      if (children != null) {
        for (File child : children) {
          addToRecursively(tarRootDir, child, stream);
        }
      }
    }
  }

  private ZipEntry createZipEntry(File tarRootDir, File source) throws IOException {
    String pathInTar = slashify(tarRootDir.toPath().relativize(source.toPath()));
    System.out.println("Adding entry " + pathInTar);
    return new ZipEntry(pathInTar);
  }

  private static String slashify(Path path) {
    String pathString = path.toString();
    if (File.separatorChar == '/') {
      return pathString;
    } else {
      return pathString.replace(File.separatorChar, '/');
    }
  }

  private boolean resolvesBelow(File source, File baseDir) throws IOException {
    return !getRelativeSymLinkTarget(source, baseDir).startsWith("..");
  }

  private Path getRelativeSymLinkTarget(File source, File baseDir) throws IOException {
    Path sourcePath = source.toPath();
    Path linkTarget = Files.readSymbolicLink(sourcePath);
    // link target may be relative, so we resolve it first
    Path resolvedLinkTarget = sourcePath.getParent().resolve(linkTarget);
    Path relative = baseDir.toPath().relativize(resolvedLinkTarget);
    Path normalizedSymLinkPath = relative.normalize();
    System.out.println("Computed symlink target path " + slashify(normalizedSymLinkPath) + " for symlink " + source
        + " relative to " + baseDir);
    return normalizedSymLinkPath;
  }

  /**
   * <code>true</code> if the file is a symbolik link.
   * 
   * @param file
   */
  private static boolean isSymbolicLink(File file) {
    return Files.isSymbolicLink(file.toPath());
  }
}
