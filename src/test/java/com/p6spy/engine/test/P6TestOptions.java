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

import java.util.HashMap;
import java.util.Map;

import javax.management.StandardMBean;
import javax.sql.XADataSource;

import com.p6spy.engine.spy.P6ModuleManager;
import com.p6spy.engine.spy.option.P6OptionsRepository;

public class P6TestOptions extends StandardMBean implements P6TestLoadableOptions {

  public static final String PASSWORD2 = "password2";
  public static final String USER2 = "user2";
  public static final String URL2 = "url2";
  public static final String PASSWORD = "password";
  public static final String USER = "user";
  public static final String URL = "url";
  public static final String XA_DATASOURCE = "xaDataSource";
  public static final String VALIDATION_QUERY = "validationQuery";

  private static final Map<String, String> defaults = new HashMap<String, String>();

  private final P6OptionsRepository optionsRepository;

  public P6TestOptions(final P6OptionsRepository optionsRepository) {
    super(P6TestOptionsMBean.class, false);
    this.optionsRepository = optionsRepository;
  }
  
  @Override
  public void load(Map<String, String> properties) {
    setUrl(properties.get(URL));
    setUser(properties.get(USER));
    setPassword(properties.get(PASSWORD));
    setUrl2(properties.get(URL2));
    setUser2(properties.get(USER2));
    setPassword2(properties.get(PASSWORD2));
    setXaDataSource(properties.get(XA_DATASOURCE));
    setValidationQuery(properties.get(VALIDATION_QUERY));
  }

  /**
   * Utility method, to make accessing options from app less verbose.
   * 
   * @return active instance of the {@link P6TestLoadableOptions}
   */
  public static P6TestLoadableOptions getActiveInstance() {
    return P6ModuleManager.getInstance().getOptions(P6TestOptions.class);
  }
  
  @Override
  public Map<String, String> getDefaults() {
    return defaults;
  }
  
  @Override
  public String getUrl() {
    return optionsRepository.get(String.class, URL);
  }

  @Override
  public void setUrl(String url) {
    optionsRepository.set(String.class, URL, url);
  }

  @Override
  public String getUser() {
    return optionsRepository.get(String.class, USER);
  }

  @Override
  public void setUser(String user) {
    optionsRepository.set(String.class, USER, user);
  }

  @Override
  public String getPassword() {
    return optionsRepository.get(String.class, PASSWORD);
  }

  @Override
  public void setPassword(String password) {
    optionsRepository.set(String.class, PASSWORD, password);
  }

  @Override
  public String getUrl2() {
    return optionsRepository.get(String.class, URL2);
  }

  @Override
  public void setUrl2(String url2) {
    optionsRepository.set(String.class, URL2, url2);
  }

  @Override
  public String getUser2() {
    return optionsRepository.get(String.class, USER2);
  }

  @Override
  public void setUser2(String user2) {
    optionsRepository.set(String.class, USER2, user2);
  }

  @Override
  public String getPassword2() {
    return optionsRepository.get(String.class, PASSWORD2);
  }

  @Override
  public void setPassword2(String password2) {
    optionsRepository.set(String.class, PASSWORD2, password2);
  }
  
  @Override
  public void setXaDataSource(String xaDataSource) {
    optionsRepository.set(XADataSource.class, XA_DATASOURCE, xaDataSource);
  }
  
  @Override
  public XADataSource getXaDataSource() {
    return optionsRepository.get(XADataSource.class, XA_DATASOURCE);
  }
  
  @Override
  public void setValidationQuery(String validationQuery) {
    optionsRepository.set(String.class, VALIDATION_QUERY, validationQuery);
  }

  @Override
  public String getValidationQuery() {
    return optionsRepository.get(String.class, VALIDATION_QUERY);
  }
  
}
