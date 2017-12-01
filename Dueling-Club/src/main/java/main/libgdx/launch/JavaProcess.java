package main.libgdx.launch;

import java.io.File;
import java.io.IOException;

/**
 * Created by JustMe on 11/28/2017.
 */
public final class JavaProcess {

    private JavaProcess() {}

    public static int exec(Class klass) throws IOException,
     InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
         File.separator + "bin" +
         File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = klass.getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
         javaBin, "-cp", classpath, className);

        Process process = builder.inheritIO().start();
//        builder.inheritIO(); didn't work!
        process.waitFor();
        return process.exitValue();
    }

}
