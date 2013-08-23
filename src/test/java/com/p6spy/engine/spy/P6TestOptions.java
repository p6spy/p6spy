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

package com.p6spy.engine.spy;


import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.p6spy.engine.common.OptionReloader;
import com.p6spy.engine.common.P6SpyOptions;
import com.p6spy.engine.common.P6SpyProperties;

public class P6TestOptions extends P6TestFramework {

	@Ignore
    @Test
    public void testSavingOptions() {
	// should go back and refactor all that
	// kludgy nonsense we do with all the "reloadProperties"
	//
	// anyway.  At this point, I know I've got a file
	// called PROPERTY_FILE written out.  I'm going
	// to test a property value, set it, save it, 
	// reload, test it, change it, reload it.  Got that?

	// some hilarious driver names here
	String muleDriver  = "Borax";
	String remDriver   = "Driver 8";

	chkDriver(muleDriver, false);
	P6SpyOptions.setRealdriver3(muleDriver);
	chkDriver(muleDriver, true);

	P6SpyProperties.saveProperties();
	chkDriver(muleDriver, true);

	P6SpyOptions.setRealdriver3(remDriver);
	chkDriver(remDriver, true);

	// now reload that saved goodness and we'll should see
	// the muleDriver pop backup
	OptionReloader.reload();
	chkDriver(muleDriver, true);

	// clean up the file, just in case
	P6SpyOptions.setRealdriver3("");
	P6SpyProperties.saveProperties();
    }

    protected void chkDriver(String expected, boolean equals) {
	String actual = P6SpyOptions.getRealdriver3();
	if (equals) {
	    assertTrue("expected a driver 3 of '" + expected + "' but found '" + actual + "'", expected.compareTo(actual) == 0);
	} else {
	    assertTrue("expected a driver 3 to not be '" + expected + "' but it was'" + actual + "'", (actual == null) || (expected.compareTo(actual) != 0));
	}

    }
}
