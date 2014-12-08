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

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/15/14.
 */
public class RequireJsConfigManagerTest {
  private Bundle bundle;
  private BundleContext bundleContext;
  private RequireJsConfigManager requireJsConfigManager;

  @Before
  public void setup() {
    bundle = mock( Bundle.class );
    bundleContext = mock( BundleContext.class );
    when( bundleContext.getBundle() ).thenReturn( bundle );
    when( bundleContext.getBundles() ).thenReturn( new Bundle[] { bundle } );
    requireJsConfigManager = new RequireJsConfigManager();
    requireJsConfigManager.setBundleContext( bundleContext );
  }

  @Test
  public void testSetBundleContext() {
    assertEquals( bundleContext, requireJsConfigManager.getBundleContext() );
  }

  @Test
  public void testInit() throws Exception {
    BundleEvent bundleEvent = mock( BundleEvent.class );
    requireJsConfigManager.init();
  }

  @Test
  public void testSetLastModified() {
    long lastModified = 100L;
    requireJsConfigManager.setLastModified( lastModified );
    assertEquals( lastModified, requireJsConfigManager.getLastModified() );
  }

  @Test
  public void testUpdateBundleContext() throws IOException, ParseException {
    when( bundle.getResource( RequireJsConfigManager.REQUIRE_JSON_PATH ) ).thenReturn( this.getClass().getClassLoader()
        .getResource( "org/pentaho/js/require/RequireJsConfigManagerTest.testUpdateBundleContext.json" ) );
    assertTrue( requireJsConfigManager.updateBundleContext( bundle ) );
  }

  @Test
  public void testUpdateBundleContextStopped() throws IOException, ParseException {
    when( bundle.getBundleId() ).thenReturn( 1L );
    when( bundle.getResource( RequireJsConfigManager.REQUIRE_JSON_PATH ) ).thenReturn( this.getClass().getClassLoader()
        .getResource( "org/pentaho/js/require/RequireJsConfigManagerTest.testUpdateBundleContextStopped.json" ) );
    requireJsConfigManager.updateBundleContext( bundle );
    assertTrue( requireJsConfigManager.updateBundleContextStopped( bundle ) );
  }

  @Test
  public void testBundleChangedNoException() {
    requireJsConfigManager.bundleChanged( bundle );
  }

  @Test
  public void testBundleChangedException() {
    when( bundle.getResource( RequireJsConfigManager.REQUIRE_JSON_PATH ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        throw new IOException();
      }
    } );
    requireJsConfigManager.bundleChanged( bundle );
  }

  @Test
  public void testGetRequireJsConfig() throws IOException, ParseException {
    when( bundle.getResource( RequireJsConfigManager.REQUIRE_JSON_PATH ) ).thenReturn( this.getClass().getClassLoader()
        .getResource( "org/pentaho/js/require/RequireJsConfigManagerTest.testGetRequireJsConfig.json" ) );
    requireJsConfigManager.updateBundleContext( bundle );
    requireJsConfigManager.invalidateCache( true );
    String config = requireJsConfigManager.getRequireJsConfig();
    if ( config.endsWith( ";" ) ) {
      config = config.substring( 0, config.length() - 1 );
    }
    RebuildCacheCallableTest.testEquals( JSONValue.parse( new InputStreamReader( this.getClass().getClassLoader()
            .getResourceAsStream( "org/pentaho/js/require/RequireJsConfigManagerTest.testGetRequireJsConfig.json" ) ) ),
        JSONValue.parse( config ) );
  }

  @Test
  /**
   * Ensure that all paths provided in absolute form are converted to relative.
   */
  public void testGetRequireJsConfigRel() throws IOException, ParseException {
    when( bundle.getResource( RequireJsConfigManager.REQUIRE_JSON_PATH ) ).thenReturn( this.getClass().getClassLoader()
        .getResource( "org/pentaho/js/require/RequireJsConfigManagerTest.testGetRequireJsConfigRel.abs.json" ) );
    requireJsConfigManager.updateBundleContext( bundle );
    requireJsConfigManager.invalidateCache( true );
    String config = requireJsConfigManager.getRequireJsConfig();
    if ( config.endsWith( ";" ) ) {
      config = config.substring( 0, config.length() - 1 );
    }

    RebuildCacheCallableTest.testEquals( JSONValue.parse( new InputStreamReader( this.getClass().getClassLoader()
            .getResourceAsStream(
                "org/pentaho/js/require/RequireJsConfigManagerTest.testGetRequireJsConfigRel.rel.json" ) ) ),
        JSONValue.parse( config ) );
  }

  @Test
  public void testExternalResources() throws IOException, ParseException {
    when( bundle.getResource( RequireJsConfigManager.EXTERNAL_RESOURCES_JSON_PATH ) )
        .thenReturn( this.getClass().getClassLoader()
            .getResource( "org/pentaho/js/require/RequireJsConfigManagerTest.testExternalResources.json" ) );
    requireJsConfigManager.updateBundleContext( bundle );
    when( bundle.getResource( "/common-ui/resources/web/common-ui-require-js-cfg.js" ) )
        .thenReturn( this.getClass().getClassLoader()
            .getResource( "org/pentaho/js/require/RequireJsConfigManagerTest.testExternalResources.internal" ) );
    requireJsConfigManager.invalidateCache( true );
    String config = requireJsConfigManager.getRequireJsConfig();
    assertTrue( config.contains( "internal_test" ) );
  }


  @Test
  public void testContextRoot() throws IOException, ParseException {
    String[] contextPermutations = new String[] { "fake/root", "/fake/root", "fake/root/" };
    for ( String contextPermutation : contextPermutations ) {
      requireJsConfigManager.setContextRoot( contextPermutation );
      assertEquals( "/fake/root/", requireJsConfigManager.getContextRoot() );
    }
  }


}
