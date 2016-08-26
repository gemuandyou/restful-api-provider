package gemu.rest.log;

import gemu.rest.core.AmusingProperties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Gemu on 2016/8/26.
 */
public class JavaLog {

    private static BufferedWriter bw;

    static {
        try {
            bw = new BufferedWriter(new FileWriter(AmusingProperties.LOG_CATALOG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JavaLog() {
        try {
            bw = new BufferedWriter(new FileWriter(AmusingProperties.LOG_CATALOG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void info(String message) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement caller = stackTrace[1];
        String w = "info: " + message + "  ->  " + caller.getClassName() + " class; " + caller.getMethodName() + " method; " + caller.getLineNumber() + " line;\n";
        try {
            bw.write(w);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
