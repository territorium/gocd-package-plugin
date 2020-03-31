/**
 * 
 */

package cd.go.common.archive;


import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gzipped Tar archiver which preserves
 * 
 * <ul> <li>POSIX file permissions</li> <li>Symbolic links (if the link target points inside the
 * archive)</li> <li>Last modification timestamp</li> </ul>
 * 
 * in the archive as found in the filesystem for files to be archived. It uses GNU tar format
 * extensions for archive entries with path length > 100.
 */
class ArchiverTar extends Archiver {

  /**
   * Creates a .tar.gz file
   * 
   * @param file
   * @param name
   */
  public ArchiverTar(File file, String name) {
    super(file, name);
  }

  @Override
  public LocalDateTime extract(File target) throws IOException {
    LocalDateTime local = null;
    Map<File, String> symLinks = new HashMap<>();
    try (TarArchiveInputStream stream = new TarArchiveInputStream(getInputStream())) {
      TarArchiveEntry entry = stream.getNextTarEntry();
      while (entry != null) {
        LocalDateTime date =
            Instant.ofEpochMilli(entry.getLastModifiedDate().getTime()).atOffset(ZoneOffset.UTC).toLocalDateTime();
        if (local == null || date.isAfter(local))
          local = date;

        File newFile = newFile(target, entry.getName());
        if (entry.isDirectory()) {
          newFile.mkdirs();
        } else if (entry.isSymbolicLink()) {
          symLinks.put(newFile, entry.getLinkName());
        } else {
          newFile.getParentFile().mkdirs();
          streamToFile(newFile, stream);
          newFile.setLastModified(entry.getLastModifiedDate().getTime());
          newFile.setExecutable(PosixPerms.isExecuteable(entry.getMode()));
        }
        entry = stream.getNextTarEntry();
      }

      for (File file : symLinks.keySet()) {
        Files.createSymbolicLink(file.toPath(), Paths.get(symLinks.get(file)));
      }

    }
    return local;
  }

  @Override
  public final void archive(List<File> files) throws IOException {
    System.out.println("Building tar: " + getFile());
    getFile().getAbsoluteFile().getParentFile().mkdirs();

    try (TarArchiveOutputStream stream = new TarArchiveOutputStream(getOutputStream(), "UTF-8")) {
      // allow "long" file paths (> 100 chars)
      stream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
      for (File file : files) {
        addToArchive(file.getParentFile(), file, stream);
      }
    }
  }

  private final void addToArchive(File root, File source, TarArchiveOutputStream stream) throws IOException {
    TarArchiveEntry entry = createEntry(root, source);
    PosixFileAttributes posix = getAttributes(source);
    if (posix != null) {
      entry.setMode(PosixPerms.toOctalFileMode(posix.permissions()));
    }
    entry.setModTime(source.lastModified());

    stream.putArchiveEntry(entry);
    if (source.isFile() && !entry.isSymbolicLink()) {
      Archiver.fileToStream(source, stream);
    }
    stream.closeArchiveEntry();
    if (source.isDirectory() && (!entry.isSymbolicLink() || !resolvesBelow(source, root))) {
      File[] children = source.listFiles();
      if (children != null) {
        for (File child : children) {
          addToArchive(root, child, stream);
        }
      }
    }
  }

  private TarArchiveEntry createEntry(File root, File source) throws IOException {
    String path = slashify(root.toPath().relativize(source.toPath()));
    System.out.println("Adding entry " + path);

    if (isSymbolicLink(source) && resolvesBelow(source, root)) {
      // only create symlink entry if link target is inside archive
      TarArchiveEntry entry = new TarArchiveEntry(path, TarArchiveEntry.LF_SYMLINK);
      entry.setLinkName(slashify(getRelativeSymLinkTarget(source, source.getParentFile())));
      return entry;
    }
    return new TarArchiveEntry(source, path);
  }

  private static String slashify(Path path) {
    String pathString = path.toString();
    if (File.separatorChar == '/') {
      return pathString;
    } else {
      return pathString.replace(File.separatorChar, '/');
    }
  }

  private PosixFileAttributes getAttributes(File source) {
    PosixFileAttributeView fileAttributeView =
        Files.getFileAttributeView(source.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
    if (fileAttributeView == null) {
      return null;
    }
    PosixFileAttributes attrs;
    try {
      attrs = fileAttributeView.readAttributes();
    } catch (IOException e) {
      return null;
    }
    return attrs;
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
