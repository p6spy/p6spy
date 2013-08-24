/*
 *
 * ====================================================================
 *
 * The P6Spy Software License, Version 1.1
 *
 * This license is derived and fully compatible with the Apache Software
 * license, see http://www.apache.org/LICENSE.txt
 *
 * Copyright (c) 2001-2002 Andy Martin, Ph.D. and Jeff Goke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "The original concept and code base for P6Spy was conceived
 * and developed by Andy Martin, Ph.D. who generously contribued
 * the first complete release to the public under this license.
 * This product was due to the pioneering work of Andy
 * that began in December of 1995 developing applications that could
 * seamlessly be deployed with minimal effort but with dramatic results.
 * This code is maintained and extended by Jeff Goke and with the ideas
 * and contributions of other P6Spy contributors.
 * (http://www.p6spy.com)"
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "P6Spy", "Jeff Goke", and "Andy Martin" must not be used
 * to endorse or promote products derived from this software without
 * prior written permission. For written permission, please contact
 * license@p6spy.com.
 *
 * 5. Products derived from this software may not be called "P6Spy"
 * nor may "P6Spy" appear in their names without prior written
 * permission of Jeff Goke and Andy Martin.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

/**
 * Description: Test class for prepared statements
 *
 * $Author: cheechq $
 * $Revision: 1.5 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6TestPerform.java,v 1.5 2003/06/03 19:20:26 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestPerform.java,v $
 * $Log: P6TestPerform.java,v $
 * Revision 1.5  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.4  2003/01/23 00:43:37  aarvesen
 * Changed the module to be dot rather than underscore
 *
 * Revision 1.3  2002/12/19 23:45:48  aarvesen
 * use factory rather than driver
 *
 * Revision 1.2  2002/10/06 18:24:04  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.1  2002/04/21 06:16:20  jeffgoke
 * added test cases, fixed batch bugs
 *
 *
 *
 */

package com.p6spy.engine.spy;


import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class P6TestPerform extends P6TestFramework {

    public static int rowsToCreate = 10000;

    @Before
    public void setupPerform() throws SQLException {
    	// we are going to fill up a large table for the following tests
        Statement statement = connection.createStatement();
        drop(statement);
        statement.execute("create table big_table_test (col1 integer, col2 varchar(255))");
        statement.execute("create table little_table_test (col1 integer, col2 varchar(255))");
        statement.close();    	
    }

    @Ignore
    @Test
    public void testSlowMonitor() {
        try {
            Map tp = getDefaultPropertyFile();
            reloadProperty(tp);

            String sql = "insert into big_table_test (col1, col2) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            String trunk = createTrunk();

            for (int i = 0; i < rowsToCreate; i++) {
                ps.setInt(1, i);
                ps.setString(2, trunk+"_"+i);
                ps.addBatch();

                if (i % 1000 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();

        } catch (Exception e) {
            fail(e.getMessage());
        }
//    }
//
//    @Test
//    public void testBeginCleanSlowMonitor() {
        try {
            Statement statement = connection.createStatement();

            Map tp = getDefaultPropertyFile();
            tp.put("trace","false");
            reloadProperty(tp);

            String trunk = createTrunk();

            String query;
            ResultSet rs;

            // now the monitor should not be active
            //String query = "select 'b' from little_table_test";
            //ResultSet rs = statement.executeQuery(query);
            //assertIsLastQuery(query);

            // activate
            //tp.put("outagedetection", "true");
            //tp.put("outagedetectioninterval", "1");
            //reloadProperty(tp);

            // now the monitor should be active but this should be fast enough to be okay
            query = "select 'zzee' from little_table_test";
            rs = statement.executeQuery(query);
            assertIsNotLastQuery("OUTAGE");
            //assertIsNotLastQuery(query);

            // this should not - it should log an outage
            query = "select col1 from big_table_test where col2 like '%"+trunk+"_"+(rowsToCreate+1)+"%'";
            rs = statement.executeQuery(query);
            assertIsLastQuery("Outage");
            // Peter Butkovic:
            // not sure why, but last query is not modified by it's read, probably used to be in the past
//            assertIsLastQuery(query);
        } catch (Exception e) {
            fail(e.getMessage()+getStackTrace(e));
        }
    }

    protected String createTrunk() {
        StringBuffer trunc = new StringBuffer(150);
        for (int i = 0; i < 150; i++) {
            trunc.append("P");
        }
        String trunk = trunc.toString();
        return trunk;
    }

    protected void drop(Statement statement) {
        dropStatement("drop table big_table_test", statement);
        dropStatement("drop table little_table_test", statement);
    }

    protected void dropStatement(String sql, Statement statement) {
        try {
            statement.execute(sql);
        } catch (Exception e) {
            // we don't really care about cleanup failing
        }
    }

    // we do not want the log active
    @Override
    protected Map getDefaultPropertyFile() throws IOException {

        Properties props = loadProperties(P6TestFramework.P6_TEST_PROPERTIES);
        String realdrivername = props.getProperty("p6realdriver");

        Properties props2 = loadProperties(P6TestFramework.P6_TEST_PROPERTIES);
        String realdrivername2 = props2.getProperty("p6realdriver2");

        HashMap tp = new HashMap();
        tp.put("module.log","com.p6spy.engine.logging.P6LogFactory");
        tp.put("module.outage","com.p6spy.engine.outage.P6OutageFactory");
        tp.put("realdriver",realdrivername);
        tp.put("realdriver2",realdrivername2);
        tp.put("filter","false");
        tp.put("include","");
        tp.put("exclude","");
        tp.put("trace","true");
        tp.put("autoflush","true");
        tp.put("logfile","spy.log");
        tp.put("append","true");
        tp.put("dateformat","");
        tp.put("includecategories","");
        tp.put("excludecategories","debug,result,batch");
        tp.put("stringmatcher","");
        tp.put("stacktrace","false");
        tp.put("stacktraceclass","");
        tp.put("reloadproperties","false");
        tp.put("reloadpropertiesinterval","1");
        tp.put("useprefix","false");
        tp.put("outagedetection", "true");
        tp.put("outagedetectioninterval", "3");
        return tp;
    }
}
