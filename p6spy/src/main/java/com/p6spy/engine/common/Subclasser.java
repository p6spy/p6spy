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
 * Description: Creates a subclass from the specifed DataSource to wrap in
 *		the p6spy functionality
 *
 * $Author: aarvesen $
 * $Revision: 1.2 $
 * $Date: 2003/12/01 00:40:18 $
 *
 * $Id: Subclasser.java,v 1.2 2003/12/01 00:40:18 aarvesen Exp $
 * $Log: Subclasser.java,v $
 * Revision 1.2  2003/12/01 00:40:18  aarvesen
 * fixed parent package bug
 * added new constructor
 * filled in main method
 *
 * Revision 1.1  2003/08/06 19:52:11  aarvesen
 * Class to generate subclasses of DataSources that wrap with P6Spy
 *
 */
package com.p6spy.engine.common;

import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.*;

public class Subclasser {
  public static String DELIMITER = System.getProperty("file.separator");
  public static String NEWLINE   = System.getProperty("line.separator");
  public static String INDENT    = "  ";
  public static String DEFAULT_PACKAGE = "com.p6spy.engine.subclass";

  protected Class parentClass; 

  protected String outputDir;
  protected String packageName;
  protected String outputName;

  public Subclasser() {
  } 

  public Subclasser(String className) throws ClassNotFoundException {
    this(P6Util.forName(className));
  } 

  public Subclasser(Class c) {
    setParentClass(c);
  } 


  /*
   * Simple invocation method: java -cp  p6spy.jar com.p6spy.engine.common.Subclasser some_class_file
   */
  public static void main(String[] args) {
    int size = args.length;
    for (int i = 0; i < size; i++) {
      try {
	Subclasser sub = new Subclasser(args[i]);
	sub.createSubClass();	
      } catch (Exception e) {
	e.printStackTrace();
      }
    }
  }

  public void createSubClass() throws Exception {
    // take the parent class.  It's a data source,
    // emit a new subclass with just the pieces that you care
    // about written over

    // do a brief sanity check
    if (parentClass == null) {
      throw new Exception("Parent Class must not be null");
    }

    if (! DataSource.class.isAssignableFrom(parentClass)) {
      throw new Exception("Parent Class " + parentClass.getName() + " is not an instanceof javax.sql.DataSource");
    }

    // okay, ready to go.
    // get the output file...
    File output = getOutputFile();
    if (output.getParent() != null) {
      output.getParentFile().mkdirs();
    }
    output.createNewFile();

    FileWriter writer = new FileWriter(output);

    writer.write(writeHeader());
    writer.write(writeConstructors());
    writer.write(overrideConnection());
    writer.write(writeFooter());
    writer.flush();
    writer.close();

    // done!

  }

  /*---------------------------------------------------------------------*\
   *                      getters and setters
  \*---------------------------------------------------------------------*/    

  public Class getParentClass() {
    return parentClass;
  }
  public void setParentClass(Class c) {
    parentClass = c;
  }

  public String getParentPackage() {
    String rv = null;
    if (parentClass != null) {
      Package pack = parentClass.getPackage();

      // had some problems with the AntClassLoader not
      // defining the package information
      if (pack != null) {
	rv = pack.getName();
      } else {
	rv = packageName(parentClass.getName());
      }
    }

    return rv;
  }

  public File getOutputFile() {
    String dir = getOutputDir();
    String fullDir = packToDir(getOutputPackage());
    String name = getOutputName();

    if (dir != "") {
      fullDir = dir + DELIMITER + fullDir;
    }

    return new File(fullDir, name + ".java");
  }

  public String getOutputDir() {
    if (outputDir == null) {
      outputDir = "scratch";
    }
    return outputDir;
  }

  public void setOutputDir(String value) {
    outputDir = value;
  }

  public String getOutputPackage() {
    if (packageName == null) {
      packageName = DEFAULT_PACKAGE;
    }
    return packageName;
  }

  public void setOutputPackage(String value) {
    packageName= value;
  }

  public String getOutputName() {
    if (outputName == null && parentClass != null) {
      outputName = "P6" + baseName(parentClass.getName());
    }
    return outputName;
  }

  public void setOutputName(String value) {
    outputName = value;
  }

  /*---------------------------------------------------------------------*\
   *                     utility methods 
  \*---------------------------------------------------------------------*/    
  /**
   * derive "Foo" from "com.p6spy.package.Foo"
   */
  public String baseName(String clazz) {
    String rv = null;
    int pos = clazz.lastIndexOf(".");
    if (pos != -1) {
      rv = clazz.substring(pos + 1);
    }
    return rv;
  }

  /**
   * derive "com.p6spy.package" from "com.p6spy.package.Foo"
   */
  public String packageName(String clazz) {
    String rv = null;
    int pos = clazz.lastIndexOf(".");
    if (pos != -1) {
      rv = clazz.substring(0, pos);
    }
    return rv;
  }

  /**
   * Change "com.p6spy.package" into "com/p6spy/pacakge" or 
   * "com\p6spy\package" or
   * "com:p6spy:package" or
   * whatever
   */
  public String packToDir(String pack) {
    StringBuffer sb = new StringBuffer();
    int length = pack.length();
    for (int i = 0; i < length; i++) {
      char c = pack.charAt(i);
      if ( c == '.') {
	sb.append(DELIMITER);
      } else {
	sb.append(c);
      }
    }
    return sb.toString();
  }

  /*---------------------------------------------------------------------*\
   *                    heavy lifting here 
  \*---------------------------------------------------------------------*/    
  public String writeHeader() throws Exception {
    StringBuffer sb = new StringBuffer(); 
    String parentPack = getParentPackage();

    //package declaration
    sb.append("// this class generated by " + this.getClass());
    sb.append(NEWLINE);
    sb.append(NEWLINE);
    sb.append("package " + getOutputPackage() + ";");

    // now the nec. imports
    // some of these are just guesses
    sb.append(NEWLINE);
    sb.append(NEWLINE);
    sb.append("import com.p6spy.engine.spy.*;" + NEWLINE);
    sb.append("import java.sql.*;" + NEWLINE);
    sb.append("import javax.sql.*;" + NEWLINE);
    sb.append("import " + parentPack + ".*;" + NEWLINE);

    // now, let's do the class declaration
    sb.append(NEWLINE);
    sb.append(NEWLINE);
    sb.append("public class " + getOutputName() + " extends " + getParentClass().getName() + " {");
    sb.append(NEWLINE);

    return sb.toString();
  }

  public String writeConstructors() throws Exception {
    StringBuffer sb = new StringBuffer();
    Class parent = getParentClass();
    Constructor[] conList = parent.getDeclaredConstructors();
    int length = conList.length;

    if (length == 0) {
      throw new Exception("No constructors found; is " + parent + " an interface?");
    }

    // now, for each constructor, we have to create a new thing.
    for (int i = 0; i < length; i++) {
      Constructor c = conList[i];
      int mods = c.getModifiers();
      Class[] params = c.getParameterTypes();
      Class[] exceps = c.getExceptionTypes();

      sb.append(NEWLINE);

      // create the modifies and name:
      // "public FooClass"
      sb.append(INDENT);
      sb.append(Modifier.toString(mods));
      sb.append(" " + getOutputName());
    
      // now, get all the params, if any
      // " (String p0, int p1)"
      sb.append(" (");
      for (int j = 0; j < params.length; j++) {
	Class param = params[j];
	if (j > 0) {
	  sb.append(", ");
	}
	sb.append(param.getName());
	sb.append(" p" + j);
      }

      sb.append(")");

      // now get all the exceptions
      if (exceps.length > 0) {
	sb.append (" throws ");
	for (int j = 0; j < exceps.length; j++) {
	  Class ex = exceps[j];
	  if (j > 0) {
	    sb.append(", ");
	  }
	  sb.append(ex.getName());
	}
      }

      // finally, close with a brace
      sb.append(" {");
      sb.append(NEWLINE);

    
      // first line is now done - whew!
      // Now, simply call super
      sb.append(INDENT);
      sb.append(INDENT);
      sb.append("super(");
      
      for (int j = 0; j < params.length; j++) {
	Class param = params[j];
	if (j > 0) {
	  sb.append(", ");
	}
	sb.append(" p" + j);
      }
      sb.append(");");

      // now put in the closing brace on the next line
      // and we're done.  Move on to the next constructor, if any
      sb.append(NEWLINE);
      sb.append(INDENT);
      sb.append("}");
    }

    return sb.toString();
  }

  public String overrideConnection() {
    StringBuffer sb = new StringBuffer();
    sb.append(overrideConnection("", ""));
    sb.append(overrideConnection("String username, String password", "username, password"));
    return sb.toString();
  }

  public String overrideConnection(String signature, String names) {
    String value = "" + 

    NEWLINE +

    INDENT + 
    "public Connection getConnection(" + signature + ") throws SQLException {" + 
    NEWLINE +

    INDENT + 
    INDENT + 
     "return P6SpyDriverCore.wrapConnection(super.getConnection(" + names + "));" +
    NEWLINE +

    INDENT + 
    "};" +
    NEWLINE +
    "";

    return value;
  }

  public String writeFooter() {
    return NEWLINE + "}" + NEWLINE;
  }
}
