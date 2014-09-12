/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.osgi.messagewriter.impl.json;

import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;
import org.pentaho.osgi.messagewriter.OutputException;
import org.pentaho.osgi.messagewriter.PrimitiveOutputBuilder;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by bryan on 9/11/14.
 */
public class JSONPrimitiveOutputBuilder extends JSONOutputBuilder implements PrimitiveOutputBuilder, JSONStreamAware {
  private Object value;

  @Override public JSONStreamAware getJSONStreamAware() {
    return this;
  }

  @Override public void set( Object value ) throws OutputException {
    this.value = value;
  }

  @Override public void writeJSONString( Writer out ) throws IOException {
    JSONValue.writeJSONString( value, out );
  }
}
