/*
 * Copyright (c) 2001-2019 Territorium Online Srl / TOL GmbH. All Rights
 * Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as
 * defined in and that are subject to the Territorium Online License Version
 * 1.0. You may not use this file except in compliance with the License. Please
 * obtain a copy of the License at http://www.tol.info/license/ and read it
 * before using this file.
 *
 * The Original Code and all software distributed under the License are
 * distributed on an 'AS IS' basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS
 * OR IMPLIED, AND TERRITORIUM ONLINE HEREBY DISCLAIMS ALL SUCH WARRANTIES,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the
 * License for the specific language governing rights and limitations under the
 * License.
 */

package cd.go.task.util.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * The {@link PackageInfo} class.
 */
public class PackageInfo {

  private static final String VERSION      = "Version";
  private static final String RELEASE_DATE = "ReleaseDate";

  private static final Path   PACKAGE      = Paths.get("meta", "package.xml");


  private File directory;

  /**
   * Constructs an instance of {@link PackageInfo}.
   * 
   * @param directory
   */
  public PackageInfo(File directory) {
    this.directory = directory;
  }

  /**
   * Update the package info.
   * 
   * @param version
   * @param localDate
   */
  public void updatePackageInfo(Version version, LocalDate localDate) throws Exception {
    File file = directory.toPath().resolve(PACKAGE).toFile();
    String text = readPackageInfo(file, version, localDate);
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(text);
      writer.flush();
    }
  }

  /**
   * Get the updated package info string.
   * 
   * @param file
   * @param version
   * @param localDate
   */
  protected final String readPackageInfo(File file, Version version, LocalDate localDate) throws Exception {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(file));

    boolean ignore = false;
    while (reader.hasNext()) {
      XMLEvent nextEvent = reader.nextEvent();
      if (nextEvent.isStartElement()) {
        StartElement startElement = nextEvent.asStartElement();
        String name = startElement.getName().getLocalPart();

        if (name.equalsIgnoreCase(PackageInfo.VERSION)) {
          ignore = true;
          buffer.append(String.format("<%1$s>%2$s</%1$s>", PackageInfo.VERSION, version.toString("0.0.0-0")));
        } else if (name.equalsIgnoreCase(PackageInfo.RELEASE_DATE)) {
          ignore = true;
          buffer.append(String.format("<%1$s>%2$s</%1$s>", PackageInfo.RELEASE_DATE, localDate.toString()));
        } else {
          buffer.append("<");
          buffer.append(name);
          Iterator<Attribute> attrs = startElement.getAttributes();
          while (attrs.hasNext()) {
            Attribute attr = attrs.next();
            buffer.append(String.format(" %s=\"%s\"", attr.getName(), attr.getValue()));
          }
          buffer.append(">");
        }
      }
      if (nextEvent.isCharacters() && !ignore) {
        buffer.append(nextEvent.asCharacters());
        ignore = false;
      }
      if (nextEvent.isEndElement()) {
        if (!ignore) {
          String name = nextEvent.asEndElement().getName().getLocalPart();
          buffer.append("</");
          buffer.append(name);
          buffer.append(">");
        }
        ignore = false;
      }
    }
    buffer.append("\n");
    return buffer.toString();
  }
}
