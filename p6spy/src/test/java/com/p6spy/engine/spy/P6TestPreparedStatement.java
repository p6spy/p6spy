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
 * $Author: aarvesen $
 * $Revision: 1.3 $
 * $Date: 2003/06/05 20:11:29 $
 *
 * $Id: P6TestPreparedStatement.java,v 1.3 2003/06/05 20:11:29 aarvesen Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestPreparedStatement.java,v $
 * $Log: P6TestPreparedStatement.java,v $
 * Revision 1.3  2003/06/05 20:11:29  aarvesen
 * bradley 'dot' johnson (bradley@irongrid.com) added in a test for dynamic array allocation
 *
 * re-added the import that Cheech removed :)
 *
 * Revision 1.2  2003/06/03 19:20:26  cheechq
 * removed unused imports
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class P6TestPreparedStatement extends P6TestFramework {

    @Before
    public void setUpPreparedStatement() {
        try {
            Statement statement = connection.createStatement();
            dropPrepared(statement);
            statement.execute("create table prepstmt_test (col1 varchar(255), col2 integer)");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPreparedQueryUpdate() {
        try {
            // test a basic insert
            String update = "insert into prepstmt_test values (?, ?)";
            PreparedStatement prep = getPreparedStatement(update);
            prep.setString(1, "miller");
            prep.setInt(2,1);
            prep.executeUpdate();
            assertIsLastQuery(update);
            assertIsLastQuery("miller");
            assertIsLastQuery("1");

            // test a basic select
            String query = "select count(*) from prepstmt_test where col2 = ?";
            prep = getPreparedStatement(query);
            prep.setInt(1,1);
            ResultSet rs = prep.executeQuery();
            assertIsLastQuery(query);
            rs.next();
            assertEquals(1, rs.getInt(1));

            // test dynamic allocation of P6_MAX_FIELDS
            int MaxFields = P6PreparedStatement.P6_MAX_FIELDS + 3;
            StringBuffer bigSelect = new StringBuffer(MaxFields);
            bigSelect.append("select count(*) from prepstmt_test where");
            for (int i = 0; i < MaxFields; i++) {
                if (i > 0) {
                  bigSelect.append(" or ");
                }
                bigSelect.append(" col2=?");
            }
            prep = getPreparedStatement(bigSelect.toString());
            for (int i = 1; i <= MaxFields; i++) {
                 prep.setInt(i, i);
	    }
            //rs = prep.executeQuery();

            // test batch inserts
            update = "insert into prepstmt_test values (?,?)";
            prep = getPreparedStatement(update);
            prep.setString(1,"danny");
            prep.setInt(2,2);
            prep.addBatch();
            prep.setString(1,"denver");
            prep.setInt(2,3);
            prep.addBatch();
            prep.setString(1,"aspen");
            prep.setInt(2,4);
            prep.addBatch();
            prep.executeBatch();
            assertIsLastQuery(update);
            assertIsLastQuery("aspen");
            assertIsLastQuery("4");

            query = "select count(*) from prepstmt_test";
            prep = getPreparedStatement(query);
            rs = prep.executeQuery();
            rs.next();
            assertEquals(4, rs.getInt(1));
        } catch (Exception e) {
            fail(e.getMessage()+" due to error: "+getStackTrace(e));
        }
    }

    @After
    public void tearDownPreparedStatement() {
        try {
            Statement statement = connection.createStatement();
            dropPrepared(statement);
        }  catch (Exception e) {
            fail(e.getMessage());
        }
    }

    protected void dropPrepared(Statement statement) {
        dropPreparedStatement("drop table prepstmt_test", statement);
    }

    protected void dropPreparedStatement(String sql, Statement statement) {
        try {
            statement.execute(sql);
        } catch (Exception e) {
            // we don't really care about cleanup failing
        }
    }

    protected PreparedStatement getPreparedStatement(String query) throws SQLException {
        return (connection.prepareStatement(query));
    }

}
