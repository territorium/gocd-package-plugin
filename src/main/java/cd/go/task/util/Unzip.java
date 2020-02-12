/*
 * Copyright (c) 2001-2019 Territorium Online Srl / TOL GmbH. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as defined in and that are
 * subject to the Territorium Online License Version 1.0. You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at http://www.tol.info/license/
 * and read it before using this file.
 *
 * The Original Code and all software distributed under the License are distributed on an 'AS IS'
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TERRITORIUM ONLINE HEREBY
 * DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the License for
 * the specific language governing rights and limitations under the License.
 */

package cd.go.task.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The {@link Unzip} class.
 */
public abstract class Unzip {

  /**
   * Constructs an instance of {@link Unzip}.
   */
  private Unzip() {}

  /**
   * @throws IOException
   * 
   */
  public static boolean unpack(File file, File target) throws IOException {
    byte[] buffer = new byte[4096];
    try (ZipInputStream stream = new ZipInputStream(new FileInputStream(file))) {
      ZipEntry entry = stream.getNextEntry();
      while (entry != null) {
        File newFile = newFile(target, entry);
        if (entry.isDirectory()) {
          newFile.mkdirs();
        } else {
          newFile.getParentFile().mkdirs();
          try (FileOutputStream fos = new FileOutputStream(newFile)) {
            int len;
            while ((len = stream.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        stream.closeEntry();
        entry = stream.getNextEntry();
      }
      stream.closeEntry();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return true;
  }

  // Files.move(new File("/tmp/test").toPath(), new File("/tmp/test2").toPath(),
  // StandardCopyOption.REPLACE_EXISTING);

  public static void main(String... args) throws IOException {
    unpack(
        new File(
            "/var/lib/go-agent/pipelines/smartIO-Installer-Develop/download/tol.app.web/smartIO-Web-20.x.43-18304.zip"),
        new File("/tmp/test"));
  }

  private static File newFile(File target, ZipEntry entry) throws IOException {
    File file = new File(target, entry.getName());
    String targetPath = target.getCanonicalPath();
    String targetFilePath = file.getCanonicalPath();

    if (!targetFilePath.startsWith(targetPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + entry.getName());
    }
    return file;
  }
}
