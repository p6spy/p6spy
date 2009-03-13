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
 * Description: factory for getting real values.
 *
 * $Author: jeffgoke $
 * $Revision: 1.5 $
 * $Date: 2003/01/28 17:01:09 $
 *
 * $Id: P6OutageFactory.java,v 1.5 2003/01/28 17:01:09 jeffgoke Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/outage/P6OutageFactory.java,v $
 * $Log: P6OutageFactory.java,v $
 * Revision 1.5  2003/01/28 17:01:09  jeffgoke
 * rewrote options to the ability for a module to have its own option set
 *
 * Revision 1.4  2003/01/21 22:50:34  jeffgoke
 * removed code no longer necessary due to the new module
 *
 * Revision 1.3  2002/12/09 21:48:04  aarvesen
 * New constructor
 *
 * Revision 1.2  2002/10/06 18:22:48  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:31:28  jeffgoke
 * version 1 rewrite
 *
 *
 *
 */

package com.p6spy.engine.outage;

import java.sql.*;
import com.p6spy.engine.spy.*;
import com.p6spy.engine.common.*;

public class P6OutageFactory extends P6CoreFactory {
    
    public P6OutageFactory() {
    }
    
    public Connection getConnection(Connection conn) throws SQLException {
        return (new P6OutageConnection(this, conn));
    }
    
    public PreparedStatement getPreparedStatement(PreparedStatement real, P6Connection conn, String p0) throws SQLException {
        return (new P6OutagePreparedStatement(this, real, conn, p0));
    }
    
    public Statement getStatement(Statement statement, P6Connection conn) throws SQLException {
        return (new P6OutageStatement(this, statement, conn));
    }
    
    public CallableStatement getCallableStatement(CallableStatement real, P6Connection conn, String p0) throws SQLException {
        return (new P6OutageCallableStatement(this, real, conn, p0));
    }
    
    public P6Options getOptions() throws SQLException {
        return (new P6OutageOptions());
    }
    
}
