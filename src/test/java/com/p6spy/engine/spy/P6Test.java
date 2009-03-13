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
 * Description: Test class to run all tests $Author: aarvesen $ $Revision: 1.11 $ $Date: 2003/08/06
 * 19:53:33 $ $Id: P6Test.java,v 1.11 2003/08/06 19:53:33 aarvesen Exp $ $Source:
 * /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6Test.java,v $ $Log: P6Test.java,v $ Revision 1.11
 * 2003/08/06 19:53:33 aarvesen added in the subclass tester Revision 1.10 2003/08/04 19:35:08
 * aarvesen Added in the pooled test Revision 1.9 2003/06/03 19:20:26 cheechq removed unused imports
 * Revision 1.8 2003/03/07 22:07:34 aarvesen added test for unloading of registered drivers Revision
 * 1.7 2003/02/14 22:23:48 aarvesen added test options to test saving properties files Revision 1.6
 * 2003/01/22 00:00:30 jeffgoke removed P6Options reference Revision 1.5 2002/12/19 23:44:21
 * aarvesen Added TestDriver Revision 1.4 2002/12/12 19:28:33 aarvesen call test options along with
 * the other tests Revision 1.3 2002/12/12 01:39:02 jeffgoke no message Revision 1.2 2002/10/06
 * 18:24:04 jeffgoke no message Revision 1.1 2002/05/24 07:30:46 jeffgoke version 1 rewrite Revision
 * 1.1 2002/04/21 06:16:20 jeffgoke added test cases, fixed batch bugs
 */

package com.p6spy.engine.spy;

import junit.framework.*;

public class P6Test extends TestCase {

    public P6Test(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(P6TestBasics.class);
        suite.addTestSuite(P6TestStatement.class);
        suite.addTestSuite(P6TestCallableStatement.class);
        suite.addTestSuite(P6TestPreparedStatement.class);
        suite.addTestSuite(P6TestCommon.class);
        suite.addTestSuite(P6TestDriver.class);
        suite.addTestSuite(P6TestOptions.class);
        suite.addTestSuite(P6TestUnloading.class);
        //        suite.addTestSuite(P6TestPooled.class);
        suite.addTestSuite(P6TestSubclasser.class);
        return suite;
    }
}
