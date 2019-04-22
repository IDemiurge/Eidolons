package main.system.auxiliary.log;

import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class LogMaster {
    public static final int PRIORITY_INFO = 0;
    public static final int PRIORITY_IMPORTANT = 1;
    public static final int PRIORITY_WARNING = 2;
    public static final int PRIORITY_ERROR = 3;
    public static final int CORE_DEBUG = -1;
    public static final String CORE_DEBUG_PREFIX = "CORE: ";
    public static final int CORE_DEBUG_1 = -100;
    public static final String CORE_DEBUG_1_PREFIX = "CORE_1: ";
    public static final int CONSTRUCTION_DEBUG = -2;
    public static final String CONSTRUCTION_DEBUG_PREFIX = "CONSTRUCTION: ";
    public static final int AI_DEBUG = -3;
    public static final int AI_DEBUG2 = -103;
    public static final String AI_DEBUG_PREFIX = "AI: ";
    public static final int MOVEMENT_DEBUG = -4;
    public static final String MOVEMENT_DEBUG_PREFIX = "MOVEMENT: ";
    public static final int GUI_DEBUG = -5;
    public static final String GUI_DEBUG_PREFIX = "GUI: ";
    public static final int ANIM_DEBUG = -6;
    public static final String ANIM_DEBUG_PREFIX = "ANIM: ";
    public static final int EVENT_DEBUG = -7;
    public static final String EVENT_DEBUG_PREFIX = "EVENT: ";
    public static final int EFFECT_DEBUG = -8;
    public static final String EFFECT_DEBUG_PREFIX = "EFFECT: ";
    public static final int TRIGGER_DEBUG = -9;
    public static final String TRIGGER_DEBUG_PREFIX = "TRIGGER: ";
    public static final int PATHING_DEBUG = -10;
    public static final String PATHING_DEBUG_PREFIX = "PATHING: ";
    public static final int COMBAT_DEBUG = -11;
    public static final String COMBAT_DEBUG_PREFIX = "COMBAT: ";
    public static final int MAP_GENERATION_DEBUG = -12;
    public static final String MAP_GENERATION_DEBUG_PREFIX = "MAP_GENERATION: ";
    public static final int CONDITION_DEBUG = -13;
    public static final String CONDITION_DEBUG_PREFIX = "CONDITION: ";
    public static final int MATH_DEBUG = -14;
    public static final String MATH_DEBUG_PREFIX = "MATH: ";
    public static final int VERBOSE_CHECK = -15;
    public static final String VERBOSE_CHECK_PREFIX = "VERBOSE_CHECK: ";
    public static final int AV_AE = -14;
    public static final int TRAVEL_DEBUG = -16;
    public static final String TRAVEL_DEBUG_PREFIX = "TRAVEL: ";
    public static final int VISIBILITY_DEBUG = -17;
    public static final String VISIBILITY_DEBUG_PREFIX = "VISIBILITY: ";
    public static final int WAITING_DEBUG = -18;
    public static final String WAITING_DEBUG_PREFIX = "WAITING: ";
    public static final int ATTACKING_DEBUG = -19;
    public static final String ATTACKING_DEBUG_PREFIX = "WAITING: ";
    public static final int VALUE_DEBUG = -20;
    public static final String VALUE_DEBUG_PREFIX = "VALUE: ";
    public static final int PERFORMANCE_DEBUG = -21;
    public static final String PERFORMANCE_DEBUG_PREFIX = "TIME: ";
    public static final int DATA_DEBUG = -22;
    public static final String DATA_DEBUG_PREFIX = "DATA: ";
    public static final int GAME_INFO = -23;
    public static final String GAME_INFO_PREFIX = "GAME_INFO: ";
    public static final int RULES_DEBUG = -24;
    public static final String RULES_DEBUG_PREFIX = "RULE: ";
    public static final int THREADING_DEBUG = -25;
    public static final String THREADING_DEBUG_PREFIX = "THREADING: ";
    public static final int WAIT_DEBUG = -26;
    public static final String WAIT_DEBUG_PREFIX = "WAITING: ";
    public static final int BUFF_DEBUG = -27;
    public static final String BUFF_DEBUG_PREFIX = "BUFF: ";
    public static final int WAVE_ASSEMBLING_DEBUG = -28;
    public static final String WAVE_ASSEMBLING_DEBUG_PREFIX = "WAVE_ASSEMBLING: ";
    public static final int LOGIC_DEBUG = -29;
    public static final String LOGIC_DEBUG_PREFIX = "LOGIC: ";
    public static final int EFFECT_SPECIFIC_DEBUG = -30;
    public static final String EFFECT_SPECIFIC_DEBUG_PREFIX = "ACTIVE EFFECT: ";
    public static final int EFFECT_PASSIVE_DEBUG = -31;
    public static final String EFFECT_PASSIVE_DEBUG_PREFIX = "PASSIVE EFFECT: ";
    public static final int MACRO_DYNAMICS = -32;
    public static final String MACRO_DYNAMICS_PREFIX = "MACRO_DYNAMICS: ";
    public static final String GENERATION_PREFIX = "GENERATION: ";
    public static final int GENERATION = -33;
    public static final String AI_TRAINING_PREFIX = "AI_TRAINING: ";
    public static final int AI_TRAINING = -34;
    public static final String ERROR_CRITICAL_PREFIX = "ERROR_CRITICAL: ";
    public static final int ERROR_CRITICAL = -35;
    public static final LOG_CHANNEL[] specialLogChannels = {

    };
    private static final String ERROR_PREFIX = "<!ERROR!>";
    private static final boolean APPEND_TIME = true;
    public static Priority INFO = Priority.toPriority(Priority.INFO_INT);
    public static boolean CORE_DEBUG_ON = false;
    public static boolean CORE_DEBUG_1_ON = false;
    public static boolean CONSTRUCTION_DEBUG_ON = false;
    public static boolean AI_DEBUG_ON = false;
    public static boolean AI_DEBUG_ON2 = true;
    public static boolean MOVEMENT_DEBUG_ON = false;
    public static boolean GUI_DEBUG_ON = false;
    public static boolean ANIM_DEBUG_ON = false;
    public static boolean EVENT_DEBUG_ON = false;
    public static boolean EFFECT_DEBUG_ON = false;
    public static boolean TRIGGER_DEBUG_ON = false;
    public static boolean PATHING_DEBUG_ON = false;
    public static boolean COMBAT_DEBUG_ON = false;
    public static boolean MAP_GENERATION_DEBUG_ON = false;
    public static boolean CONDITION_DEBUG_ON = false;
    public static boolean MATH_DEBUG_ON = false;
    public static boolean TRAVEL_DEBUG_ON = false;
    public static boolean VISIBILITY_DEBUG_ON = false;
    public static boolean WAITING_DEBUG_ON = false;
    public static boolean ATTACKING_DEBUG_ON = false;
    public static boolean VALUE_DEBUG_ON = false;
    public static boolean PERFORMANCE_DEBUG_ON = true;
    public static boolean DATA_DEBUG_ON = false;
    public static boolean GAME_INFO_ON = true;
    public static boolean RULES_DEBUG_ON = false;
    public static boolean THREADING_DEBUG_ON = false;
    public static boolean WAIT_DEBUG_ON = true;
    public static boolean BUFF_DEBUG_ON = true;
    public static boolean WAVE_ASSEMBLING_DEBUG_ON = true;
    public static boolean LOGIC_DEBUG_ON = true;
    public static boolean EFFECT_SPECIFIC_DEBUG_ON = false;
    public static boolean EFFECT_PASSIVE_DEBUG_ON = !true;
    public static boolean MACRO_DYNAMICS_ON = true;
    public static boolean GENERATION_ON = false;
    public static boolean AI_TRAINING_ON = true;
    public static boolean ERROR_CRITICAL_ON = false;
    static String shout = "\n******************\n";
    private static boolean off = false;
    private static int PRIORITY = 1;
    private static Map<LOG_CHANNEL, StringBuilder> logBufferMap;
    private static Set<Exception> exceptions = new LinkedHashSet<>();
    private static String criticalLogFilePath;
    private static String fullLogFilePath;
    private static PrintStream exceptionPrintStream;
    private static PrintStream fullPrintStream;
    private static boolean logBufferOn;

    public static Logger getInstance() {
        String callingClassName = Thread.currentThread().getStackTrace()[2].getClass()
         .getCanonicalName();
        return Logger.getLogger(callingClassName);
    }

    public static void log(String s) {
        if (off) {
            return;
        }
        if (CoreEngine.isExe()) {
            return;
        }

        if (APPEND_TIME) {
            s = TimeMaster.getFormattedTime() + " - " + s;
        }
        if (isFullLogging()){
            getFullPrintStream().println(s);
        }
        System.out.println(s);
    }

    private static boolean isFullLogging() {
        if (!CoreEngine.isIDE())
            return true;
        return false;
    }

    public static void logInNewThread(final String s) {
        new Thread(new Runnable() {
            public void run() {
                log(s);
            }
        }, "logger thread").start();
    }

    public static void gameInfo(String mes) {
        log(LOG_CHANNEL.GAME_INFO, mes);
    }

    public static void log(LOG_CHANNEL c, String s) {
        if (c.isOn()) {
            if (isLogInNewThread()) {
                logInNewThread(c.getPrefix() + s);
            } else {
                log(c.getPrefix() + s);
            }
        }
        if (c.getLog() != null) {
            // TODO Game.game.getLogManager().log(c.getLog(), s);
        }
        LogFileMaster.checkWriteToFileNewThread(c, s);
    }

    public static void writeAll() throws IOException {
        StringBuilder msg = new StringBuilder();

//        for (Exception sub : exceptions) {
//            sub.printStackTrace(new PrintStream(getLogFilePath()));
//        }
        if (isFullLogging()){
            getFullPrintStream().flush();
            getExceptionPrintStream().flush();
        }
//        for (LOG_CHANNEL channel : getLogBufferMap().keySet()) {
//            if (!isChannelLogged(channel))
//                continue;
//
//            StringBuilder s = getLogBufferMap().get(channel);
//            msg.append(channel.getPrefix() + "\n" + s + "\n");
//            //email?..
//        }
//        FileManager.write(msg.toString(), getLogFilePath());
    }

    public static void writeStatInfo(String string) {
                FileManager.write(string, getStatFilePath());
    }


    private static boolean isChannelLogged(LOG_CHANNEL sub) {
        if (sub == null)
            return false;
        switch (sub) {

        }
        return true;
    }

    private static String getLogFilePath() {
        if (fullLogFilePath == null)
            fullLogFilePath = StrPathBuilder.build(PathFinder.getLogPath(),
             CoreEngine.filesVersion+ " full " + TimeMaster.getTimeStampForThisSession() + ".txt");
        return fullLogFilePath;
    }

    private static String getCriticalLogFilePath() {
        if (criticalLogFilePath == null)
            criticalLogFilePath = StrPathBuilder.build(PathFinder.getLogPath(),
             CoreEngine.filesVersion+ " critical " + TimeMaster.getTimeStampForThisSession() + ".txt");
        return criticalLogFilePath;
    }

    private static String getStatFilePath() {
        return PathFinder.getLogPath()+
        "/stats/"+CoreEngine.filesVersion + " game stats "+TimeMaster.getTimeStampForThisSession() + ".txt";
    }
    public static void log(int priority, String s) {

        if (priority < 0) {
            boolean switcher = false;
            String prefix = "";
            LOG_CHANNEL c = LOG_CHANNEL.getByCode(priority);
            if (c != null) {
                switcher = c.isOn();
                prefix = c.getPrefix();
            } else {
                switch (priority) {
                    case THREADING_DEBUG: {
                        switcher = THREADING_DEBUG_ON;
                        prefix = THREADING_DEBUG_PREFIX;
                        break;
                    }
                    case GAME_INFO: {
                        switcher = GAME_INFO_ON;
                        prefix = GAME_INFO_PREFIX;
                        break;

                    }
                    case WAIT_DEBUG: {
                        switcher = WAIT_DEBUG_ON;
                        prefix = WAIT_DEBUG_PREFIX;
                        break;
                    }
                    case RULES_DEBUG: {
                        switcher = RULES_DEBUG_ON;
                        prefix = RULES_DEBUG_PREFIX;
                        break;
                    }
                    case DATA_DEBUG: {
                        switcher = DATA_DEBUG_ON;
                        prefix = DATA_DEBUG_PREFIX;
                        break;
                    }
                    case PERFORMANCE_DEBUG: {
                        switcher = PERFORMANCE_DEBUG_ON;
                        prefix = PERFORMANCE_DEBUG_PREFIX;
                        break;
                    }
                    case VALUE_DEBUG: {
                        switcher = VALUE_DEBUG_ON;
                        prefix = VALUE_DEBUG_PREFIX;
                        break;
                    }
                    case ATTACKING_DEBUG: {
                        switcher = ATTACKING_DEBUG_ON;
                        prefix = ATTACKING_DEBUG_PREFIX;
                        break;
                    }
                    case WAITING_DEBUG: {
                        switcher = WAITING_DEBUG_ON;
                        prefix = WAITING_DEBUG_PREFIX;
                        break;
                    }
                    case VISIBILITY_DEBUG: {
                        switcher = VISIBILITY_DEBUG_ON;
                        prefix = VISIBILITY_DEBUG_PREFIX;
                        break;
                    }
                    case TRAVEL_DEBUG: {
                        switcher = TRAVEL_DEBUG_ON;
                        prefix = TRAVEL_DEBUG_PREFIX;
                        break;
                    }
                    case VERBOSE_CHECK: {
                        switcher = true;
                        prefix = VERBOSE_CHECK_PREFIX;
                        break;
                    }
                    case CONDITION_DEBUG: {
                        switcher = CONDITION_DEBUG_ON;
                        prefix = CONDITION_DEBUG_PREFIX;
                        break;
                    }
                    case MATH_DEBUG: {
                        switcher = MATH_DEBUG_ON;
                        prefix = MATH_DEBUG_PREFIX;
                        break;
                    }
                    case MAP_GENERATION_DEBUG: {
                        switcher = MAP_GENERATION_DEBUG_ON;
                        prefix = MAP_GENERATION_DEBUG_PREFIX;
                        break;
                    }
                    case COMBAT_DEBUG: {
                        switcher = COMBAT_DEBUG_ON;
                        prefix = COMBAT_DEBUG_PREFIX;
                        break;
                    }
                    case TRIGGER_DEBUG: {
                        switcher = TRIGGER_DEBUG_ON;
                        prefix = TRIGGER_DEBUG_PREFIX;
                        break;
                    }
                    case PATHING_DEBUG: {
                        switcher = PATHING_DEBUG_ON;
                        prefix = PATHING_DEBUG_PREFIX;
                        break;
                    }
                    case EFFECT_DEBUG: {
                        switcher = EFFECT_DEBUG_ON;
                        prefix = EFFECT_DEBUG_PREFIX;
                        break;
                    }
                    case EVENT_DEBUG: {
                        switcher = EVENT_DEBUG_ON;
                        prefix = EVENT_DEBUG_PREFIX;
                        break;
                    }
                    case ANIM_DEBUG: {
                        switcher = ANIM_DEBUG_ON;
                        prefix = ANIM_DEBUG_PREFIX;
                        break;
                    }

                    //                    case PHASE_ANIM_DEBUG: {
                    //                        switcher = PHASE_ANIM_DEBUG_ON;
                    //                        prefix = PHASE_ANIM_DEBUG_PREFIX;
                    //                        break;
                    //                    }
                    case CORE_DEBUG_1: {
                        switcher = CORE_DEBUG_1_ON;
                        prefix = CORE_DEBUG_1_PREFIX;
                        break;
                    }
                    case CORE_DEBUG: {
                        switcher = CORE_DEBUG_ON;
                        prefix = CORE_DEBUG_PREFIX;
                        break;
                    }
                    case CONSTRUCTION_DEBUG: {
                        switcher = CONSTRUCTION_DEBUG_ON;
                        prefix = CONSTRUCTION_DEBUG_PREFIX;
                        break;
                    }
                    case AI_DEBUG: {
                        switcher = AI_DEBUG_ON;
                        prefix = AI_DEBUG_PREFIX;
                        break;
                    }
                    case MOVEMENT_DEBUG: {
                        switcher = MOVEMENT_DEBUG_ON;
                        prefix = MOVEMENT_DEBUG_PREFIX;
                        break;
                    }
                    case GUI_DEBUG: {
                        switcher = GUI_DEBUG_ON;
                        prefix = GUI_DEBUG_PREFIX;
                        break;
                    }
                }
            }
            if (switcher) {
                log(prefix + s);
            }
            if (isLogBufferOn())
                addToLogBuffer(c, s);
            return;
        }
        if (priority >= PRIORITY) {
            log(s);
        }
        if (isLogBufferOn())
            addToLogBuffer(null, s);
        //        LogFileMaster.checkWriteToFileNewThread(priority, s);
    }



    private static Map<LOG_CHANNEL, StringBuilder> getLogBufferMap() {
        if (logBufferMap == null) {
            init();
        }
        return logBufferMap;
    }

    private static void init() {
        logBufferMap = new HashMap<>();
        for (LOG_CHANNEL sub : LOG_CHANNEL.values()) {
            logBufferMap.put(sub, new StringBuilder());
        }
        logBufferMap.put(null, new StringBuilder());

        logBufferOn = true;

        FileManager.write("game init", getLogFilePath());
    }

    private static void addToLogBuffer(LOG_CHANNEL c, String s) {
        getLogBufferMap()
         .get(c).append(s + "\n");
    }

    private static boolean isLogInNewThread() {
        return false;
    }

    public static void logToFile(String string) {
        logToFile(string, getLogFileName());
    }

    private static String getLogFileName() {
        return "main log.txt";
    }

    public static void logToFile(String string, String logFileName) {
        logToFile(string, logFileName, false);
    }

    public static void logToFile(String string, String logFileName, boolean append) {
        String content = string;
        String path = PathFinder.getLogPath();
        String fileName = logFileName == null ? getLogFileName() : logFileName;
        // XML_Writer.write(content, path, fileName);
        if (append) {
            FileManager.appendToTextFile(content, path, fileName);
        } else {
            FileManager.write(content, path + "/" + fileName);
        }

    }

    /**
     * @return the off
     */
    public static boolean isOff() {
        return off;
    }

    /**
     * @param off the off to set
     */
    public static void setOff(boolean off) {
        LogMaster.off = off;
    }

    public static void toggle(String e) {
        if (e == null) {
            return;
        }
        LOG_CHANNEL c = new EnumMaster<LOG_CHANNEL>().retrieveEnumConst(LOG_CHANNEL.class, e);
        if (c == null) {
            return;
        }
        c.setOn(!c.isOn());
    }

    public static void error(String string) {
        log(ERROR_PREFIX + string);

    }

    public static Set<Exception> getExceptions() {
        return exceptions;
    }

    public static void logException(Exception e) {
        e.printStackTrace(getExceptionPrintStream());
        e.printStackTrace(getFullPrintStream());
    }

    public static PrintStream getFullPrintStream() {
        if (fullPrintStream == null) {
            try {
                fullPrintStream = new PrintStream(
                 new FileOutputStream(PathFinder.getEnginePath()+ PathUtils.getPathSeparator()+ getLogFilePath(), true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fullPrintStream;
    }
        public static PrintStream getExceptionPrintStream() {
        if (exceptionPrintStream == null) {
            try {
                exceptionPrintStream= new PrintStream(
                 new FileOutputStream(PathFinder.getEnginePath()+ PathUtils.getPathSeparator()+getCriticalLogFilePath(), true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return exceptionPrintStream;
    }

    public static boolean isLogBufferOn() {
        return logBufferOn;
    }

    public static void setLogBufferOn(boolean logBufferOn) {
        LogMaster.logBufferOn = logBufferOn;
    }



    public enum LOG {
        GAME_INFO, HIDDEN_INFO, SYSTEM_INFO, DEBUG
    }

    public enum LOGS {
        COMBAT_LOG, SYS_LOG, CHAT_LOG
    }

}
