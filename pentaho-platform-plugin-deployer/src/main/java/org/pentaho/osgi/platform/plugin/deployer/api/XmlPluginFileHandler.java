/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.osgi.platform.plugin.deployer.api;

import org.apache.cxf.helpers.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 8/26/14.
 */
public abstract class XmlPluginFileHandler implements PluginFileHandler {
  private final String[] interestedPath;

  protected XmlPluginFileHandler( String... interestedPath ) {
    this.interestedPath = interestedPath;
  }

  @Override public void handle( String relativePath, File file, PluginMetadata pluginMetadata )
    throws PluginHandlingException {
    try {
      handle( relativePath, getNodes( XMLUtils.parse( file ), 0 ), pluginMetadata );
    } catch ( Exception e ) {
      throw new PluginHandlingException( e );
    }
  }

  protected abstract void handle( String relativePath, List<Node> nodes, PluginMetadata pluginMetadata )
    throws PluginHandlingException;

  protected List<Node> getNodes( Node node, int startIndex ) {
    List<Node> result = new ArrayList<Node>();
    for ( String part : interestedPath ) {
      NodeList nodeList = node.getChildNodes();
      if ( nodeList != null ) {
        for ( int i = 0; i < nodeList.getLength(); i++ ) {
          Node potential = nodeList.item( i );
          if ( part.equals( potential.getLocalName() ) ) {
            if ( startIndex == interestedPath.length - 1 ) {
              result.add( nodeList.item( i ) );
            } else {
              result.addAll( getNodes( nodeList.item( i ), startIndex + 1 ) );
            }
          }
        }
      }
    }
    return result;
  }

  protected Map<String, String> getAttributes( Node node ) {
    Map<String, String> result = new HashMap<String, String>();
    NamedNodeMap namedNodeMap = node.getAttributes();
    for ( int i = 0; i < namedNodeMap.getLength(); i++ ) {
      Attr attr = (Attr) namedNodeMap.item( i );
      result.put( attr.getName(), attr.getValue() );
    }
    return result;
  }

  protected String camelCaseJoin( String str ) {
    StringBuilder sb = new StringBuilder();
    for ( String elem : str.split( "[^A-Za-z]" ) ) {
      String trimmed = elem.trim();
      if ( trimmed.length() > 0 ) {
        if ( sb.length() > 0 ) {
          sb.append( trimmed.substring( 0, 1 ).toUpperCase() );
        } else {
          sb.append( trimmed.substring( 0, 1 ).toLowerCase() );
        }
        if ( trimmed.length() > 1 ) {
          sb.append( trimmed.substring( 1 ) );
        }
      }
    }
    return sb.toString();
  }

  protected void setAttribute( Document document, Node node, String attribute, String value ) {
    Attr attr = document.createAttribute( attribute );
    attr.setValue( value );
    node.getAttributes().setNamedItem( attr );
  }
}
