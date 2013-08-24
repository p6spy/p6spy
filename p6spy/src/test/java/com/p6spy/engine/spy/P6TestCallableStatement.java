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
 * $Author: bradleydot $
 * $Revision: 1.3 $
 * $Date: 2003/08/06 18:50:59 $
 *
 * $Id: P6TestCallableStatement.java,v 1.3 2003/08/06 18:50:59 bradleydot Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestCallableStatement.java,v $
 * $Log: P6TestCallableStatement.java,v $
 * Revision 1.3  2003/08/06 18:50:59  bradleydot
 * Added TestCallable to verify that values array size will grow appropriately
 * when registerOutParameter methods are called.
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

import java.sql.CallableStatement;

import org.junit.Ignore;
import org.junit.Test;

public class P6TestCallableStatement extends P6TestPreparedStatement {

	@Ignore
    @Test
    public void testCallable () throws Exception {
       int bigParam = 1024;
       int valuesLen;
       int setterMax = 32;
       StringBuffer testproc = new StringBuffer(bigParam);

       // set
       testproc.append("CALL TEST.METHOD (");
       for (int i = 0; i < bigParam; i++) {
         testproc.append("?");
       }
       testproc.append(")");

       CallableStatement call = connection.prepareCall(testproc.toString());

       for (int x=1; x<=setterMax;x++){
        String tmpstring = ("String" +x);
        call.setString(x, tmpstring);
        }
       
       setterMax++;
       
       try {
       call.registerOutParameter(setterMax,java.sql.Types.INTEGER);
       // values should be grown after this call since
       // setterMax is greater than array length
       valuesLen = ((P6CallableStatement)call).getValuesLength();
       assertEquals(setterMax+P6CallableStatement.P6_GROW_MAX, valuesLen);

       //  try various registerOutParameter methods
       call.registerOutParameter(1,java.sql.Types.INTEGER);

       setterMax+=P6CallableStatement.P6_GROW_MAX;
       call.registerOutParameter(setterMax,java.sql.Types.FLOAT,3);
       // values should be grown after this call since
       // setterMax is greater than array length
       valuesLen = ((P6CallableStatement)call).getValuesLength();
       assertEquals(setterMax+P6CallableStatement.P6_GROW_MAX, valuesLen);

       call.registerOutParameter(setterMax+3,java.sql.Types.INTEGER);
       call.registerOutParameter(bigParam,java.sql.Types.INTEGER);
       } catch (Exception e)  {
         fail(e.getMessage()+" Failed Registering Out Parameter: " + getStackTrace(e));
       }

       // last register out with param of bigParam will cause
       // values to be grown based on bigParam.  Test follows...
       valuesLen = ((P6CallableStatement)call).getValuesLength();
       assertEquals(bigParam+P6CallableStatement.P6_GROW_MAX, valuesLen);
       call.close();
    }

}
