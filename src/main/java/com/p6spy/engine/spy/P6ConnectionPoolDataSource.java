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
 *  Support for PooledConnection interface requires 
 *  an aware DataSource to produce the PooledConnection
 *  ojects. This is it.
 *
 * $Author: aarvesen $
 *
 * $Log: P6ConnectionPoolDataSource.java,v $
 * Revision 1.5  2003/08/07 19:06:38  aarvesen
 * removed the synchro's
 * changed the imports slightly
 * call up to the new constructor
 *
 * Revision 1.4  2003/08/04 19:33:13  aarvesen
 * removed the flawed implementation of trying to wrap a normal p6 connection in a pooled connection
 *
 * Revision 1.3  2003/06/03 19:20:25  cheechq
 * removed unused imports
 *
 * Revision 1.2  2003/01/30 23:38:49  dlukeparker
 *
 *
 * Added cvs keywords
 *
 * Revision 1.1  2003/01/30 23:35:22  dlukeparker
 *
 *
 * Added support for the javax.sql operations for pooled connections. This
 * is required for support of WebSphere.
 *
 * Finished implementation of com/p6spy/engine/spy/P6DataSource.java
 *
 * Added com/p6spy/engine/spy/P6ConnectionPoolDataSource.java
 * Added com/p6spy/engine/spy/P6DataSourceFactory.java
 * Added com/p6spy/engine/spy/P6PooledConnection.java
 * Added com/p6spy/engine/spy/P6ProxyConnection.java
 *
 * Made changes in spy.properties and com/p6spy/engine/common/P6SpyOptions.java
 * to enable datasource name, driver and properties setting. Also added support
 * for specifying the JNDI context for finding the real datasource.
 *
 *
 */

package com.p6spy.engine.spy;

import java.sql.*;
import javax.sql.*;

public class P6ConnectionPoolDataSource extends P6DataSource implements ConnectionPoolDataSource {

    public P6ConnectionPoolDataSource() {
      super();
    }

    public P6ConnectionPoolDataSource(DataSource ds) {
      super(ds);
    }

    public PooledConnection getPooledConnection() throws SQLException {
      if (rds == null) {
	bindDataSource();
      }

      PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection();
      P6PooledConnection pooledConnection = new P6PooledConnection(pc);
      return pooledConnection;
    }
    
    
    public PooledConnection getPooledConnection(String s, String s1) throws SQLException {

      if (rds == null) {
	bindDataSource();
      }

      PooledConnection pc = ((ConnectionPoolDataSource) rds).getPooledConnection(s, s1);
      P6PooledConnection pooledConnection = new P6PooledConnection(pc);
      return pooledConnection;
    }

}
