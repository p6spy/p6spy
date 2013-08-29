/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.p6spy.engine.spy.ds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;

import javax.ejb.EJB;
import javax.naming.NamingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.leak.P6LeakStatement;
import com.p6spy.engine.logging.P6LogStatement;
import com.p6spy.engine.logging.appender.FileLogger;
import com.p6spy.engine.outage.P6OutageStatement;
import com.p6spy.engine.spy.P6Statement;
import com.p6spy.engine.spy.P6TestFramework;

@RunWith(Arquillian.class)
public class P6TestDataSource {

  static {
    // make sure to get all the unload driver stuff happen very early
    try {
      new P6TestFramework("Derby_DS") {};
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Deployment
   public static Archive<?> createDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
            // test classes 
            .addPackage(Game.class.getPackage())
            // persistence
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            
            // please note, that in case of packages refactoring in the p6spy, these should be updated accordingly
            
            // p6spy stuff
            .addAsResource("com/p6spy/engine/spy/ds/P6Test_Derby_DS.properties", "WEB-INF/classes/spy.properties")
            // package contents of: com.p6spy.engine.common
            .addPackage(P6Util.class.getPackage())
            // package contents of: com.p6spy.engine.leak
            .addPackage(P6LeakStatement.class.getPackage())
            // package contents of: com.p6spy.engine.logging
            .addPackage(P6LogStatement.class.getPackage())
            // package contents of: com.p6spy.engine.logging.appender
            .addPackage(FileLogger.class.getPackage())
            // package contents of: com.p6spy.engine.outage
            .addPackage(P6OutageStatement.class.getPackage())
            // package contents of: com.p6spy.engine.spy
            .addPackage(P6Statement.class.getPackage())
            ;
   }
   
   @Before
   public void setupXADataSource() throws NamingException {
     service.purge();
   }
 
   @EJB
   DualRepositoryService service;
   
   @EJB
   BookRepository bookRepo;
   
   
   @Test
   public void testDSPersistOK() {
     bookRepo.insertInTx();
     assertEquals(1, bookRepo.getRecordCount());
   }
   
   @Test
   public void testDSPersistThenRollback() {
     bookRepo.insertThenRollbackInTx();
     
     assertEquals(0, bookRepo.getRecordCount());
   }
   

   // TODO XA Datasources to be proxied over P6spy
   
   @Test
   public void should_not_modify_database_transaction_fails()
   {
      try
      {
         service.succeedFirstFailSecondInTx();
         // exception expected
         fail();
      }
      catch (Exception e)
      {
      }
      
      assertEquals(0, service.getGameCount());
      assertEquals(0, service.getInvoiceCount());
   }
   
   @Test
   public void should_modify_database_if_no_transaction() throws Exception
   {
      try
      {
         service.succeedFirstFailSecondWithoutTx();
         // exception expected
         fail();
      }
      catch (Exception e)
      {
      }
      
      assertEquals(1, service.getGameCount());
      assertEquals(0, service.getInvoiceCount());
   }
   
   @Test
   public void should_not_modify_database_if_rollback_transaction() throws Exception
   {
      service.insertBothThenRollbackInTx();
      
      assertEquals(0, service.getGameCount());
      assertEquals(0, service.getInvoiceCount());
   }
}
