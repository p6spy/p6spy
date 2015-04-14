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
package com.p6spy.engine.spy;

import java.sql.Connection;
import java.sql.SQLException;

import com.p6spy.engine.spy.option.OptionsRepository;

/**
 * p6factory exists to make extending the spy core easier when making a new module. Since there are
 * so many methods that return a NEW object of some type (connection, etc) either you would be
 * forced to overload them all, or we could use this factory method to handle that situation. not
 * perfect, but should make extending and maintaining the code far easier.
 */
public interface P6Factory {
  // OK this is not a typical factory, but to keep P6Factory an interface
  // getOptions can't be static, we'll live with it I guess
  public P6LoadableOptions getOptions(OptionsRepository optionsRepository);

  public Connection getConnection(Connection conn, OptionsRepository optionsRepository) throws SQLException;

}
