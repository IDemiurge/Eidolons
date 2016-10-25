package main.system.net.user;

import main.system.net.data.DataUnit;
import main.system.net.user.User.USER_VALUES;

import java.util.Arrays;
import java.util.HashSet;

public class User extends DataUnit<USER_VALUES> {

    public static final String LOCAL_HOST = "127.0.0.1";
    public static final String HOST = "HOST_USER";
    public static final String CLIENT = "CLIENT_USER";
    static String[] relevantUserValues = {USER_VALUES.USERNAME.name(),
            // USER_VALUES.GUILD.name(),
            // USER_VALUES.RANG.name(),
            // USER_VALUES.RATING.name(),
    };
    USER_STATUS status;
    private long ping;

    public User(String text) {
        setData(text);
    }

    public User() {
    }

    // TEST MODE
    public User(boolean host) {
        setValue(USER_VALUES.USERNAME, (host) ? HOST : CLIENT);
        setValue(USER_VALUES.LAST_IP, LOCAL_HOST);

    }

    public String getRelevantData() {
        if (relevantValues == null)
            relevantValues = relevantUserValues;
        return getData(new HashSet<String>(Arrays.asList(relevantValues)));

    }

    public String getIP() {
        return getValue(USER_VALUES.LAST_IP);
    }

    public String getName() {
        return getValue(USER_VALUES.USERNAME);
    }

    public boolean isActivated() {
        String s = getValue(USER_VALUES.ACTIVATED);
        if (s == null)
            return false;
        return (s.equals("YES"));
    }

    public long getPing() {
        return ping;
    }

    // email, ip .. ??

    public void setPing(long l) {
        this.ping = l;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User user = (User) o;
            if (user.getData().equals(this.getData()))
                return true;
        }
        return false;
    }

    public enum USER_STATUS {
        ONLINE,
        OFFLINE,
        INGAME,
        PLAYING
    }

    public enum USER_VALUES {
        USERNAME,
        PASSWORD,
        PARTIES,
        EMAIL,

        ACTIVATED,
        RATING,
        LAST_IP,
        RANG,
        GUILD,
        AVATAR
    }
}
