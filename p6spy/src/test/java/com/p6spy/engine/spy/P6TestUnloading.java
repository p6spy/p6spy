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

package com.p6spy.engine.spy;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.p6spy.engine.common.P6Util;

public class P6TestUnloading extends P6TestFramework {

    @Before
    public void setUpUnloading() {
        /*
         * try { //P6TestUtil.unloadDrivers(); } catch (SQLException e)
         * {com.p6spy.engine.spy.P6SpyDriver fail("could not init"); }
         */
    }

    @Ignore
    @Test
    public void testDriverUnloading() throws Exception {
        Properties props = P6TestUtil.loadProperties(P6TestFramework.P6_TEST_PROPERTIES);
        String url = props.getProperty("url");
        String user = props.getProperty("user");
        String password = props.getProperty("password");
        String p6Driver = props.getProperty("p6driver");

        unloadAll();
        Map properties = P6TestUtil.getDefaultPropertyFile();
        P6TestUtil.reloadProperty(properties);
//            registerDriver(oracleDriver);
        //P6Util.forName(oracleDriver);
        Connection con = DriverManager.getConnection(url, user, password);

        chkInstanceOf(con, "oracle");

        registerDriver(p6Driver);
        con = DriverManager.getConnection(url, user, password);
        chkInstanceOf(con, "p6spy");

        unloadAll();
    }

    protected void chkInstanceOf(Connection con, String packageName) {
        String conClass = con.getClass().getName();
        assertTrue("Expected connection to have a package of " + packageName + " but found " + conClass, conClass.indexOf(packageName) != -1);
    }

    protected void registerDriver(String driverClass) throws Exception {
        Class clazz = P6Util.forName(driverClass);
        Driver driver = (Driver) clazz.newInstance();
        DriverManager.registerDriver(driver);

        if (driver instanceof P6SpyDriver) {
            ReinitSpyDriver.setInitialized(false);
            ((P6SpyDriver) driver).initMethod("");
        }
    }

    protected void unloadAll() throws SQLException {
        ArrayList dereg = new ArrayList();
        for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();) {
            dereg.add(e.nextElement());
        }

        // if you found any drivers let's dereg them now
        int size = dereg.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Driver driver = (Driver) dereg.get(i);
                System.err.println("Deregistering driver " + driver);
                DriverManager.deregisterDriver(driver);
            }
        }

    }

}
