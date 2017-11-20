package main.system.text;

import com.badlogic.gdx.utils.StringBuilder;
import main.data.filesys.PathFinder;
import main.game.core.game.DC_Game;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;
import main.system.threading.WaitMaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 11/19/2017.
 */
public class SpecialLogger {
    private static final int WRITE_ALL_PERIOD = 5000;
    private static SpecialLogger instance;
    Map<LOG_CHANNEL, StringBuilder> channelMap = new HashMap<>();
    private StringBuilder combatActionLogBuilder = new StringBuilder();
    private StringBuilder aiLogBuilder = new StringBuilder();
    private StringBuilder visibilityLogBuilder = new StringBuilder();
    private StringBuilder inputLogBuilder = new StringBuilder();
    private String timeStamp;
    private Thread thread;

    private SpecialLogger() {

    }

    private static boolean isOn(SPECIAL_LOG log) {
        switch (log) {

        }
        return false;
    }

    public static SpecialLogger getInstance() {
        if (instance == null) {
            instance = new SpecialLogger();
        }
        return instance;
    }

    public static void setInstance(SpecialLogger instance) {
        SpecialLogger.instance = instance;
    }

    public void dialog() {
        LOG_CHANNEL channel = new EnumMaster<LOG_CHANNEL>().selectEnum(LOG_CHANNEL.class
        );
        boolean once = DialogMaster.confirm("Write once or continuously?");
    }

    public void startWritingChannel(LOG_CHANNEL channel) {
        channelMap.put(channel, new StringBuilder());
    }

    public void startWritingThread() {
        
        timeStamp = TimeMaster.getFormattedDate(true) +
         " " +
         TimeMaster.getFormattedTime(false, true);
        thread = new Thread(() -> {
            WaitMaster.WAIT(WRITE_ALL_PERIOD);
            writeLogs();
        }, "log writing thread");
        thread.start();
    }

    private void writeLogs() {
        for (SPECIAL_LOG sub : SPECIAL_LOG.values()) {
            if (isOn(sub))
                writeLog(sub);
        }
    }

    private boolean isGameLogged(SPECIAL_LOG log) {
        switch (log) {

        }
        return false;
    }

    private boolean isConsoleLogged(SPECIAL_LOG log) {
        switch (log) {

        }
        return false;
    }

    public void checkAppendToSpecialLog(LOG_CHANNEL channel, String text) {
        for (SPECIAL_LOG sub : SPECIAL_LOG.values()) {
            if (sub.getChannels().contains(channel)) {
                appendSpecialLog(sub, text);
                break;
            }
        }
        StringBuilder builder = channelMap.get(channel);
        if (builder!=null )
        {
            String string = builder.toString();
            writeLog(channel.name(), builder, string,
             true, false);

        }
    }


    public void appendSpecialLog(SPECIAL_LOG log, String string) {
        getBuilder(log).append(string + "\n");
    }


    public void writeLog(SPECIAL_LOG log) {
        Object builder = getBuilder(log);
        String string = builder.toString();
        writeLog(log.name(), builder, string,  isConsoleLogged(log), isGameLogged(log ));
    }
        public void writeLog(String logName,
                             Object builder, String string,
                             boolean consoleLogged,
                             boolean gameLogged) {
        if (consoleLogged)
            if (gameLogged )
                DC_Game.game.getLogManager().log(string);
            else
                LogMaster.log(string);

        FileManager.write(string,
         PathFinder.getLogPath() + logName + StringMaster.getPathSeparator() +
          logName +
          " log from" +
          timeStamp +
          ".txt");

    }


    private StringBuilder getBuilder(SPECIAL_LOG log) {
        switch (log) {
            case AI:
                return getAiLogBuilder();
            case VISIBILITY:
                return getVisibilityLogBuilder();
            case COMBAT:
                return getCombatActionLogBuilder();
            case INPUT:
                return getInputLogBuilder();
        }
        return null;
    }

    public void combatEndLog(String string) {
        getCombatActionLogBuilder().append(string + "\n");
    }

    public void combatStartLog(String string) {
        getCombatActionLogBuilder().append("\n" + string);
    }


    public void combatActionLog(String string) {
        getCombatActionLogBuilder().append(string + "\n");
    }

    public StringBuilder getAiLogBuilder() {
        return aiLogBuilder;
    }


    public StringBuilder getVisibilityLogBuilder() {
        return visibilityLogBuilder;
    }

    public StringBuilder getInputLogBuilder() {
        return inputLogBuilder;
    }

    public StringBuilder getCombatActionLogBuilder() {
        if (combatActionLogBuilder == null) {
            combatActionLogBuilder = new StringBuilder();
        }
        return combatActionLogBuilder;
    }

    public void logCombatLog() {
        writeLog(SPECIAL_LOG.COMBAT);
    }

    public enum SPECIAL_LOG {
        AI(LOG_CHANNEL.AI_DEBUG, LOG_CHANNEL.AI_DEBUG2),
        VISIBILITY,
        COMBAT(LOG_CHANNEL.GAME_INFO),
        INPUT,;

        private List<LOG_CHANNEL> channels;

        SPECIAL_LOG(LOG_CHANNEL... channels) {
            this.channels = Arrays.asList(channels);
        }

        private List<LOG_CHANNEL> getChannels() {
            return channels;
        }
    }

}
