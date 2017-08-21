/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
 *
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
 */

package com.p6spy.engine.test;

import javax.sql.XADataSource;

import com.p6spy.engine.spy.P6LoadableOptions;

public interface P6TestLoadableOptions extends P6LoadableOptions, P6TestOptionsMBean {

  // no need to expose these via MBean => keep them here
  String getUrl();

  void setUrl(String url);

  String getUser();

  void setUser(String user);

  String getPassword();

  void setPassword(String password);

  String getUrl2();

  void setUrl2(String url2);

  String getUser2();

  void setUser2(String user2);

  String getPassword2();

  void setPassword2(String password2);

  XADataSource getXaDataSource();

  void setXaDataSource(String xaDataSource);

  String getValidationQuery();

  void setValidationQuery(String validationQuery);

}
