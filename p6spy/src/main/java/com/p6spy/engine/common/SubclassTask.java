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
