package main.system.auxiliary.log;

import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Strings;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.launch.Flags;
import main.system.threading.TimerTaskMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogFileMasterOld {
    private static final long WRITE_ALL_PERIOD = 5000;
    static Map<LOG_CHANNEL, List<String>> channelLogs = new HashMap<>();
    static Map<Integer, List<String>> priorityLogs = new HashMap<>();
    private static boolean dirty = true;
    private static String gameSubfolder;
    private static boolean writingStarted;
    private static boolean writingPaused;

    public static String getLogFilePath() {
        return PathFinder.getXML_PATH() + "logs/" + TimeMaster.getFormattedDate(false);

    }
    public static void logToFile(String string, String logFileName) {
        logToFile(string, logFileName, false);
    }

    public static void logToFile(String string, String logFileName, boolean append) {
        String content = string;
        String path = PathFinder.getLogPath();
        // XML_Writer.write(content, path, fileName);
//        if (append) {
//            FileManager.appendToTextFile(content, path, fileName);
//        } else {
//            FileManager.write(content, path + "/" + fileName);
//        }

    }
    /*
     * on exit could be an option
     * flush-thread on timer
     * preserve line format
     */
    private static String getGameSubfolder() {
        if (gameSubfolder == null) {
            gameSubfolder = FileManager.getUniqueFileVersion(getGameLogFileNameBase(),
             getLogFilePath());
        }
        return gameSubfolder;

    }

    private static String getGameLogFileNameBase() {
        return " game";// TODO some basic info? groups/dungeon/party...
    }

    public static void writeFullGameLog() {
        String fullContent = null;
        String folder = getLogFilePath();
        folder += FileManager.getUniqueFileVersion(getGameLogFileNameBase(), folder);
        XML_Writer.write(fullContent, folder, "full log.txt");
    }

    public static void startWritingThread() {
        writingStarted = true;
        TimerTaskMaster.newTimer(new LogFileMasterOld(), "writeAll", null, null, WRITE_ALL_PERIOD);

    }

    public static void checkWriteToFile(LOG_CHANNEL channel, String text, boolean writeNow) {
        if (!isWritingLogFilesOn()) {
            return;
        }
        if (channel.getLog() == null) {
            return;
        }
        if (!isLogTypeSupported(channel.getLog())) {
            return;
        }
        // another subfolder!
        List<String> entries = channelLogs.get(channel);
        if (entries == null) {
            entries = new ArrayList<>();
            channelLogs.put(channel, entries);
        }
        entries.add(text);
        if (!writeNow) {
            setDirty(true);
            return;
        }
        String string = channel.getLog().toString() + "/" + channel.toString();
        write(entries, string);

    }

    public static void checkWriteToFile(int priority, String text, boolean writeNow) {
        if (!isWritingLogFilesOn()) {
            return;
        }
        if (priority < getMinPriority()) {
            return;
        }
        List<String> entries = priorityLogs.get(priority);
        if (entries == null) {
            entries = new ArrayList<>();
            priorityLogs.put(priority, entries);
        }
        entries.add(text);
        if (!writeNow) {
            setDirty(true);
            return;
        }
        String string = getPriorityLogFileName(priority);
        write(entries, string);

    }

    private static String getPriorityLogFileName(int priority) {
        return "priority " + priority;
    }

    private static int getMinPriority() {
        return 1;
    }

    private static void write(List<String> entries, String string) {
        String fullContent = ContainerUtils.constructStringContainer(entries, Strings.NEW_LINE);
        String folder = getLogFilePath();
        String fileName = string + " log.txt";
        folder += getGameSubfolder();
        XML_Writer.write(fullContent, folder, fileName);
    }

    private static boolean isLogTypeSupported(LOG log) {
        return true;
    }

    private static boolean isWritingLogFilesOn() {
        return Flags.isWritingLogFilesOn();
    }

    public static void checkWriteToFileNewThread(final LOG_CHANNEL channel, final String text) {
        if (!isWritingLogFilesOn()) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                checkWriteToFile(channel, text, !writingStarted);
            }
        }, "LogFileMaster thread").start();

        if (!writingStarted) {
            startWritingThread();
        }
    }

    public static void checkWriteToFileNewThread(final int priority, final String text) {
        if (!isWritingLogFilesOn()) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                checkWriteToFile(priority, text, !writingStarted);
            }
        }, "LogFileMaster thread").start();

        if (!writingStarted) {
            startWritingThread();
        }
    }

    public static boolean isDirty() {
        return dirty;
    }

    public static void setDirty(boolean dirty) {
        LogFileMasterOld.dirty = dirty;
    }

    public void writeAll() {
        if (LogFileMasterOld.writingPaused) {
            return;
        }
        if (!LogFileMasterOld.isDirty()) {
            return;
        }
        for (final Integer priority : priorityLogs.keySet()) {
            final List<String> entries = channelLogs.get(priority);
            new Thread(new Runnable() {
                public void run() {
                    write(entries, getPriorityLogFileName(priority));
                }
            }, " thread").start();
        }
        for (LOG_CHANNEL channel : channelLogs.keySet()) {
            final List<String> entries = channelLogs.get(channel);
            if (!ListMaster.isNotEmpty(entries)) {
                continue;
            }
            final String string = channel.getLog().toString() + "/" + channel.toString();
            new Thread(new Runnable() {
                public void run() {
                    write(entries, string);
                }
            }, " thread").start();
        }
    }

}
