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
 * Description: Test class for statements
 *
 * $Author: cheechq $
 * $Revision: 1.3 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6TestBasics.java,v 1.3 2003/06/03 19:20:26 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestBasics.java,v $
 * $Log: P6TestBasics.java,v $
 * Revision 1.3  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.2  2002/12/12 01:39:01  jeffgoke
 * no message
 *
 * Revision 1.1  2002/10/06 18:24:04  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 *
 *
 */

package com.p6spy.engine.spy;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.junit.Before;

public class P6TestBasics {

    protected Connection connection = null;

    @Before
    public void setUpBasics() throws Exception {
      P6TestUtil.unloadDrivers();
    }

    public void testNative() throws Exception {
        // load the native driver
        Map properties = P6TestUtil.getDefaultPropertyFile();
        P6TestUtil.reloadProperty(properties);
        connection = P6TestUtil.loadDrivers("p6realdriver");
        sqltests();
        P6TestUtil.unloadDrivers();
    }
        
    public void testSpy() throws Exception {
        // load the p6log driver
        Map properties = P6TestUtil.getDefaultPropertyFile();
        P6TestUtil.reloadProperty(properties);
        
        connection = P6TestUtil.loadDrivers("p6driver");
        sqltests();
    }

    protected void preparesql() throws SQLException {
        Statement statement = connection.createStatement();
        drop(statement);
        statement.execute("create table basic_test (col1 varchar(255), col2 integer(5))");
    }

    protected void sqltests() throws SQLException {
        preparesql();

        // insert test
        String insert = "insert into basic_test values (\'bob\', 5)";
        Statement statement = connection.createStatement();
        statement.executeUpdate(insert);

        // update test
        String update = "update basic_test set col1 = \'bill\' where col2 = 5";
        statement.executeUpdate(update);

        // query test
        String query = "select col1 from basic_test where col2 = 5";
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        assertEquals(rs.getString(1), "bill");

        // prepared test
        PreparedStatement ps = connection.prepareStatement("insert into basic_test values (?, ?)");
        ps.setString(1,"joe");
        ps.setInt(2,6);
        ps.executeUpdate();
        ps.setString(1,"andy");
        ps.setInt(2,7);
        ps.execute();

        ps = connection.prepareStatement("update basic_test set col1 = ? where col2 = ?");
        ps.setString(1,"charles");
        ps.setInt(2,6);
        ps.executeUpdate();
        ps.setString(1,"bobby");
        ps.setInt(2,7);
        ps.execute();

        ps = connection.prepareStatement("select col1 from basic_test where col1 = ? and col2 = ?");
        ps.setString(1,"charles");
        ps.setInt(2,6);
        rs = ps.executeQuery();
        rs.next();
        assertEquals("charles", rs.getString(1));
    }

    protected void drop(Statement statement) {
        if (statement == null) { return; }
        dropStatement("drop table basic_test", statement);
    }

    protected void dropStatement(String sql, Statement statement) {
        try {
            statement.execute(sql);
        } catch (Exception e) {
            // we don't really care about cleanup failing
        }
    }
    
                }

