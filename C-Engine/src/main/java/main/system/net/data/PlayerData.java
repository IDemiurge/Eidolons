package main.system.net.data;

import main.system.net.data.PlayerData.PLAYER_VALUES;

public class PlayerData extends DataUnit<PLAYER_VALUES> {

    public PlayerData(String data) {
        super(data);
    }

    public enum PLAYER_VALUES {
        NAME,
        HERO_TYPE,
        COLOR,

    }
}
