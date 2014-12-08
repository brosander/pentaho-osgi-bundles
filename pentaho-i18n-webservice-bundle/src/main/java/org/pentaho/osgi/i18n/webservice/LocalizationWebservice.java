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

import org.pentaho.osgi.i18n.LocalizationService;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by bryan on 9/5/14.
 */
@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
@WebService
public class LocalizationWebservice implements LocalizationService {
  private LocalizationService localizationService;

  @Override public ResourceBundle getResourceBundle( String key, String name, Locale locale ) {
    return localizationService.getResourceBundle( key, name, locale );
  }

  public void setLocalizationService( LocalizationService localizationService ) {
    this.localizationService = localizationService;
  }

  @GET
  @Path( "/{key}/{name}/{language}" )
  public ResourceBundle getResourceBundleService( @PathParam( "key" ) String key, @PathParam( "name" ) String name,
                                                  @PathParam( "language" ) String localeString ) {
    name = name.replaceAll( "\\.", "/" );
    String[] splitLocale;
    if ( localeString == null || localeString.trim().length() == 0 ) {
      splitLocale = new String[] { };
    } else {
      splitLocale = localeString.split( "-" );
    }
    Locale locale;
    if ( splitLocale.length == 1 ) {
      locale = new Locale( splitLocale[ 0 ] );
    } else if ( splitLocale.length >= 2 ) {
      locale = new Locale( splitLocale[ 0 ], splitLocale[ 1 ] );
    } else {
      locale = Locale.getDefault();
    }
    return getResourceBundle( key, name, locale );
  }
}
