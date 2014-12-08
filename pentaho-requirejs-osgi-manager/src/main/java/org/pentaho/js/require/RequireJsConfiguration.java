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

import org.osgi.framework.Bundle;

import java.util.List;

/**
 * Created by bryan on 9/2/14.
 */
public class RequireJsConfiguration {
  private final Bundle bundle;
  private final List<String> requireConfigurations;

  public RequireJsConfiguration( Bundle bundle, List<String> requireConfigurations ) {
    this.bundle = bundle;
    this.requireConfigurations = requireConfigurations;
  }

  public Bundle getBundle() {
    return bundle;
  }

  public List<String> getRequireConfigurations() {
    return requireConfigurations;
  }
}
