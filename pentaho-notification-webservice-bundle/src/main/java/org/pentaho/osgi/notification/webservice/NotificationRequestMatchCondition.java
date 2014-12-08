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

package org.pentaho.osgi.notification.webservice;

import org.pentaho.osgi.notification.api.MatchCondition;
import org.pentaho.osgi.notification.api.NotificationObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by bryan on 9/22/14.
 */
public class NotificationRequestMatchCondition implements MatchCondition {
  private final Map<String, Map<String, Long>> relevantMap = new HashMap<String, Map<String, Long>>();

  public Set<String> getTypes() {
    return relevantMap.keySet();
  }

  public NotificationRequestMatchCondition( NotificationRequestWrapper notificationRequestWrapper ) {
    for ( NotificationRequest notificationRequest : notificationRequestWrapper.getRequests() ) {
      String type = notificationRequest.getNotificationType();
      Map<String, Long> typeMap = new HashMap<String, Long>();
      relevantMap.put( type, typeMap );
      for ( NotificationRequestEntry notificationRequestEntry : notificationRequest.getEntries() ) {
        Long sequence = notificationRequestEntry.getSequence();
        if ( sequence == null ) {
          sequence = 0L;
        }
        typeMap.put( notificationRequestEntry.getId(), sequence );
      }
    }
  }

  @Override public boolean matches( Object object ) {
    if ( !( object instanceof NotificationObject ) ) {
      return false;
    }
    NotificationObject notificationObject = (NotificationObject) object;
    String type = notificationObject.getType();
    Map<String, Long> typeMap = relevantMap.get( type );
    if ( typeMap == null ) {
      return false;
    }
    Long sequence = typeMap.get( notificationObject.getId() );
    if ( sequence != null ) {
      return notificationObject.getSequence() >= sequence;
    }
    return false;
  }
}
