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

public class FileLogManager {

    private static final float WRITE_PERIOD = 5000;
    public static boolean on;
    private static PrintStream console;

    public static void writeStatInfo(String toString) {
        //TODO
    }


    public static boolean isFullLoggingConsole() {
        return false;
    }

    public enum LOG_OUTPUT {
        EXCEPTION,
        ACTIONS,
        MAIN,
        //        STATS,
        FULL,
        INPUT;
        String path;
        PrintStream stream;

        LOG_OUTPUT() {
            path = StrPathBuilder.build(PathFinder.getRootPath(),PathFinder.getLogPath(),
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

    private static float timer;

    public static void act(float delta) {
        timer += delta;
        if (timer >= WRITE_PERIOD) {
            timer = 0;
            autoWrite();
        }
    }

    public static void autoWrite() {
        for (LOG_OUTPUT value : LOG_OUTPUT.values()) {
            if (isLogged(value)) {
                value.stream.flush();
            }
        }
    }

    public static void streamInput(String text) {
        stream(LOG_OUTPUT.INPUT, text);
    }

    public static void streamMain(String text) {
        stream(LOG_OUTPUT.MAIN, text);
    }
    public static void streamAction(  String text) {
        stream(LOG_OUTPUT.ACTIONS, text);
    }

        public static void stream(LOG_OUTPUT value, String text) {
            if (Flags.isIDE()) {
                return;
            }
        if (isAppendTime(value)) {
            text = TimeMaster.getFormattedTime(true, true) + " - " + text;
        }
        value.stream.print(text + "\n");
        LOG_OUTPUT.FULL.stream.print(text + "\n");
    }

    private static boolean isAppendTime(LOG_OUTPUT value) {
        return true;
    }

    public static boolean isLogged(LOG_OUTPUT value) {
        return on;
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
        if (console==null) {
            console=createPrintStream(getConsoleLogPath());
        }
        return console;
    }

    private static String getConsoleLogPath() {
        return PathFinder.getRootPath()+"/logs/"+CoreEngine.VERSION+ " "+
                TimeMaster.getDateString()+" "+TimeMaster.getTimeStamp()+" console.txt";
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
