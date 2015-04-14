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
package com.p6spy.engine.test;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.OptionsRepository;

public class P6TestFactory implements P6Factory {

  @Override
  public P6LoadableOptions getOptions(OptionsRepository optionsRepository) {
    return new P6TestOptions(optionsRepository);
  }

  @Override
  public Connection getConnection(Connection conn, OptionsRepository optionsRepository) throws SQLException {
    // no wrapping required here
    return conn;
  }

}
