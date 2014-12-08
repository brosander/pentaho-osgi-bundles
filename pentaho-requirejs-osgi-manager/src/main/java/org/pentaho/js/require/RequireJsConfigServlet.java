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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by bryan on 8/5/14.
 */
public class RequireJsConfigServlet extends HttpServlet {
  private final String requireJs;
  private RequireJsConfigManager manager;

  public RequireJsConfigServlet() throws IOException {
    InputStream inputStream = null;
    InputStreamReader inputStreamReader = null;
    BufferedReader reader = null;
    try {
      inputStream = getClass().getClassLoader().getResourceAsStream( "js/require.js" );
      inputStreamReader = new InputStreamReader( inputStream );
      reader = new BufferedReader( inputStreamReader );
      String line = null;
      StringBuilder sb = new StringBuilder();
      while ( ( line = reader.readLine() ) != null ) {
        sb.append( line );
        sb.append( "\n" );
      }
      requireJs = sb.toString();
    } finally {
      if ( inputStreamReader != null ) {
        inputStreamReader.close();
      }
      if ( reader != null ) {
        reader.close();
      }
      if ( inputStream != null ) {
        inputStream.close();
      }
    }
  }

  public RequireJsConfigManager getManager() {
    return manager;
  }

  public void setManager( RequireJsConfigManager manager ) {
    this.manager = manager;
  }

  @Override
  protected long getLastModified( HttpServletRequest req ) {
    return manager.getLastModified();
  }

  @Override
  protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
    resp.setContentType( "text/javascript" );
    PrintWriter printWriter = new PrintWriter( resp.getOutputStream() );
    try {
      printWriter.write( requireJs );
      printWriter.write( "\nif(typeof CONTEXT_PATH == 'undefined'){\n" );
      printWriter.write( "\twindow.CONTEXT_PATH = '/';\n" );
      printWriter.write( "}\n" );
      printWriter.write( "\nrequireCfg = " );
      printWriter.write( manager.getRequireJsConfig() );
      printWriter.write( "\n" );
      String config = req.getParameter( "config" );
      printWriter.write( "requireCfg.baseUrl = '" + manager.getContextRoot() + "';\n" );
      if ( config == null || Boolean.valueOf( config ) ) {
        printWriter.write( "require.config(requireCfg);" );
      }
    } finally {
      printWriter.close();
    }
  }
}
