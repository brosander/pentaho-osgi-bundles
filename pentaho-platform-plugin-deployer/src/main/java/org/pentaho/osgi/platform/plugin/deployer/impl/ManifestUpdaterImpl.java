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

import org.pentaho.osgi.platform.plugin.deployer.PlatformPluginBundlingURLConnection;
import org.pentaho.osgi.platform.plugin.deployer.api.ManifestUpdater;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 8/26/14.
 */
public class ManifestUpdaterImpl implements ManifestUpdater {
  private final Map<String, String> imports;
  private final Set<String> exportServices;

  public ManifestUpdaterImpl() {
    imports = new HashMap<String, String>();
    exportServices = new HashSet<String>();
  }

  @Override public Map<String, String> getImports() {
    return imports;
  }

  @Override public Set<String> getExportServices() {
    return exportServices;
  }

  @Override public void write( java.util.jar.Manifest originalManifest, OutputStream outputStream, String name,
                               String symbolicName, String version ) throws IOException {
    java.util.jar.Manifest newManifest = new java.util.jar.Manifest(  );
    if ( originalManifest != null ) {
      newManifest = new java.util.jar.Manifest( originalManifest );
    }
    newManifest.getMainAttributes().putValue( "Manifest-Version", "2" );
    newManifest.getMainAttributes().putValue( "Bundle-ManifestVersion", "2" );
    newManifest.getMainAttributes().putValue( "Bundle-SymbolicName", symbolicName );
    newManifest.getMainAttributes().putValue( "Bundle-Name", name );
    newManifest.getMainAttributes().putValue( "Bundle-Version", version );
    newManifest.getMainAttributes().putValue( "Bundle-Blueprint", PluginZipFileProcessor.BLUEPRINT );
    newManifest.getMainAttributes()
      .putValue( "Export-Service", join( getExportServices(), "," ) );
    newManifest.getMainAttributes()
      .putValue( "Import-Package", getImportString( ) );
    newManifest.write( outputStream );
  }

  protected String join( Collection<String> collection, String delimiter ) {
    StringBuilder sb = new StringBuilder();
    for ( String value : collection ) {
      sb.append( value );
      sb.append( delimiter );
    }
    if ( sb.length() > 0 ) {
      sb.setLength( sb.length() - delimiter.length() );
    }
    return sb.toString();
  }

  protected String getImportString( ) {
    Set<String> imports = new HashSet<String>();
    for ( Map.Entry<String, String> entry : getImports().entrySet() ) {
      String importString = entry.getKey();
      String version = entry.getValue();
      if ( version != null ) {
        importString += ";version=\"" + entry.getValue() + "\"";
      }
      imports.add( importString );
    }
    List<String> importList = new ArrayList<String>( imports );
    Collections.sort( importList );
    return join( importList, "," );
  }
}
