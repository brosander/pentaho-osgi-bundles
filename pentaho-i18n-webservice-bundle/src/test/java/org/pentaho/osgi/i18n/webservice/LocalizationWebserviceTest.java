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

package org.pentaho.osgi.i18n.webservice;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.osgi.i18n.LocalizationService;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 9/6/14.
 */
public class LocalizationWebserviceTest {
  private LocalizationService localizationService;
  private LocalizationWebservice localizationWebservice;

  @Before
  public void setup() {
    localizationService = mock( LocalizationService.class );
    localizationWebservice = new LocalizationWebservice();
    localizationWebservice.setLocalizationService( localizationService );
  }

  @Test
  public void testWebserviceMethodDefault() {
    String key = "test-key";
    String name = "test.name";
    String localeString = "";
    ResourceBundle resourceBundle = mock( ResourceBundle.class );
    when( localizationService
      .getResourceBundle( eq( key ), eq( name.replaceAll( "\\.", "/" ) ), eq( Locale.getDefault() ) ) )
      .thenReturn( resourceBundle );
    assertEquals( resourceBundle, localizationWebservice.getResourceBundleService( key, name, localeString ) );
  }

  @Test
  public void testWebserviceMethodOneLocaleParam() {
    String key = "test-key";
    String name = "test.name";
    String localeString = "en";
    ResourceBundle resourceBundle = mock( ResourceBundle.class );
    when( localizationService
      .getResourceBundle( eq( key ), eq( name.replaceAll( "\\.", "/" ) ), eq( new Locale( "en" ) ) ) )
      .thenReturn( resourceBundle );
    assertEquals( resourceBundle, localizationWebservice.getResourceBundleService( key, name, localeString ) );
  }

  @Test
  public void testWebserviceMethodTwoLocaleParams() {
    String key = "test-key";
    String name = "test.name";
    String localeString = "en-US";
    ResourceBundle resourceBundle = mock( ResourceBundle.class );
    when( localizationService
      .getResourceBundle( eq( key ), eq( name.replaceAll( "\\.", "/" ) ), eq( new Locale( "en", "US" ) ) ) )
      .thenReturn( resourceBundle );
    assertEquals( resourceBundle, localizationWebservice.getResourceBundleService( key, name, localeString ) );
  }
}
