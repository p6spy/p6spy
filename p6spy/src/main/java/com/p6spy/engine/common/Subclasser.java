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
