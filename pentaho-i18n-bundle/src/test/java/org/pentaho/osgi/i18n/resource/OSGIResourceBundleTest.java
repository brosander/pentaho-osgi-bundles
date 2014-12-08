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

package org.pentaho.osgi.i18n.resource;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by bryan on 9/5/14.
 */
public class OSGIResourceBundleTest {
  @Test
  public void testNoParent() throws IOException {
    OSGIResourceBundle osgiResourceBundle = new OSGIResourceBundle(
      getClass().getClassLoader().getResource( "org/pentaho/osgi/resource/OSGIResourceBundleTest.properties" ) );
    assertEquals( "testValue", osgiResourceBundle.getString( "key" ) );
    assertEquals( "testValueParent", osgiResourceBundle.getString( "parentKey" ) );
  }

  @Test
  public void testParent() throws IOException {
    OSGIResourceBundle osgiResourceBundle = new OSGIResourceBundle(
      getClass().getClassLoader().getResource( "org/pentaho/osgi/resource/OSGIResourceBundleTest.properties" ) );
    OSGIResourceBundle osgiResourceBundleChild = new OSGIResourceBundle( osgiResourceBundle,
      getClass().getClassLoader().getResource( "org/pentaho/osgi/resource/OSGIResourceBundleTestChild.properties" ) );
    assertEquals( "testValueChild", osgiResourceBundleChild.getString( "key" ) );
    assertEquals( "testValueParent", osgiResourceBundleChild.getString( "parentKey" ) );
  }
}
