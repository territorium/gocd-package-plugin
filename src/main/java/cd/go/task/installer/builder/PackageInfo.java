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

package cd.go.task.installer.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import cd.go.common.util.Environment;
import cd.go.task.installer.Constants;

/**
 * The {@link PackageInfo} is a helper class to manage the meta/package.xml file.
 *
 * <pre>
 * <?xml version="1.0"?>
 * <Package>
 *     <DisplayName>TOL Package</DisplayName>
 *     <Description>TOL Skeleton Package</Description>
 *     <Version>1.2.3</Version>
 *     <ReleaseDate>2009-04-23</ReleaseDate>
 *     <Name>com.vendor.root.component2</Name>
 *     <Dependencies>com.vendor.root.component1</Dependencies>
 * </Package>
 * </pre>
 */
class PackageInfo {


  private static final String VERSION      = "Version";
  private static final String RELEASE_DATE = "ReleaseDate";

  private static final Path   PACKAGE      = Paths.get("meta", "package.xml");


  private final Environment env;

  /**
   * Constructs an instance of {@link PackageInfo}.
   *
   * @param env
   */
  PackageInfo(Environment env) {
    this.env = env;
  }

  /**
   * Update the package info.
   *
   * @param name
   * @param workingDir
   */
  void updatePackageInfo(String name, LocalDate releaseDate, File workingDir) throws IOException {
    File file = new File(workingDir, name).toPath().resolve(PackageInfo.PACKAGE).toFile();
    String text = readPackageInfo(file, releaseDate);
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
  protected final String readPackageInfo(File file, LocalDate releaseDate) throws IOException {
    Version version = this.env.isSet(Constants.ENV_VERSION) ? Version.parse(this.env.get(Constants.ENV_VERSION)) : null;
    String relaseDate = releaseDate.toString();

    StringBuffer pattern = null;
    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

    try {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(file));
      while (reader.hasNext()) {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isStartElement()) {
          StartElement startElement = nextEvent.asStartElement();
          String name = startElement.getName().getLocalPart();

          if (name.equalsIgnoreCase(PackageInfo.VERSION) && (version != null)) {
            pattern = new StringBuffer();
          } else if (name.equalsIgnoreCase(PackageInfo.RELEASE_DATE) && (relaseDate != null)) {
            pattern = new StringBuffer();
          } else {
            buffer.append("<");
            buffer.append(name);
            Iterator<?> attrs = startElement.getAttributes();
            while (attrs.hasNext()) {
              Attribute attr = (Attribute) attrs.next();
              buffer.append(String.format(" %s=\"%s\"", attr.getName(), attr.getValue()));
            }
            buffer.append(">");
          }
        }

        if (nextEvent.isCharacters()) {
          if (pattern == null) {
            buffer.append(nextEvent.asCharacters());
          } else {
            pattern.append(nextEvent.asCharacters());
          }
        }

        if (nextEvent.isEndElement()) {
          EndElement endElement = nextEvent.asEndElement();
          String name = endElement.getName().getLocalPart();

          if (name.equalsIgnoreCase(PackageInfo.VERSION) && (version != null)) {
            String text = version.toString(pattern.toString());
            buffer.append(String.format("<%1$s>%2$s</%1$s>", PackageInfo.VERSION, text));
          } else if (name.equalsIgnoreCase(PackageInfo.RELEASE_DATE) && (relaseDate != null)) {
            buffer.append(String.format("<%1$s>%2$s</%1$s>", PackageInfo.RELEASE_DATE, relaseDate));
          }
          if (pattern == null) {
            buffer.append("</");
            buffer.append(name);
            buffer.append(">");
          }
          pattern = null;
        }
      }
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    buffer.append("\n");
    return buffer.toString();
  }
}
