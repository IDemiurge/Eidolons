package main.system.auxiliary.log;

import main.data.filesys.PathFinder;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

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
    public static boolean ANIM_DEBUG_ON = true;
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
    public static boolean AI_TRAINING_ON=true;
    static String shout = "\n******************\n";
    private static boolean off = false;
    private static int PRIORITY = 1;

    public static Logger getInstance() {
        String callingClassName = Thread.currentThread().getStackTrace()[2].getClass()
                .getCanonicalName();
        return Logger.getLogger(callingClassName);
    }

    public static void shout(String s) {
if (!CoreEngine.isExe())
        System.out.println(shout + s + shout);
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
        System.out.println(s);
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
                if (isLogInNewThread()) {
                    logInNewThread(prefix + s);
                } else {
                    log(prefix + s);
                }
                // if (logs != null) {
                // for (Log log : logs)
                // log.log(prefix + s);
                // }
                // if (MicroGame.game.getLog() != null)
                // MicroGame.game.getLog().sysLog(s);
            }
        }
        if (priority >= PRIORITY) {
            log(s);
        }

        LogFileMaster.checkWriteToFileNewThread(priority, s);
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
            FileManager.write(content, path + "\\" + fileName);
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

    public enum LOG {
        GAME_INFO, HIDDEN_INFO, SYSTEM_INFO, DEBUG
    }

    public enum LOGS {
        COMBAT_LOG, SYS_LOG, CHAT_LOG
    }

    public enum LOG_CHANNEL {
        EFFECT_ACTIVE_DEBUG(LogMaster.EFFECT_SPECIFIC_DEBUG_PREFIX, LogMaster.EFFECT_SPECIFIC_DEBUG_ON, LogMaster.EFFECT_SPECIFIC_DEBUG),

        EFFECT_PASSIVE_DEBUG(LogMaster.EFFECT_PASSIVE_DEBUG_PREFIX, LogMaster.EFFECT_PASSIVE_DEBUG_ON, LogMaster.EFFECT_PASSIVE_DEBUG),
        WAVE_ASSEMBLING(LogMaster.WAVE_ASSEMBLING_DEBUG_PREFIX, LogMaster.WAVE_ASSEMBLING_DEBUG_ON, LogMaster.WAVE_ASSEMBLING_DEBUG),
        CONDITION_DEBUG(LogMaster.CONDITION_DEBUG_PREFIX, LogMaster.CONDITION_DEBUG_ON, LogMaster.CONDITION_DEBUG),

        CORE_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        EVENT_DEBUG(LogMaster.EVENT_DEBUG_PREFIX, LogMaster.EVENT_DEBUG_ON, LogMaster.EVENT_DEBUG),
        TRIGGER_DEBUG(LogMaster.TRIGGER_DEBUG_PREFIX, LogMaster.TRIGGER_DEBUG_ON, LogMaster.TRIGGER_DEBUG),
        EFFECT_DEBUG(LogMaster.EFFECT_DEBUG_PREFIX, LogMaster.EFFECT_DEBUG_ON, LogMaster.EFFECT_DEBUG),
        PERFORMANCE_DEBUG(LogMaster.PERFORMANCE_DEBUG_PREFIX, LogMaster.PERFORMANCE_DEBUG_ON, LogMaster.PERFORMANCE_DEBUG),
        WAIT_DEBUG(LogMaster.WAIT_DEBUG_PREFIX, LogMaster.WAIT_DEBUG_ON, LogMaster.WAIT_DEBUG),
        RULES_DEBUG(LogMaster.RULES_DEBUG_PREFIX, LogMaster.RULES_DEBUG_ON, LogMaster.RULES_DEBUG),
        BUFF_DEBUG(LogMaster.BUFF_DEBUG_PREFIX, LogMaster.BUFF_DEBUG_ON, LogMaster.BUFF_DEBUG),
        LOGIC_DEBUG(LogMaster.LOGIC_DEBUG_PREFIX, LogMaster.LOGIC_DEBUG_ON, LogMaster.LOGIC_DEBUG),
        VISIBILITY_DEBUG(LogMaster.VISIBILITY_DEBUG_PREFIX, LogMaster.VISIBILITY_DEBUG_ON, LogMaster.VISIBILITY_DEBUG),
        PATHING_DEBUG(LogMaster.PATHING_DEBUG_PREFIX, LogMaster.PATHING_DEBUG_ON, LogMaster.PATHING_DEBUG),

        CORE_DEBUG_1(LogMaster.CORE_DEBUG_1_PREFIX, LogMaster.CORE_DEBUG_1_ON, LogMaster.CORE_DEBUG_1),
        CONSTRUCTION_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        AI_DEBUG2(LogMaster.AI_DEBUG_PREFIX, LogMaster.AI_DEBUG_ON2, LogMaster.AI_DEBUG2),
        AI_DEBUG(LogMaster.AI_DEBUG_PREFIX, LogMaster.AI_DEBUG_ON, LogMaster.AI_DEBUG),
        AI_TRAINING(LogMaster.AI_TRAINING_PREFIX, LogMaster.AI_TRAINING_ON,
         LogMaster.AI_TRAINING),
        MOVEMENT_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        GUI_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        ANIM_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        COMBAT_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        MAP_GENERATION_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        MATH_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        VERBOSE_CHECK(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        TRAVEL_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        WAITING_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        ATTACKING_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        VALUE_DEBUG(LogMaster.CORE_DEBUG_PREFIX, LogMaster.CORE_DEBUG_ON, LogMaster.CORE_DEBUG),
        DATA_DEBUG(LogMaster.DATA_DEBUG_PREFIX, LogMaster.DATA_DEBUG_ON, LogMaster.DATA_DEBUG),
        GAME_INFO(LOG.GAME_INFO, LogMaster.GAME_INFO_PREFIX, LogMaster.GAME_INFO_ON, LogMaster.GAME_INFO),

        MACRO_DYNAMICS(LOG.GAME_INFO, LogMaster.MACRO_DYNAMICS_PREFIX, LogMaster.MACRO_DYNAMICS_ON, LogMaster.MACRO_DYNAMICS),
        GENERATION(LOG.SYSTEM_INFO, LogMaster.GENERATION_PREFIX, LogMaster.GENERATION_ON, LogMaster.GENERATION),;
        private boolean on;
        private String prefix;
        private int code;
        private LOG log;

        LOG_CHANNEL(LOG log, String prefix, boolean on, int code) {
            this(prefix, on, code);
            this.setLog(log);
        }

        LOG_CHANNEL(String prefix, boolean on, int code) {
            this.setCode(code);
            this.setOn(on);
            this.setPrefix(prefix);
        }

        public static LOG_CHANNEL getByCode(int priority) {

            for (LOG_CHANNEL c : LOG_CHANNEL.values()) {
                if (c.getCode() == priority) {
                    return c;
                }
            }
            return null;
        }

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public LOG getLog() {
            return log;
        }

        public void setLog(LOG log) {
            this.log = log;
        }
    }

}
