/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2013 P6Spy
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.p6spy.engine.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ConnectionInformation implements Loggable {

  private static final AtomicInteger counter = new AtomicInteger(0);
  private final int connectionId;

  public ConnectionInformation() {
    this.connectionId = counter.getAndIncrement();
  }

  @Override
  public int getConnectionId() {
    return connectionId;
  }

  @Override
  public String getSql() {
    return "";
  }

  @Override
  public String getSqlWithValues() {
    return "";
  }
}
