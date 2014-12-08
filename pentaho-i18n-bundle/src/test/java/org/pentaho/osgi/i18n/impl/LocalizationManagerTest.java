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

package org.pentaho.osgi.i18n.impl;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.pentaho.osgi.i18n.impl.LocalizationManager;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 9/5/14.
 */
public class LocalizationManagerTest {
  private Logger log;
  private Logger cachedLogger;
  private LocalizationManager localizationManager;

  @Before
  public void setup() {
    log = mock( Logger.class );
    cachedLogger = LocalizationManager.getLog();
    localizationManager = new LocalizationManager();
  }

  @After
  public void teardown() {
    LocalizationManager.setLog( cachedLogger );
  }

  @Test
  public void testLocalizationManager() throws IOException, ParseException {
    localizationManager.bundleChanged(
      makeMockBundle( 1L, "org/pentaho/osgi/bundle1/LocalizationManagerTest.testLocalizationManager.i18n.json",
        "org/pentaho/osgi/bundle1/messages.properties",
        "org/pentaho/osgi/bundle1/messages_fr.properties", "org/pentaho/osgi/bundle1/messages_de_DE.properties" ) );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "fakeLocale" );
    assertBundleKeyEquals( "key", "testPlugin", "org/pentaho/osgi/messages", "key", "fakeLocale" );
    assertBundleKeyEquals( "key_fr", "testPlugin", "org/pentaho/osgi/messages", "key", "fr" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "fr" );
    assertBundleKeyEquals( "key_fr", "testPlugin", "org/pentaho/osgi/messages", "key", "fr", "FR" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "fr", "FR" );
    assertBundleKeyEquals( "key", "testPlugin", "org/pentaho/osgi/messages", "key", "de" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "de" );
    assertBundleKeyEquals( "key_de_DE", "testPlugin", "org/pentaho/osgi/messages", "key", "de", "DE" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "de", "DE" );
    localizationManager.bundleChanged(
      makeMockBundle( 2L, "org/pentaho/osgi/bundle2/LocalizationManagerTest.testLocalizationManager.i18n.json",
        "org/pentaho/osgi/bundle2/messages_fr.properties" ) );
    assertBundleKeyEquals( "key_fr_bundle2", "testPlugin", "org/pentaho/osgi/messages", "key", "fr" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "fr" );
    localizationManager.bundleChanged(
      makeMockBundle( 3L, "org/pentaho/osgi/bundle3/LocalizationManagerTest.testLocalizationManager.i18n.json",
        "org/pentaho/osgi/bundle3/messages_fr.properties" ) );
    assertBundleKeyEquals( "key_fr_bundle3", "testPlugin", "org/pentaho/osgi/messages", "key", "fr" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "fr" );
    localizationManager.bundleChanged(
      makeMockBundle( 4L, "fakepath" ) );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "fakeLocale" );
    assertBundleKeyEquals( "key", "testPlugin", "org/pentaho/osgi/messages", "key", "fakeLocale" );
    assertBundleKeyEquals( "key_fr_bundle3", "testPlugin", "org/pentaho/osgi/messages", "key", "fr" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "fr" );
    assertBundleKeyEquals( "key_fr_bundle3", "testPlugin", "org/pentaho/osgi/messages", "key", "fr", "FR" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "fr", "FR" );
    assertBundleKeyEquals( "key", "testPlugin", "org/pentaho/osgi/messages", "key", "de" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "de" );
    assertBundleKeyEquals( "key_de_DE", "testPlugin", "org/pentaho/osgi/messages", "key", "de", "DE" );
    assertBundleKeyEquals( "defaultKey", "testPlugin", "org/pentaho/osgi/messages", "defaultKey", "de", "DE" );
  }

  private void assertBundleKeyEquals( String expected, String key, String name, String valueKey,
                                      String... localeStrings ) {
    Locale locale = null;
    if ( localeStrings.length == 1 ) {
      locale = new Locale( localeStrings[ 0 ] );
    } else if ( localeStrings.length == 2 ) {
      locale = new Locale( localeStrings[ 0 ], localeStrings[ 1 ] );
    } else {
      throw new RuntimeException( "Expected either 1 or 2 locale strings" );
    }
    assertEquals( expected, localizationManager.getResourceBundle( key, name, locale ).getString( valueKey ) );
  }

  private Bundle makeMockBundle( Long bundleId, String i18nJsonPath, String... propertiesPaths ) {
    Bundle bundle = mock( Bundle.class );
    when( bundle.getBundleId() ).thenReturn( bundleId );
    when( bundle.getResource( "META-INF/js/i18n.json" ) ).thenReturn( getClass().getClassLoader()
      .getResource( i18nJsonPath ) );
    List<URL> propertiesFiles = new ArrayList<URL>();
    for ( String path : propertiesPaths ) {
      propertiesFiles.add( getClass().getClassLoader().getResource( path ) );
    }
    when( bundle.findEntries( "org/pentaho/osgi", "messages*.properties", false ) )
      .thenReturn( new Vector<URL>( propertiesFiles )
        .elements() );
    return bundle;
  }
}
