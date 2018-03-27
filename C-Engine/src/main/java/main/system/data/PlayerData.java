package main.system.data;


import main.system.data.PlayerData.PLAYER_VALUE;

public class PlayerData extends DataUnit<PLAYER_VALUE> {

    public static final Boolean FORMAT = false;

    public PlayerData(String data) {
        super(data);
    }

    @Override
    public Boolean getFormat() {
        return FORMAT ;
    }

    public enum PLAYER_VALUE {
        NAME, COLOR, EMBLEM, PORTRAIT, ALLEGIENCE, MAIN_HERO,
    }
    public enum ALLEGIENCE {
        PLAYER, ALLY, ENEMY, NEUTRAL, PASSIVE;

        public boolean isNeutral() {
            switch (this) {
                case PASSIVE:
                case NEUTRAL:
                    return true;
            }
            return false;
        }

        public boolean isAi() {
            return !isMe();
        }

        public boolean isMe() {
            switch (this) {
                case PLAYER:
                    return true;
            }
            return false;
        }
    }


}
