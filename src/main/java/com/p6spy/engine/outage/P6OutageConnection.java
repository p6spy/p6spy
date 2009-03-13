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
 * Description: Wrapper class for Connection
 *
 * $Author: cheechq $
 * $Revision: 1.6 $
 * $Date: 2003/06/03 19:20:23 $
 *
 * $Id: P6OutageConnection.java,v 1.6 2003/06/03 19:20:23 cheechq Exp $
 * $Log: P6OutageConnection.java,v $
 * Revision 1.6  2003/06/03 19:20:23  cheechq
 * removed unused imports
 *
 * Revision 1.5  2003/01/28 17:01:08  jeffgoke
 * rewrote options to the ability for a module to have its own option set
 *
 * Revision 1.4  2002/12/19 16:29:03  aarvesen
 * Removed the checkReload call
 *
 * Revision 1.3  2002/12/09 21:46:07  aarvesen
 * New constructor
 * jdk 1.4 changes
 *
 * Revision 1.2  2002/10/06 18:22:48  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:31:28  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.7  2002/05/18 06:39:52  jeffgoke
 * Peter Laird added Outage detection.  Added junit tests for outage detection.
 * Fixed multi-driver tests.
 *
 * Revision 1.6  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.5  2002/04/27 20:24:01  jeffgoke
 * added logging of commit statements and rollback statements
 *
 * Revision 1.4  2002/04/11 04:18:03  jeffgoke
 * fixed bug where callable & prepared were not passing their ancestors the correct constructor information
 *
 * Revision 1.3  2002/04/10 04:24:26  jeffgoke
 * added support for callable statements and fixed numerous bugs that allowed the real class to be returned
 *
 * Revision 1.2  2002/04/07 20:43:59  jeffgoke
 * fixed bug that caused null connection to return an empty connection instead of null.
 * added an option allowing the user to truncate.
 * added a release target to the build to create the release files.
 *
 * Revision 1.1.1.1  2002/04/07 04:52:25  jeffgoke
 * no message
 *
 * Revision 1.2  2001-08-02 07:52:43-05  andy
 * <>
 *
 * Revision 1.1  2001-07-30 23:03:31-05  andy
 * <>
 *
 * Revision 1.0  2001-07-30 17:46:22-05  andy
 * Initial revision
 *
 */

package com.p6spy.engine.outage;

import com.p6spy.engine.spy.*;
import java.sql.*;

public class P6OutageConnection extends P6Connection implements java.sql.Connection {
    
    
    public P6OutageConnection(P6Factory factory, Connection conn) throws SQLException {
        super(factory, conn);
    }
    
    public void commit() throws SQLException {
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,
            "commit","", "");
        }
        
        try {
            passthru.commit();
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    public void rollback() throws SQLException {
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,
            "rollback","", "");
        }
        
        try {
            passthru.rollback();
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
    
    // Since JDK 1.4
    public void rollback(Savepoint p0) throws SQLException {
        long startTime = System.currentTimeMillis();
        
        if (P6OutageOptions.getOutageDetection()) {
            P6OutageDetector.getInstance().registerInvocation(this,startTime,
            "rollback","", "");
        }
        
        try {
            passthru.rollback(p0);
        }
        finally {
            if (P6OutageOptions.getOutageDetection()) {
                P6OutageDetector.getInstance().unregisterInvocation(this);
            }
        }
    }
}
