/*
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
 * Description: test for the Subclasser utility $Author: aarvesen $ $Revision: 1.3 $ $Date:
 * 2003/12/01 00:40:54 $ $Id: P6TestSubclasser.java,v 1.3 2003/12/01 00:40:54 aarvesen Exp $ $Log:
 * P6TestSubclasser.java,v $ Revision 1.3 2003/12/01 00:40:54 aarvesen test both the String as well
 * as the Class constructor Revision 1.2 2003/08/07 19:09:18 aarvesen modified to reflect the minor
 * changes in the DataSource constructors Revision 1.1 2003/08/06 19:52:41 aarvesen code to test out
 * the new subclasser
 */
package com.p6spy.engine.spy;

import static com.p6spy.engine.common.Subclasser.DELIMITER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import junit.framework.Protectable;

import org.junit.Test;

import com.p6spy.engine.common.Subclasser;

public class P6TestSubclasser {

    @Test
    public void testString() throws Exception {
        Subclasser sub = new Subclasser();
        Class clazz = this.getClass();
        String packageName = clazz.getPackage().getName();
        String className = clazz.getName();

        // NB, this will all break if you change the package structure :)
        // pretty unlikely, but just in case it starts to fail, keep it in mind
        String expectedName = "P6TestSubclasser";
        assertEquals(expectedName, sub.baseName(className));

        // this one's a little trickier... since it could fail
        // depending on your architecture.  So put in this terrible
        // switchlike hack here
        String expectedPath = "com" + DELIMITER + "p6spy" + DELIMITER + "engine" +  DELIMITER + "spy";
        assertEquals(expectedPath, sub.packToDir(packageName));

        // now check the default file
        String newName = "ThisIsATestClass";

        sub.setOutputPackage(packageName);
        sub.setOutputName(newName);

        File actualFile = sub.getOutputFile();
        File expectedFile = new File("scratch" + DELIMITER + expectedPath, newName + ".java");
        assertEquals(expectedFile, actualFile);
    }

    @Test
    public void testBadClass() throws Exception {
        final Subclasser sub = new Subclasser();
        // Protectable is JUnit class
        Protectable p = new Protectable() {
            public void protect() throws Exception {
                sub.createSubClass();
            }
        };

        chkException(p, "must not be null");

        sub.setParentClass(this.getClass());
        chkException(p, "instanceof javax.sql.DataSource");
    }

    @Test
    public void testWriteHeader() throws Exception {
        final Subclasser sub = new Subclasser();
        // Protectable is JUnit class
        Protectable p = new Protectable() {
            public void protect() throws Exception {
                sub.writeHeader();
            }
        };

        chkException(p, null);

        sub.setParentClass(com.p6spy.engine.spy.P6DataSource.class);
        String actual = sub.writeHeader();

        // com.p6spy.engine.spy is imported twice since
        // we're sublcassing the p6spy datasource.
        String expected = "" + "// this class generated by class com.p6spy.engine.common.Subclasser" + sub.NEWLINE + sub.NEWLINE + "package "
            + sub.DEFAULT_PACKAGE + ";" + sub.NEWLINE + sub.NEWLINE + "import com.p6spy.engine.spy.*;" + sub.NEWLINE + "import java.sql.*;"
            + sub.NEWLINE + "import javax.sql.*;" + sub.NEWLINE + "import com.p6spy.engine.spy.*;" + sub.NEWLINE + sub.NEWLINE + sub.NEWLINE
            + "public class P6P6DataSource extends com.p6spy.engine.spy.P6DataSource {" + sub.NEWLINE + "";

        assertEquals(sub.NEWLINE + expected, sub.NEWLINE + actual);
    }

    @Test
    public void testWriteConstructors() throws Exception {
        final Subclasser sub = new Subclasser();
        Protectable p = new Protectable() {
            public void protect() throws Exception {
                sub.writeConstructors();
            }
        };

        chkException(p, null);

        sub.setParentClass(javax.sql.DataSource.class);
        chkException(p, "interface");

        sub.setParentClass(com.p6spy.engine.spy.P6DataSource.class);
        
        final String expected = "" + sub.NEWLINE + sub.INDENT + "public P6P6DataSource (javax.sql.DataSource p0) {" + sub.NEWLINE + sub.INDENT + sub.INDENT
            + "super( p0);" + sub.NEWLINE + sub.INDENT + "}" + sub.NEWLINE + sub.INDENT + "public P6P6DataSource () {" + sub.NEWLINE + sub.INDENT
            + sub.INDENT + "super();" + sub.NEWLINE + sub.INDENT + "}" + "";
        
        final String expectedAlternateOrder = "" + sub.NEWLINE + sub.INDENT + "public P6P6DataSource () {" + sub.NEWLINE + sub.INDENT + sub.INDENT
                + "super();" + sub.NEWLINE + sub.INDENT + "}" + sub.NEWLINE + sub.INDENT + "public P6P6DataSource (javax.sql.DataSource p0) {" + sub.NEWLINE + sub.INDENT
                + sub.INDENT + "super( p0);" + sub.NEWLINE + sub.INDENT + "}" + "";
        
        String actual = sub.writeConstructors();

        assertTrue(expected.equals(actual) || expectedAlternateOrder.equals(actual));
        // sometimes JUnit truncs the strings... if that's the case with your
        // version, then you can uncomment this line to get out own
        // full listing of the strings in their variance
        //assertEquals("Expected constructor like: " + sub.NEWLINE + expected + sub.NEWLINE + " but found " + sub.NEWLINE + actual + sub.NEWLINE, expected, actual);
    }

    @Test
    public void testOverride() throws Exception {
        Subclasser sub = new Subclasser();
        String actual = sub.overrideConnection();
        String expected = "" + sub.NEWLINE + sub.INDENT + "public Connection getConnection() throws SQLException {" + sub.NEWLINE + sub.INDENT
            + sub.INDENT + "return P6SpyDriverCore.wrapConnection(super.getConnection());" + sub.NEWLINE + sub.INDENT + "};" + sub.NEWLINE
            + sub.NEWLINE + sub.INDENT + "public Connection getConnection(String username, String password) throws SQLException {" + sub.NEWLINE
            + sub.INDENT + sub.INDENT + "return P6SpyDriverCore.wrapConnection(super.getConnection(username, password));" + sub.NEWLINE + sub.INDENT
            + "};" + sub.NEWLINE + "";

        assertEquals(expected, actual);
    }

    protected void chkException(Protectable p, String msg) {
        try {
            p.protect();
            fail("Expected exception with message containing '" + msg + "', but no exception was thrown.");
        } catch (Throwable e) {
            String actual = e.getMessage();
            if (msg == null && actual == null) {
                // okay
            } else if (actual == null && msg != null) {
                fail("Expected exception with message containing '" + msg + "', but got a null message from " + e.getClass().getName());
            } else if (msg == null && actual != null) {
                fail("Expected null message, but got a '" + actual + "'.");
            } else if (actual.indexOf(msg) == -1) {
                fail("Expected exception with message containing '" + msg + "', but message was '" + actual + "'.");
            }
        }
    }

}
