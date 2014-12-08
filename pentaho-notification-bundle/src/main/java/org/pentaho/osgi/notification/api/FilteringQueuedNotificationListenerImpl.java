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

package org.pentaho.osgi.notification.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by bryan on 9/18/14.
 */
public class FilteringQueuedNotificationListenerImpl implements NotificationListener {
  private final Set<String> types;
  private final BlockingQueue<NotificationObject> queuedNotifications = new LinkedBlockingQueue<NotificationObject>();
  private final List<Notifier> notifiersRegisteredWith = new ArrayList<Notifier>();
  private final MatchCondition matchCondition;

  public FilteringQueuedNotificationListenerImpl( Set<String> types, MatchCondition matchCondition ) {
    this.types = types;
    this.matchCondition = matchCondition;
  }

  @Override public void notify( NotificationObject notificationObject ) {
    if ( matchCondition.matches( notificationObject ) ) {
      boolean success = false;
      while ( !success ) {
        try {
          queuedNotifications.put( notificationObject );
          success = true;
        } catch ( InterruptedException e ) {
          // Ignore
        }
      }
    }
  }

  public synchronized void registerWithIfRelevant( Notifier notifier ) {
    if ( !Collections.disjoint( notifier.getEmittedTypes(), types ) ) {
      notifier.register( this );
      notifiersRegisteredWith.add( notifier );
    }
  }

  public synchronized void unregisterWithAll() {
    for ( Notifier notifier : notifiersRegisteredWith ) {
      notifier.unregister( this );
    }
  }

  public BlockingQueue<NotificationObject> getQueuedNotifications() {
    return queuedNotifications;
  }
}
