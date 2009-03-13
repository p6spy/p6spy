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
 * Description: JDBC Driver Extension implementing PreparedStatement.
 *
 * $Author: cheechq $
 * $Revision: 1.6 $
 * $Date: 2003/06/03 19:20:23 $
 *
 * $Id: P6OutagePreparedStatement.java,v 1.6 2003/06/03 19:20:23 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/outage/P6OutagePreparedStatement.java,v $
 * $Log: P6OutagePreparedStatement.java,v $
 * Revision 1.6  2003/06/03 19:20:23  cheechq
 * removed unused imports
 *
 * Revision 1.5  2003/01/28 17:01:10  jeffgoke
 * rewrote options to the ability for a module to have its own option set
 *
 * Revision 1.4  2002/12/19 16:29:34  aarvesen
 * Removed the checkReload call
 *
 * Revision 1.3  2002/12/09 21:49:23  aarvesen
 * New constructor
 * jdk 1.4 changes
 *
 * Revision 1.2  2002/10/06 18:22:48  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:31:28  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.8  2002/05/18 06:39:52  jeffgoke
 * Peter Laird added Outage detection.  Added junit tests for outage detection.
 * Fixed multi-driver tests.
 *
 * Revision 1.7  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.6  2002/04/21 06:15:34  jeffgoke
 * added test cases, fixed batch bugs
 *
 * Revision 1.5  2002/04/18 06:54:39  jeffgoke
 * added batch statement logging support
 *
 * Revision 1.4  2002/04/15 05:13:32  jeffgoke
 * Simon Sadedin added timing support.  Fixed bug where batch execute was not
 * getting logged.  Added result set timing.  Updated the log format to include
 * categories, and updated options to control the categories.  Updated
 * documentation.
 *
 * Revision 1.3  2002/04/11 04:18:03  jeffgoke
 * fixed bug where callable & prepared were not passing their ancestors the correct constructor information
 *
 * Revision 1.2  2002/04/10 04:24:26  jeffgoke
 * added support for callable statements and fixed numerous bugs that allowed the real class to be returned
 *
 * Revision 1.1.1.1  2002/04/07 04:52:25  jeffgoke
 * no message
 *
 * Revision 1.2  2001-08-05 09:16:04-05  andy
 * final version on the website
 *
 * Revision 1.1  2001-08-02 07:52:43-05  andy
 * <>
 *
 * Revision 1.0  2001-08-02 06:37:42-05  andy
 * Initial revision
 *
 *
 */

package com.p6spy.engine.outage;

import com.p6spy.engine.spy.*;
import java.sql.*;

public class P6OutagePreparedStatement extends P6PreparedStatement implements PreparedStatement {
    
    
    public P6OutagePreparedStatement(P6Factory factory, PreparedStatement statement, P6Connection conn, String query) {
        super(factory, statement, conn, query);
    }
    
    public boolean execute() throws SQLException {
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,
            "statement", preparedQuery, getQueryFromPreparedStatement());
        }
        
        try {
            return prepStmtPassthru.execute();
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    public ResultSet executeQuery() throws SQLException {
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this, startTime,
            "statement", preparedQuery, getQueryFromPreparedStatement());
        }
        
        try {
            ResultSet resultSet = prepStmtPassthru.executeQuery();
            return (new P6ResultSet(getP6Factory(), resultSet, this, preparedQuery, getQueryFromPreparedStatement()));
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    public int executeUpdate() throws SQLException {
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this, startTime,
            "statement", preparedQuery, getQueryFromPreparedStatement());
        }
        
        try {
            return prepStmtPassthru.executeUpdate();
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    // ---------------------------------------------------------------------------------------
    // we need to override the same methods that P6SLogStatement overrides because we don't have
    // multiple inheritance.  considering the alternatives (delegation), it seems cleaner
    // to just override the methods.  to understand why this is true, realize
    // P6LogPreparedStatement inherits from P6PreparedStatement which inherits from P6Statement,
    // so P6LogPreparedStatement never inherits from P6LogStatement and therefore it does not
    // inherit any of the functionality we define in P6LogStatement.
    // ---------------------------------------------------------------------------------------
    
    public boolean execute(String p0) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return passthru.execute(p0);
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    // Since JDK 1.4
    public boolean execute(String p0, int p1) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return passthru.execute(p0, p1);
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    // Since JDK 1.4
    public boolean execute(String p0, int p1[]) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return passthru.execute(p0, p1);
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    // Since JDK 1.4
    public boolean execute(String p0, String p1[]) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return passthru.execute(p0, p1);
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    public ResultSet executeQuery(String p0) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return (new P6ResultSet(getP6Factory(), passthru.executeQuery(p0), this, "", p0));
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    public int executeUpdate(String p0) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return(passthru.executeUpdate(p0));
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    // Since JDK 1.4
    public int executeUpdate(String p0, int p1) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return(passthru.executeUpdate(p0, p1));
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    // Since JDK 1.4
    public int executeUpdate(String p0, int p1[]) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return(passthru.executeUpdate(p0, p1));
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    // Since JDK 1.4
    public int executeUpdate(String p0, String p1[]) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,"statement","", p0);
        }
        
        try {
            return(passthru.executeUpdate(p0, p1));
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    public void addBatch(String p0) throws java.sql.SQLException {
        statementQuery = p0;
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,
            "batch","", p0);
        }
        
        try {
            passthru.addBatch(p0);
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    public int[] executeBatch() throws java.sql.SQLException {
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,
            "statement", preparedQuery, statementQuery);
        }
        
        try {
            return(passthru.executeBatch());
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
}
