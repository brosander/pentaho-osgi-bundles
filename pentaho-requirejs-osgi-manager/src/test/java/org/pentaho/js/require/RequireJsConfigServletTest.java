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

package org.pentaho.js.require;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/15/14.
 */
public class RequireJsConfigServletTest {
  private RequireJsConfigManager requireJsConfigManager;
  private RequireJsConfigServlet requireJsConfigServlet;

  @Before
  public void setup() throws IOException {
    requireJsConfigManager = mock( RequireJsConfigManager.class );
    requireJsConfigServlet = new RequireJsConfigServlet();
    requireJsConfigServlet.setManager( requireJsConfigManager );
  }

  @Test
  public void testGetManager() throws IOException {
    assertEquals( requireJsConfigManager, requireJsConfigServlet.getManager() );
  }

  @Test
  public void testGetLastModified() {
    when( requireJsConfigManager .getLastModified() ).thenReturn( 10L );
    assertEquals( 10L, requireJsConfigServlet.getLastModified( null ) );
  }

  @Test
  public void testDoGetWithConfig() throws ServletException, IOException {
    HttpServletRequest request = mock( HttpServletRequest.class );
    HttpServletResponse response = mock( HttpServletResponse.class );
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(  );
    when( response.getOutputStream() ).thenReturn( new ServletOutputStream() {
      @Override public void write( int b ) throws IOException {
        outputStream.write( b );
      }
    } );
    String testConfig = "TEST_CONFIG";
    when( requireJsConfigManager.getRequireJsConfig() ).thenReturn( testConfig );
    requireJsConfigServlet.doGet( request, response );
    assertTrue( outputStream.toString( "UTF-8" ).contains( testConfig ) );
    assertTrue( outputStream.toString( "UTF-8" ).endsWith( "require.config(requireCfg);" ) );
  }

  @Test
  public void testDoGetWithConfigTrue() throws ServletException, IOException {
    HttpServletRequest request = mock( HttpServletRequest.class );
    HttpServletResponse response = mock( HttpServletResponse.class );
    when( request.getParameter( "config" ) ).thenReturn( "true" );
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(  );
    when( response.getOutputStream() ).thenReturn( new ServletOutputStream() {
      @Override public void write( int b ) throws IOException {
        outputStream.write( b );
      }
    } );
    String testConfig = "TEST_CONFIG";
    when( requireJsConfigManager.getRequireJsConfig() ).thenReturn( testConfig );
    requireJsConfigServlet.doGet( request, response );
    assertTrue( outputStream.toString( "UTF-8" ).contains( testConfig ) );
    assertTrue( outputStream.toString( "UTF-8" ).endsWith( "require.config(requireCfg);" ) );
  }

  @Test
  public void testDoGetWithoutConfig() throws ServletException, IOException {
    HttpServletRequest request = mock( HttpServletRequest.class );
    when( request.getParameter( "config" ) ).thenReturn( "false" );
    HttpServletResponse response = mock( HttpServletResponse.class );
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(  );
    when( response.getOutputStream() ).thenReturn( new ServletOutputStream() {
      @Override public void write( int b ) throws IOException {
        outputStream.write( b );
      }
    } );
    String testConfig = "TEST_CONFIG";
    when( requireJsConfigManager.getRequireJsConfig() ).thenReturn( testConfig );
    requireJsConfigServlet.doGet( request, response );
    assertTrue( outputStream.toString( "UTF-8" ).contains( testConfig ) );
    assertFalse( outputStream.toString( "UTF-8" ).endsWith( "require.config(requireCfg);" ) );
  }




  @Test
  public void testSetContextRoot() throws ServletException, IOException {

    HttpServletRequest request = mock( HttpServletRequest.class );
    when( request.getParameter( "config" ) ).thenReturn( "false" );
    HttpServletResponse response = mock( HttpServletResponse.class );
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(  );
    when( response.getOutputStream() ).thenReturn( new ServletOutputStream() {
      @Override public void write( int b ) throws IOException {
        outputStream.write( b );
      }
    } );
    String testConfig = "TEST_CONFIG";
    when( requireJsConfigManager.getRequireJsConfig() ).thenReturn( testConfig );
    when( requireJsConfigManager.getContextRoot() ).thenReturn("/test/root/");

    requireJsConfigServlet.doGet( request, response );
    String output = outputStream.toString( "UTF-8" );
    assertTrue( output.contains( "requireCfg.baseUrl = '/test/root/" ) );

  }
}
