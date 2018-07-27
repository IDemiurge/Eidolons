package main.system.auxiliary.log;

import com.badlogic.gdx.utils.StringBuilder;
import main.data.filesys.PathFinder;
import main.game.core.game.Game;
import main.swing.generic.services.DialogMaster;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.threading.WaitMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 11/19/2017.
 */
public class SpecialLogger implements FileLogger {
    private static final int WRITE_ALL_PERIOD = 5000;
    private static SpecialLogger instance;
    Map<LOG_CHANNEL, StringBuilder> channelMap = new HashMap<>();
    //    private StringBuilder combatActionLogBuilder = new StringBuilder();
//    private StringBuilder aiLogBuilder = new StringBuilder();
//    private StringBuilder visibilityLogBuilder = new StringBuilder();
//    private StringBuilder inputLogBuilder = new StringBuilder();
    private String timeStamp;
    private Thread thread;
    private Map<SPECIAL_LOG, StringBuilder> builderMap = new HashMap<>();

    private SpecialLogger() {

    }

    private static boolean isOn(SPECIAL_LOG log) {
        switch (log) {

        }
        return true;
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

    public void appendExceptionToFileLog(String message) {
        appendSpecialLog(SPECIAL_LOG.EXCEPTIONS, message);
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

        timeStamp = TimeMaster.getTimeStamp();
        thread = new Thread(() -> {
            WaitMaster.WAIT(WRITE_ALL_PERIOD);
            writeLogs();
        }, "log writing thread");
        thread.start();
    }

    public void writeLogs() {
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
        if (builder != null) {
            String string = builder.toString();
            writeLog(channel.name(), builder, string,
             true, false);

        }
    }


    public void appendSpecialLog(SPECIAL_LOG log, String string) {
        string = TimeMaster.getFormattedTime(true) + ": " + string;
        getBuilder(log).append(string + "\n");
        main.system.auxiliary.log.LogMaster.log(1,log + ": " + string );
    }


    public void writeLog(SPECIAL_LOG log) {
        Object builder = getBuilder(log);
        String string = builder.toString();
        writeLog(log.name(), builder, string, isConsoleLogged(log), isGameLogged(log));
    }

    public void writeLog(String logName,
                         Object builder, String string,
                         boolean consoleLogged,
                         boolean gameLogged) {
        if (string == null)
            return;
        if (consoleLogged)
            if (gameLogged)
                Game.game.getLogManager().log(string);
            else
                LogMaster.log(string);

        FileManager.write(string,
         PathFinder.getLogPath() + logName + PathUtils.getPathSeparator() +
          logName +
          " log from" +
          getTimeStamp() +
          ".txt");

    }

    public String getTimeStamp() {
        if (timeStamp == null) {
            timeStamp = TimeMaster.getTimeStamp();
        }
        return timeStamp;
    }

    private StringBuilder getBuilder(SPECIAL_LOG log) {
        StringBuilder builder = builderMap.get(log);
        if (builder == null) {
            builder = new StringBuilder();
            builderMap.put(log, builder);
        }

        return builder;
    }

    public void logCombatLog() {
        writeLog(SPECIAL_LOG.COMBAT);
    }


}
