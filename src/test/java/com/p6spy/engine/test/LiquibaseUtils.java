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

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class LiquibaseUtils {
  private static final Logger log = Logger.getLogger(LiquibaseUtils.class);

  public static void setup(final String url, final String username, final String password)
      throws LiquibaseException {

    // setup database for testing
    log.info("Setting up database for testing");
    String nativeUrl = url;
    if( url.startsWith("jdbc:p6spy:") ) {
      nativeUrl = url.replace("jdbc:p6spy:", "jdbc:");
    }

    setup(new DriverManagerDataSource(nativeUrl, username, password));
  }

  public static void setup(final DataSource dataSource) throws LiquibaseException {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setChangeLog("classpath:liquibase.xml");
    liquibase.setDataSource(dataSource);
    liquibase.setResourceLoader(new DefaultResourceLoader());
    liquibase.setDropFirst(true);
    liquibase.afterPropertiesSet();
  }

}
