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

package org.pentaho.osgi.platform.plugin.deployer;

import org.apache.karaf.util.DeployerUtils;
import org.pentaho.osgi.platform.plugin.deployer.api.PluginFileHandler;
import org.pentaho.osgi.platform.plugin.deployer.impl.PluginZipFileProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.apache.karaf.util.MvnUtils.getMvnPath;

/**
 * Created by bryan on 8/27/14.
 */
public class PlatformPluginBundlingURLConnection extends URLConnection {
  public static final int TEN_MEGABYTES = 10 * 1024 * 1024;
  private static final Pattern maxSizePattern = Pattern.compile( "maxSize=([0-9]+)" );
  private final List<PluginFileHandler> pluginFileHandlers;

  public PlatformPluginBundlingURLConnection( URL u, List<PluginFileHandler> pluginFileHandlers ) {
    super( u );
    this.pluginFileHandlers = pluginFileHandlers;
  }

  public static int getMaxSize( String query ) {
    if ( query != null ) {
      Matcher matcher = maxSizePattern.matcher( query );
      if ( matcher.matches() ) {
        return Integer.parseInt( matcher.group( 1 ) );
      }
    }
    return TEN_MEGABYTES;
  }

  @Override public void connect() throws IOException {
    //Noop
  }

  @Override public InputStream getInputStream() throws IOException {
    final ExceptionPipedInputStream pipedInputStream =
      new ExceptionPipedInputStream( getMaxSize( getURL().getQuery() ) );
    String mvnPath = getMvnPath( getURL() );
    int lastSlash = mvnPath.lastIndexOf( '/' );
    if ( lastSlash >= 0 ) {
      mvnPath = mvnPath.substring( lastSlash + 1 );
    }
    final String[] nameVersion = DeployerUtils.extractNameVersionType( mvnPath );
    final PipedOutputStream pipedOutputStream = new PipedOutputStream( pipedInputStream );
    final ZipOutputStream zipOutputStream = new ZipOutputStream( pipedOutputStream );
    URLConnection connection = getURL().openConnection();
    InputStream connectionInputStream = connection.getInputStream();
    ZipInputStream zipInputStream = new ZipInputStream( connectionInputStream );
    final PluginZipFileProcessor pluginZipFileProcessor =
      new PluginZipFileProcessor( pluginFileHandlers, nameVersion[ 0 ], nameVersion[ 0 ], nameVersion[ 1 ] );
    pluginZipFileProcessor.processBackground( Executors.newSingleThreadExecutor(), zipInputStream, zipOutputStream,
      pipedInputStream );
    return pipedInputStream;
  }
}
