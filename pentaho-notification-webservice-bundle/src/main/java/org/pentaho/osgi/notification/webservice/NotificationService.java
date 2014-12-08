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

import org.pentaho.osgi.notification.api.NotificationAggregator;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by bryan on 8/21/14.
 */
@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
@WebService
public class NotificationService {
  public static final long TIMEOUT = 30 * 1000;
  private NotificationAggregator notificationAggregator;

  public void setNotificationAggregator( NotificationAggregator notificationAggregator ) {
    this.notificationAggregator = notificationAggregator;
  }

  @POST
  @Path( "/" )
  public NotificationResponse getNotifications( NotificationRequestWrapper notificationRequestWrapper ) {
    NotificationRequestMatchCondition notificationRequestMatchCondition =
      new NotificationRequestMatchCondition( notificationRequestWrapper );
    return new NotificationResponse( notificationAggregator
      .getNotificationsBlocking( notificationRequestMatchCondition.getTypes(), notificationRequestMatchCondition,
        TIMEOUT ) );
  }
}
