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

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bryan on 8/5/14.
 */
public class RequireJsConfigManager {
  public static final String REQUIRE_JSON_PATH = "META-INF/js/require.json";
  public static final String EXTERNAL_RESOURCES_JSON_PATH = "META-INF/js/externalResources.json";
  public static final String STATIC_RESOURCES_JSON_PATH = "META-INF/js/staticResources.json";
  private final Map<Long, JSONObject> configMap = new HashMap<Long, JSONObject>();
  private final Map<Long, RequireJsConfiguration> requireConfigMap = new HashMap<Long, RequireJsConfiguration>();
  private final JSONParser parser = new JSONParser();
  private BundleContext bundleContext;
  private ExecutorService executorService = Executors.newCachedThreadPool();
  private volatile Future<String> cache;
  private volatile long lastModified;
  private String contextRoot = "/";

  public BundleContext getBundleContext() {
    return bundleContext;
  }

  public void setBundleContext( BundleContext bundleContext ) {
    this.bundleContext = bundleContext;
  }

  public boolean updateBundleContext( Bundle bundle ) throws IOException, ParseException {
    boolean shouldInvalidate = updateBundleContextStopped( bundle );
    URL configFileUrl = bundle.getResource( REQUIRE_JSON_PATH );
    URL externalResourcesUrl = bundle.getResource( EXTERNAL_RESOURCES_JSON_PATH );
    if ( configFileUrl == null && externalResourcesUrl == null ) {
      return shouldInvalidate;
    } else {
      JSONObject requireJsonObject = loadJsonObject( configFileUrl );
      JSONObject externalResourceJsonObject = loadJsonObject( externalResourcesUrl );
      JSONObject staticResourceJsonObject = loadJsonObject( bundle.getResource( STATIC_RESOURCES_JSON_PATH ) );

      boolean result = false;
      synchronized ( configMap ) {
        if ( requireJsonObject != null ) {
          configMap.put( bundle.getBundleId(), requireJsonObject );
          result = true;
        }
        if ( externalResourceJsonObject != null ) {
          List<String> requireJsList = (List<String>) externalResourceJsonObject.get( "requirejs" );
          if ( requireJsList != null ) {
            if ( staticResourceJsonObject != null ) {
              List<String> translatedList = new ArrayList<String>( requireJsList.size() );
              for ( String element : requireJsList ) {
                boolean found = false;
                for ( Object key : staticResourceJsonObject.keySet() ) {
                  String strKey = key.toString();
                  if ( element.startsWith( strKey ) ) {
                    String value = staticResourceJsonObject.get( key ).toString();
                    translatedList.add( value + element.substring( strKey.length() ) );
                    found = true;
                    break;
                  }
                }
                if ( !found ) {
                  translatedList.add( element );
                }
              }
              requireJsList = translatedList;
            }
            requireConfigMap.put( bundle.getBundleId(), new RequireJsConfiguration( bundle, requireJsList ) );
            result = true;
          }
        }
      }
      return result;
    }
  }

  private JSONObject loadJsonObject( URL url ) throws IOException, ParseException {
    if ( url == null ) {
      return null;
    }
    URLConnection urlConnection = url.openConnection();
    InputStream inputStream = urlConnection.getInputStream();
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;
    StringBuilder sb = new StringBuilder();
    try {
      inputStreamReader = new InputStreamReader( urlConnection.getInputStream() );
      bufferedReader = new BufferedReader( inputStreamReader );
      return (JSONObject) parser.parse( bufferedReader );
    } finally {
      if ( bufferedReader != null ) {
        bufferedReader.close();
      }
      if ( inputStreamReader != null ) {
        inputStreamReader.close();
      }
      if ( inputStream != null ) {
        inputStream.close();
      }
    }
  }

  public boolean updateBundleContextStopped( Bundle bundle ) {
    JSONObject bundleConfig = null;
    RequireJsConfiguration requireJsConfiguration = null;
    synchronized ( configMap ) {
      bundleConfig = configMap.remove( bundle.getBundleId() );
      requireJsConfiguration = requireConfigMap.remove( bundle.getBundleId() );
    }
    return bundleConfig != null || requireJsConfiguration != null;
  }

  public void invalidateCache( boolean shouldInvalidate ) {
    if ( shouldInvalidate ) {
      synchronized ( configMap ) {
        cache = executorService.submit( new RebuildCacheCallable( new HashMap<Long, JSONObject>( this.configMap ),
            new ArrayList<RequireJsConfiguration>( requireConfigMap.values() ) ) );
        lastModified = System.currentTimeMillis();
      }
    }
  }

  public String getRequireJsConfig() {
    Future<String> cache = null;
    String result = null;
    int tries = 3;
    Exception lastException = null;
    while ( tries-- > 0 && ( result == null || cache != this.cache ) ) {
      cache = this.cache;
      try {
        result = cache.get();
      } catch ( InterruptedException e ) {
        // ignore
      } catch ( ExecutionException e ) {
        lastException = e;
        invalidateCache( true );
      }
    }
    if ( result == null ) {
      result = "// Error computing RequireJS Config: ";
      if ( lastException != null ) {
        result += lastException.getCause().getMessage();
      } else {
        result += "unknown error";
      }
    }
    return result;
  }

  public long getLastModified() {
    return lastModified;
  }

  protected void setLastModified( long lastModified ) {
    this.lastModified = lastModified;
  }

  public void bundleChanged( Bundle bundle ) {
    boolean shouldRefresh = true;
    try {
      shouldRefresh = updateBundleContext( bundle );
    } catch ( Exception e ) {
      // Ignore TODO possibly log
    } finally {
      invalidateCache( shouldRefresh );
    }
  }

  public void init() throws Exception {
    bundleContext.addBundleListener( new RequireJsBundleListener( this ) );
    for ( Bundle bundle : bundleContext.getBundles() ) {
      updateBundleContext( bundle );
    }
    updateBundleContext( bundleContext.getBundle() );
    invalidateCache( true );
  }

  public String getContextRoot() {
    return this.contextRoot;
  }

  public void setContextRoot( String contextRoot ) {
    // ensure that the given string is properly bounded with slashes
    contextRoot = ( contextRoot.startsWith( "/" ) == false ) ? "/" + contextRoot : contextRoot;
    contextRoot = ( contextRoot.endsWith( "/" ) == false ) ? contextRoot + "/" : contextRoot;
    this.contextRoot = contextRoot;
  }
}
