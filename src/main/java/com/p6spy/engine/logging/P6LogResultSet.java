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
 * $Date: 2003/06/03 19:20:22 $
 *
 * $Id: P6LogResultSet.java,v 1.6 2003/06/03 19:20:22 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/logging/P6LogResultSet.java,v $
 * $Log: P6LogResultSet.java,v $
 * Revision 1.6  2003/06/03 19:20:22  cheechq
 * removed unused imports
 *
 * Revision 1.5  2002/12/19 17:00:12  aarvesen
 * remove getTrace from the driver level
 *
 * Revision 1.4  2002/12/19 16:31:43  aarvesen
 * Removed the checkReload call
 *
 * Revision 1.3  2002/12/06 22:27:31  aarvesen
 * new factory registration in the constructor
 *
 * Revision 1.2  2002/10/06 18:22:12  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:31:45  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.3  2002/05/16 04:58:40  jeffgoke
 * Viktor Szathmary added multi-driver support.
 * Rewrote P6SpyOptions to be easier to manage.
 * Fixed several bugs.
 *
 * Revision 1.2  2002/04/15 05:13:32  jeffgoke
 * Simon Sadedin added timing support.  Fixed bug where batch execute was not
 * getting logged.  Added result set timing.  Updated the log format to include
 * categories, and updated options to control the categories.  Updated
 * documentation.
 *
 * Revision 1.1  2002/04/10 04:24:26  jeffgoke
 * added support for callable statements and fixed numerous bugs that allowed the real class to be returned
 *
 *
 *
 */

package com.p6spy.engine.logging;

import com.p6spy.engine.spy.*;
import com.p6spy.engine.common.*;
import java.sql.*;

public class P6LogResultSet extends P6ResultSet implements ResultSet {
    
    
    public P6LogResultSet(P6Factory factory, ResultSet resultSet, P6Statement statement, String preparedQuery, String query) {
        super(factory, resultSet, statement, preparedQuery, query);
    }
    
    public boolean next() throws SQLException {
        long startTime = System.currentTimeMillis();
        try {
            return super.next();
        }
        finally {
	    P6Connection p6connection = (P6Connection)this.statement.getConnection();
	    P6LogQuery.logElapsed(p6connection.getId(), startTime, "result", preparedQuery, query);
        }
    }
    
}

