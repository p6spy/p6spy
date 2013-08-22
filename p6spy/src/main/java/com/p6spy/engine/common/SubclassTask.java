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
 * Description: Ant task wrapper around the sub class UI
 *		the p6spy functionality
 *
 * $Author: aarvesen $
 * $Revision: 1.1 $
 * $Date: 2003/12/01 00:39:34 $
 *
 * $Id: SubclassTask.java,v 1.1 2003/12/01 00:39:34 aarvesen Exp $
 * $Log: SubclassTask.java,v $
 * Revision 1.1  2003/12/01 00:39:34  aarvesen
 * ant subclassing task
 *
 * Revision 1.1  2003/08/06 19:52:11  aarvesen
 * Class to generate subclasses of DataSources that wrap with P6Spy
 *
 */
package com.p6spy.engine.common;

import javax.sql.DataSource;
import java.lang.reflect.Modifier;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.jar.*;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public class SubclassTask extends Task {

    protected String outputDir;

    protected String outputPackage;

    protected File baseDir;

    protected Path classpath;

    protected List<FileSet> fileSets;

    protected AntClassLoader loader;

    public SubclassTask() {
        fileSets = new ArrayList<FileSet>();
    }

    @Override
    public void execute() throws BuildException {
        // use the fileset defined in the parent (abstract) class
        loader = createClassLoader();

        for (FileSet fileset : fileSets) {
            DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
            String[] files = ds.getIncludedFiles();

            if ( files != null) {
                for (String fileName: files) {
                    if (fileName.endsWith(".class")) {
                        fixAndLoad(fileName);
                    } else if (fileName.endsWith(".jar")) {
                        expandJar(ds.getBasedir(), fileName);
                    } else {
                        log("File '" + fileName + "' is neither a .class nor a .jar file, skipping.", Project.MSG_WARN);
                    }
                }
            }
        }
    }
    public void fixAndLoad(String fileName) {
        // strip off the ".class" at the end
        String cleanString = fileName.substring(0, fileName.indexOf(".class"));
        StringBuffer className = new StringBuffer();

        // change all of the delims to dots... don't use the
        // replace methods on String to preserve some backwards compat
        int start = 0;
        int pos = 0;
        while ((pos = cleanString.indexOf(Subclasser.DELIMITER, start)) > 0) {
            className.append(cleanString.substring(start, pos));
            className.append(".");
            start = pos + 1;
        }

        // catch the tail
        if (start < cleanString.length()) {
            className.append(cleanString.substring(start));
        }

        cleanString = className.toString();
        // try not to further subclass P6 Stuff
        // this is a little weak, but whatever
        if (cleanString.indexOf(".P6") == -1) {
            loadClass(cleanString);
        }
    }

    public void loadClass(String className) {
        try {
            Class k = loader.loadClass(className);
            int mods = k.getModifiers();

            // make sure it's a DataSource and something
            // instantiatable (whew)
            if (DataSource.class.isAssignableFrom(k) && ((mods & Modifier.ABSTRACT) == 0) && ((mods & Modifier.INTERFACE) == 0)
                && ((mods & Modifier.FINAL) == 0)) {

                log("Found class " + className, Project.MSG_WARN);

                Subclasser sub = new Subclasser(k);

                if (outputPackage != null) {
                    sub.setOutputPackage(outputPackage);
                }

                if (outputDir != null) {
                    sub.setOutputDir(outputDir);
                }

                sub.createSubClass();
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public void expandJar(File parentDir, String jarName) {
        try {
            File jarFS = new File(parentDir, jarName);
            JarFile jar = new JarFile(jarFS);

            for (Enumeration e = jar.entries(); e.hasMoreElements();) {
                JarEntry entry = (JarEntry) e.nextElement();

                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    fixAndLoad(entry.getName());
                }

            }
        } catch (IOException e) {
            throw new BuildException(e);
        }

    }

    // this is modelled on the code in the Define abstract task
    protected AntClassLoader createClassLoader() {
        Path localPath = (classpath == null) ? Path.systemClasspath : classpath;
        AntClassLoader loader = new AntClassLoader(getProject(), localPath);
        return loader;
    }

    // weird lowercaseness for Ant
    public String getOutputpackage() {
        return outputPackage;
    }

    public void setOutputpackage(String value) {
        outputPackage = value;
    }

    public String getOutputdir() {
        if (outputDir == null) {
            outputDir = ".";
        }
        return outputDir;
    }

    // do this as a file to let Ant do all the file checking for you
    public void setOutputdir(File file) {
        outputDir = file.getAbsolutePath();
    }

    public void setDir(File dir) {
        baseDir = dir;
    }

    // these are for the classpath here
    public void setClasspath(Path cp) {
        if (classpath == null) {
            classpath = cp;
        } else {
            classpath.append(cp);
        }
    }

    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }

        return classpath.createPath();
    }

    public void addFileset(FileSet fs) {
        fileSets.add(fs);
    }
}
