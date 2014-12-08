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

package org.pentaho.osgi.platform.plugin.deployer.impl.handlers.pluginxml;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.osgi.platform.plugin.deployer.api.PluginHandlingException;
import org.pentaho.osgi.platform.plugin.deployer.api.PluginMetadata;
import org.pentaho.osgi.platform.plugin.deployer.impl.JSONUtil;
import org.w3c.dom.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by bryan on 9/2/14.
 */
public class PluginXmlExternalResourcesHandlerTest {
  private JSONUtil jsonUtil;

  @Before
  public void setup() {
    jsonUtil = mock( JSONUtil.class );
  }

  @Test
  public void testHandleNoNodesDoesntDoAnything() throws PluginHandlingException {
    PluginXmlExternalResourcesHandler pluginXmlExternalResourcesHandler = new PluginXmlExternalResourcesHandler();
    pluginXmlExternalResourcesHandler.setJsonUtil( jsonUtil );
    PluginMetadata pluginMetadata = mock( PluginMetadata.class );
    List<Node> nodes = new ArrayList<Node>();
    pluginXmlExternalResourcesHandler.handle( "plugin.xml", nodes, pluginMetadata );
    verifyNoMoreInteractions( pluginMetadata );
    verifyNoMoreInteractions( jsonUtil );
  }

  @Test
  public void testHandleTwoNodes() throws PluginHandlingException, IOException {
    Map<String, String> node1Props = new HashMap<String, String>();
    node1Props.put( "context", "requirejs" );
    Map<String, String> node2Props = new HashMap<String, String>();
    node2Props.put( "context", "requirejs" );
    Node node1 = PluginXmlStaticPathsHandlerTest.makeMockNode( node1Props );
    when( node1.getTextContent() ).thenReturn( "/test/content/1" );
    Node node2 = PluginXmlStaticPathsHandlerTest.makeMockNode( node2Props );
    when( node2.getTextContent() ).thenReturn( "/test/content/2" );
    PluginXmlExternalResourcesHandler pluginXmlExternalResourcesHandler = new PluginXmlExternalResourcesHandler();
    pluginXmlExternalResourcesHandler.setJsonUtil( new JSONUtil() );
    PluginMetadata pluginMetadata = mock( PluginMetadata.class );
    FileWriter fileWriter = mock( FileWriter.class );
    final StringBuilder sb = new StringBuilder( );
    doAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        sb.append( invocation.getArguments()[0] );
        return null;
      }
    } ).when( fileWriter ).write( anyString() );
    when( pluginMetadata.getFileWriter( PluginXmlExternalResourcesHandler.EXTERNAL_RESOURCES_FILE ) ).thenReturn( fileWriter );
    List<Node> nodes = new ArrayList<Node>( Arrays.asList( node1, node2 ) );
    pluginXmlExternalResourcesHandler.handle( "plugin.xml", nodes, pluginMetadata );
    String result = sb.toString();
    JSONObject jsonObject = (JSONObject) JSONValue.parse( result );
    assertEquals( 1, jsonObject.size() );
    List<String> configs = (List<String>) jsonObject.get( "requirejs" );
    assertNotNull( configs );
    assertEquals( 2, configs.size() );
    assertEquals( "/test/content/1", configs.get( 0 ) );
    assertEquals( "/test/content/2", configs.get( 1 ) );
  }
}
