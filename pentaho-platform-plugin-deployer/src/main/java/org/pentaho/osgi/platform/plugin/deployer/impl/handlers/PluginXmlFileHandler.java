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

package org.pentaho.osgi.platform.plugin.deployer.impl.handlers;

import org.pentaho.osgi.platform.plugin.deployer.api.PluginHandlingException;
import org.pentaho.osgi.platform.plugin.deployer.api.PluginMetadata;
import org.pentaho.osgi.platform.plugin.deployer.api.XmlPluginFileHandler;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by bryan on 8/29/14.
 */
public abstract class PluginXmlFileHandler extends XmlPluginFileHandler {
  public PluginXmlFileHandler( String xpath ) {
    super( xpath );
  }
  @Override public boolean handles( String fileName ) {
    if ( fileName != null ) {
      String[] splitName = fileName.split( "/" );
      if ( splitName.length == 2 && "plugin.xml".equals( splitName[ 1 ] ) ) {
        return true;
      }
    }
    return false;
  }
}
