/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package cd.go.common.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The {@link Archive} provides utilities to handle different kind of archives..
 */
public abstract class Archive {


  /* Bits used in the mode field, values in octal. */
  public static final int TSUID   = 04000; /* set UID on execution */
  public static final int TSGID   = 02000; /* set GID on execution */
  public static final int TSVTX   = 01000; /* reserved */
  /* file permissions */
  public static final int TUREAD  = 00400; /* read by owner */
  public static final int TUWRITE = 00200; /* write by owner */
  public static final int TUEXEC  = 00100; /* execute/search by owner */
  public static final int TGREAD  = 00040; /* read by group */
  public static final int TGWRITE = 00020; /* write by group */
  public static final int TGEXEC  = 00010; /* execute/search by group */
  public static final int TOREAD  = 00004; /* read by other */
  public static final int TOWRITE = 00002; /* write by other */
  public static final int TOEXEC  = 00001; /* execute/search by other */

  /**
   * Uncompress the provided ZIP-file.
   * 
   * @param file
   */
  public static LocalDateTime unpack(File file) throws IOException {
    if (file.getName().toLowerCase().endsWith(".zip") || file.getName().toLowerCase().endsWith(".war")
        || file.getName().toLowerCase().endsWith(".jar")) {
      String name = file.getName().substring(0, file.getName().length() - 4);
      return Archive.unzip(file, new File(file.getParentFile(), name));
    } else if (file.getName().toLowerCase().endsWith(".tar.gz")) {
      String name = file.getName().substring(0, file.getName().length() - 7);
      return Archive.untargz(file, new File(file.getParentFile(), name));
    } else if (file.getName().toLowerCase().endsWith(".tar")) {
      String name = file.getName().substring(0, file.getName().length() - 4);
      return Archive.untar(file, new File(file.getParentFile(), name));
    }

    throw new IOException("Unsupported compression format for " + file.getName());
  }

  /**
   * Uncompress the provided ZIP-file to the target location
   * 
   * @param file
   * @param target
   */
  public static LocalDateTime unzip(File file, File target) throws IOException {
    LocalDateTime local = null;
    try (ZipInputStream stream = new ZipInputStream(new FileInputStream(file))) {
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
          writeToFile(newFile, stream);
          newFile.setLastModified(entry.getTime());
        }
        stream.closeEntry();
        entry = stream.getNextEntry();
      }
    }
    return local;
  }

  /**
   * Uncompress the provided ZIP-file to the target location
   * 
   * @param file
   * @param target
   */
  public static LocalDateTime untar(File file, File target) throws IOException {
    LocalDateTime local = null;
    try (TarArchiveInputStream stream = new TarArchiveInputStream(new FileInputStream(file))) {
      TarArchiveEntry entry = stream.getNextTarEntry();
      while (entry != null) {
        LocalDateTime date =
            Instant.ofEpochMilli(entry.getLastModifiedDate().getTime()).atOffset(ZoneOffset.UTC).toLocalDateTime();
        if (local == null || date.isAfter(local))
          local = date;

        File newFile = newFile(target, entry.getName());
        if (entry.isDirectory()) {
          newFile.mkdirs();
        } else {
          newFile.getParentFile().mkdirs();
          writeToFile(newFile, stream);
          newFile.setLastModified(entry.getLastModifiedDate().getTime());
          newFile.setExecutable(
              (entry.getMode() & TUEXEC) > 0 || (entry.getMode() & TGEXEC) > 0 || (entry.getMode() & TOEXEC) > 0);
        }
        entry = stream.getNextTarEntry();
      }
    }
    return local;
  }

  /**
   * Uncompress the provided ZIP-file to the target location
   * 
   * @param file
   * @param target
   */
  public static LocalDateTime untargz(File file, File target) throws IOException {
    File tar = new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - 3));
    try (GzipCompressorInputStream istream = new GzipCompressorInputStream(new FileInputStream(file))) {
      final byte[] buffer = new byte[4096];
      int n = 0;
      try (OutputStream ostream = new FileOutputStream(tar)) {
        while (-1 != (n = istream.read(buffer))) {
          ostream.write(buffer, 0, n);
        }
      }
    }
    return untar(tar, target);
  }

  private static File newFile(File target, String name) throws IOException {
    File file = new File(target, name);
    String targetPath = target.getCanonicalPath();
    String targetFilePath = file.getCanonicalPath();

    if (!targetFilePath.startsWith(targetPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + name);
    }
    return file;
  }

  /**
   * Write the {@link InputStream} to {@link File}
   *
   * @param file
   * @param stream
   */
  private static void writeToFile(File file, InputStream stream) throws IOException {
    byte[] buffer = new byte[4096];
    try (FileOutputStream output = new FileOutputStream(file)) {
      int len;
      while ((len = stream.read(buffer)) > 0) {
        output.write(buffer, 0, len);
      }
    }
  }
}
