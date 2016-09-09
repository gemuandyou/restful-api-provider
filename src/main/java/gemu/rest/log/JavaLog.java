package gemu.rest.log;

import gemu.rest.core.AmusingProperties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Gemu on 2016/8/26.
 */
public class JavaLog {

    private static BufferedWriter bwInfo;
    private static BufferedWriter bwDebug;
    private static BufferedWriter bwIssue;

    static {
        try {
            bwInfo = new BufferedWriter(new FileWriter(AmusingProperties.INFO_LOG_CATALOG));
            bwDebug = new BufferedWriter(new FileWriter(AmusingProperties.DEBUG_LOG_CATALOG));
            bwIssue = new BufferedWriter(new FileWriter(AmusingProperties.ISSUE_LOG_CATALOG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JavaLog(String type) {
        try {
            if ("log".equals(type.toLowerCase()))
                bwInfo = new BufferedWriter(new FileWriter(AmusingProperties.INFO_LOG_CATALOG));
            if ("debug".equals(type.toLowerCase()))
                bwDebug = new BufferedWriter(new FileWriter(AmusingProperties.DEBUG_LOG_CATALOG));
            if ("issue".equals(type.toLowerCase()))
                bwIssue = new BufferedWriter(new FileWriter(AmusingProperties.ISSUE_LOG_CATALOG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void info(String message) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement caller = stackTrace[1];
        String w = "info: " + message + "  ->  " + caller.getClassName() + " class; " + caller.getMethodName() + " method; " + caller.getLineNumber() + " line;\n";
        try {
            bwInfo.write(w);
            bwInfo.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void debug(String message) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement caller = stackTrace[1];
        String w = "debug: " + message + "  ->  " + caller.getClassName() + " class; " + caller.getMethodName() + " method; " + caller.getLineNumber() + " line;\n";
        try {
            bwDebug.write(w);
            bwDebug.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void issue(String message) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement caller = stackTrace[1];
        String w = "issue: " + message + "  ->  " + caller.getClassName() + " class; " + caller.getMethodName() + " method; " + caller.getLineNumber() + " line;\n";
        try {
            bwIssue.write(w);
            bwIssue.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
