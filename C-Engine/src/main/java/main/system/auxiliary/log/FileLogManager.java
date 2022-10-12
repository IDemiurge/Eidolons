package main.system.auxiliary.log;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;

public class FileLogManager {

    private static final float WRITE_PERIOD = 5;
    private static float timer;
    public static boolean on= !Flags.isIDE() ;
    private static PrintStream console;
    private static Iterator<LOG_OUTPUT> iterator= Arrays.asList(LOG_OUTPUT.values()).iterator();
    private static boolean fullLoggingConsole= Flags.isCombatGame();

    public static void writeStatInfo(String toString) {
        //TODO
    }


    public static boolean isFullLoggingConsole() {
        return fullLoggingConsole;
    }

    public enum LOG_OUTPUT {
        EXCEPTION,
        ACTIONS,
        MAIN,
        //        STATS,
        FULL,
        LAST,
        INPUT;
        String path;
        PrintStream stream;
        StringBuilder cache = new StringBuilder();

        public void flush() {
            stream.println(cache.toString());
            cache = new StringBuilder();
            stream.flush();
        }

        public void append(String s) {
            cache.append(s);
        }

        LOG_OUTPUT() {
            path = StrPathBuilder.build(PathFinder.getRootPath(), PathFinder.getLogPath(),
                    TimeMaster.getTimeStampForThisSession());
            FileManager.getFile(path).mkdirs();
            path += "/" +
                    CoreEngine.filesVersion + " " +
                    name() + ".txt";
            try {
                FileManager.getFile(path).createNewFile();
                stream = createPrintStream(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static void act(float delta) {
        timer += delta;
        if (timer >= WRITE_PERIOD) {
            timer = 0;
            flushNext();
        }
    }

    public static void flushNext() {
        if (iterator.hasNext()) {
            LOG_OUTPUT output = iterator.next();
            if (isLogged(output)) {
                output.flush();
            }
        } else {
            iterator = Arrays.asList(LOG_OUTPUT.values()).iterator();
        }
    }

    public static void streamInput(String text) {
        stream(LOG_OUTPUT.INPUT, text);
    }

    public static void streamMain(String text) {
        stream(LOG_OUTPUT.MAIN, text);
    }

    public static void streamAction(String text) {
        stream(LOG_OUTPUT.ACTIONS, text);
    }

    public static void stream(LOG_OUTPUT output, String text) {
        if (!isOn()) {
            return;
        }
        if (isAppendTime(output)) {
            text = TimeMaster.getFormattedTime(true, true) + " - " + text;
        }
        output.append(text + "\n");
        if (output != LOG_OUTPUT.FULL) {
            LOG_OUTPUT.FULL.append(text + "\n");
        }
    }

    private static boolean isAppendTime(LOG_OUTPUT value) {
        return true;
    }

    public static boolean isLogged(LOG_OUTPUT value) {
        return on;
    }

    public static boolean isOn() {
        return on ;
    }

    public static boolean isFullLogging() {
        return true;
    }

    public static void writeAll() throws IOException {
        if (isFullLogging()) {
            LOG_OUTPUT.FULL.stream.flush();
            getExceptionPrintStream().flush();
        }
    }


    public static void logException(Exception e) {
        e.printStackTrace(getExceptionPrintStream());
        if (isFullLogging())
            e.printStackTrace(LOG_OUTPUT.FULL.stream);
    }


    public static PrintStream getMainPrintStream() {
        return LOG_OUTPUT.MAIN.stream;
    }

    public static PrintStream getExceptionPrintStream() {
        return LOG_OUTPUT.EXCEPTION.stream;
        //        if (exceptionPrintStream == null) {
        //            try {
        //                String filePath = PathFinder.getRootPath() + PathUtils.getPathSeparator() + getCriticalLogFilePath();
        //                FileManager.getFile(PathUtils.cropLastPathSegment(filePath)).mkdir();
        //                FileManager.getFile(filePath).createNewFile();
        //                exceptionPrintStream = new PrintStream(
        //                        new FileOutputStream(filePath, true));
        //            } catch (FileNotFoundException e) {
        //                e.printStackTrace();
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }
        //        return exceptionPrintStream;
    }

    public static PrintStream getConsolePrintStream() {
        if (console == null) {
            console = createPrintStream(getConsoleLogPath());
        }
        return console;
    }

    private static String getConsoleLogPath() {
        return PathFinder.getRootPath() + "/logs/" + CoreEngine.VERSION + " " +
                TimeMaster.getDateString() + " " + TimeMaster.getTimeStamp() + " console.txt";
    }

    public static PrintStream createPrintStream(String path) {
        try {
            return new PrintStream(
                    new FileOutputStream(path, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
