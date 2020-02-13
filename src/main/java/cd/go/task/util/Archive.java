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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * The {@link Archive} class.
 */
public abstract class Archive {

  /**
   * Constructs an instance of {@link Archive}.
   */
  private Archive() {}

  /**
   * @throws IOException
   * 
   */
  public static boolean unzip(File file, File target) throws IOException {
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


  public static void main(String... args) throws Throwable {
    // File file = new File("/tmp/test/smartIO-Web-20.0.43-18304.zip");
    // unzip(file, new File("/tmp/test"));
    // Version version = Version.parse(file.getName());
    // System.out.println(Version.parse(file.getName()));
    // File target = new File("/tmp/test/data/webapps/client/smartio-" +
    // version.toString("0.0.0")+"-Build-"+version.getBuild());
    // target.getParentFile().mkdirs();
    // Files.move(new File("/tmp/test/smartio").toPath(), target.toPath(),
    // StandardCopyOption.REPLACE_EXISTING);


    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader = xmlInputFactory.createXMLEventReader(
        new FileInputStream(new File("/data/smartIO/develop/installer/packages2/tol/meta/package.xml")));

    while (reader.hasNext()) {
      XMLEvent nextEvent = reader.nextEvent();
      if (nextEvent.isStartElement()) {
        StartElement startElement = nextEvent.asStartElement();
        System.out.print("<" + startElement.getName().getLocalPart() + ">");
        startElement.getAttributes().forEachRemaining(a -> System.out
            .print(" " + ((Attribute) a).getName().getLocalPart() + "=\"" + ((Attribute) a).getValue() + "\""));
      }
      if (nextEvent.isCharacters()) {
        System.out.print(nextEvent.asCharacters());
      }
      if (nextEvent.isEndElement()) {
        EndElement endElement = nextEvent.asEndElement();
        System.out.print("</" + endElement.getName().getLocalPart() + ">");
      }
    }

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
