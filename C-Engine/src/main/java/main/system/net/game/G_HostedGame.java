package main.system.net.game;

import main.system.net.data.DataUnit;
import main.system.net.data.DataUnit.GAME_VALUES;
import main.system.net.user.User;

import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashSet;

public class G_HostedGame extends DataUnit<GAME_VALUES> {
    public static final String[] gameRelevantValues = {
            GAME_VALUES.TITLE.name(), GAME_VALUES.HOST_NAME.name(),
            GAME_VALUES.HOST_IP.name()};

    protected boolean started;

    protected boolean host;
    protected String string;
    protected boolean joined;
    ServerSocket hostSocket;

    public G_HostedGame(String string, boolean started, boolean host) {
        this(string, started);
        this.setHost(host);

        // new game connector => new game host => new game lobby; not here
        // though!
    }

    public G_HostedGame(String string, boolean started) {
        super(string);
        this.started = started;
        this.string = string;

        // String[] array = string.split("-");
        // try {
        // title = array[0];
        // hostName = array[1];
        // hostIP = array[2];
        // } catch (ArrayIndexOutOfBoundsException e) {
        // ServerConnector.send(CODES.ERROR);
        // ServerConnector.getViewer().info(
        // "failed to init game! - " + string);
        // }
    }

    public G_HostedGame(String input, User user) {
        setValue(GAME_VALUES.TITLE.name(), input);
        setValue(GAME_VALUES.HOST_NAME.name(), user.getName());
        setValue(GAME_VALUES.HOST_IP.name(), user.getIP());
    }

    public String getRelevantData() {
        if (relevantValues == null)
            relevantValues = gameRelevantValues;
        return getData(new HashSet<String>(Arrays.asList(relevantValues)));

    }

    @Override
    public String toString() {
        return string;
    }

    public String getHostName() {
        return getValues().get(GAME_VALUES.HOST_NAME.name());
    }

    public String getHostIP() {
        return getValues().get(GAME_VALUES.HOST_IP.name());
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    protected boolean isFull() {

        return false;
    }

    public String getTitle() {
        return getValues().get(GAME_VALUES.TITLE.name());
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

}
