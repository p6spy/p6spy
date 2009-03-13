package com.p6spy.engine.leak;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class P6Objects {

    // Contains each P6 object and its stacktrace
    private static Map objects = new HashMap();

    // Called when each object is opened
    public static void open(final Object object) {
        objects.put(object, getStackTrace());
    }

    // Called when each object is closed
    public static void close(final Object object) {
        objects.remove(object);
    }

    // Returns the Map of open objects
    public static Map getOpenObjects() {
        return objects;
    }

    private static String getStackTrace() {
        //    return getStack("", 15, 2);
        return getStack("", 0, 2);
    }

    private static String getStack(final String msg, int deep, int first) {

        //  msg      a message to appear in the Trace
        // deep = 0  complete stack
        // first     from "first" level, starts at 0

        int n = 0;
        StringBuffer output = new StringBuffer();
        StringWriter sw = new StringWriter();
        new Throwable("").printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        // to clean up the stacktrace
        StringTokenizer st = new StringTokenizer(stackTrace, "\n");

        // get rid of the first line
        String s = st.nextToken();

        if (msg.length() > 0) {
            output.append(msg).append('\n');
        }

        s = st.nextToken();
        s = st.nextToken();

        //    if (s.indexOf("StackTrace.displayStack") < 0) {
        //      output.append(s).append('\n');
        //      // process the stack
        //    }

        if (deep == 0) {
            while (st.hasMoreTokens()) {
                String str = st.nextToken();
                if (n++ >= first) {
                    output.append(str).append('\n');
                }
            }
        } else {
            while (deep > 1) {
                if (st.hasMoreTokens()) {
                    String str = st.nextToken();
                    if (n++ >= first) {
                        output.append(str).append('\n');
                    }
                    deep--;
                } else {
                    deep = 0;
                }
            }
        }

        return output.toString();
    }
}