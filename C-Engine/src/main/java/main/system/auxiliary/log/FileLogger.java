package main.system.auxiliary.log;

import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 11/26/2017.
 */
public interface FileLogger {

    void appendSpecialLog(SPECIAL_LOG log, String string);


    void writeLog(SPECIAL_LOG log);

    public enum SPECIAL_LOG {
        AI(LOG_CHANNEL.AI_DEBUG, LOG_CHANNEL.AI_DEBUG2),
        VISIBILITY,
        COMBAT(LOG_CHANNEL.GAME_INFO),
        INPUT, MAIN(), EXCEPTIONS, ;

        private List<LOG_CHANNEL> channels;

        SPECIAL_LOG(LOG_CHANNEL... channels) {
            this.channels = Arrays.asList(channels);
        }

         public List<LOG_CHANNEL> getChannels() {
            return channels;
        }
    }
}
