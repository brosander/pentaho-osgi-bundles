/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2014 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.osgi.platform.plugin.deployer.impl;

import org.pentaho.osgi.platform.plugin.deployer.api.ManifestUpdater;
import org.pentaho.osgi.platform.plugin.deployer.api.PluginMetadata;
import org.pentaho.osgi.platform.plugin.deployer.impl.handlers.pluginxml.PluginXmlStaticPathsHandler;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by bryan on 8/26/14.
 */
public class PluginMetadataImpl implements PluginMetadata {
  private final ManifestUpdater manifestUpdater = new ManifestUpdaterImpl();
  private final Document blueprint;
  private final File rootDirectory;

  public PluginMetadataImpl( File rootDirectory ) throws ParserConfigurationException {
    blueprint = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    blueprint.appendChild( blueprint.createElementNS( PluginXmlStaticPathsHandler.BLUEPRINT_BEAN_NS, "blueprint" ) );
    this.rootDirectory = rootDirectory;
  }

  @Override public ManifestUpdater getManifestUpdater() {
    return manifestUpdater;
  }

  @Override public Document getBlueprint() {
    return blueprint;
  }

  @Override public void writeBlueprint( OutputStream outputStream ) throws IOException {
    Result output = new StreamResult( outputStream );
    Source input = new DOMSource( getBlueprint() );
    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
      transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );
      transformer.transform( input, output );
    } catch ( TransformerException e ) {
      throw new IOException( e );
    }
  }

  @Override public FileWriter getFileWriter( String path ) throws IOException {
    File resultFile = new File( rootDirectory.getAbsolutePath() + "/" + path );
    File parentDir = resultFile.getParentFile();
    int tries = 100;
    while ( !parentDir.exists() && tries-- > 0 ) {
      parentDir.mkdirs();
    }
    return new FileWriter( resultFile );
  }
}
