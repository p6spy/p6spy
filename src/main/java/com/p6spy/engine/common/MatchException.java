/*
Copyright 2013 P6Spy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.p6spy.engine.common;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Description: An adaptor to allow custome Matcher implementations to throw
 *              nested exceptions.
 *
 * $Author: jeffgoke $
 * $Revision: 1.1 $
 * $Date: 2002/05/24 07:32:01 $
 *
 * $Id: MatchException.java,v 1.1 2002/05/24 07:32:01 jeffgoke Exp $
 * $Log: MatchException.java,v $
 * Revision 1.1  2002/05/24 07:32:01  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.1  2002/04/22 02:27:04  jeffgoke
 * Simon Sadedin added timing information.  Added Junit tests.
 *
 */
public class MatchException extends Exception {
  public MatchException(String message, Exception nested) {
    super(message);
    this.nested = nested;
  }
  public void printStackTrace(PrintStream s) {
    super.printStackTrace(s);
    if(nested != null)
      nested.printStackTrace(s);
  }
  public void printStackTrace(PrintWriter s) {
    super.printStackTrace(s);
    if(nested != null)
      nested.printStackTrace(s);
  }

  Exception nested;
}
