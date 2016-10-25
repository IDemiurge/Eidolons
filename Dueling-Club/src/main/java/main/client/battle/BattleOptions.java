package main.client.battle;

import main.client.battle.BattleOptions.ARENA_GAME_OPTIONS;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.net.data.DataUnit;

public class BattleOptions extends DataUnit<ARENA_GAME_OPTIONS> {
    public FACING_DIRECTION getPlayerPartySide() {
        if (getValue(ARENA_GAME_OPTIONS.PLAYER_STARTING_SIDE) != null) {
            return new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(
                    FACING_DIRECTION.class,
                    getValue(ARENA_GAME_OPTIONS.PLAYER_STARTING_SIDE));
        }
        return FACING_DIRECTION.NONE;
    }

    public DIFFICULTY getDifficulty() {
        if (getValue(ARENA_GAME_OPTIONS.DIFFICULTY) != null) {
            return new EnumMaster<DIFFICULTY>().retrieveEnumConst(
                    DIFFICULTY.class, getValue(ARENA_GAME_OPTIONS.DIFFICULTY));
        }
        return DIFFICULTY.DISCIPLE;
    }

    public enum ARENA_GAME_OPTIONS {
        DIFFICULTY,
        NUMBER_OF_HEROES,
        MODE,
        BACKGROUND,
        HERO_LEVEL,
        TURNS_BETWEEN_WAVES,
        TURNS_TO_PREPARE,
        LIVES,
        PLAYER_STARTING_SIDE,
    }

    public enum DIFFICULTY {
        NEOPHYTE(100, 175),
        NOVICE(125, 150),
        DISCIPLE(150, 125),
        ADEPT(200, 100),
        CHAMPION(300, 75),
        AVATAR(450, 50);
        private int powerPercentage;
        private int roundsToFightMod;

        DIFFICULTY(int power, int roundsToFightMod) {
            this.roundsToFightMod = roundsToFightMod;
            this.setPowerPercentage(power);
        }

        public int getPowerPercentage() {
            return powerPercentage;
        }

        public void setPowerPercentage(int powerPercentage) {
            this.powerPercentage = powerPercentage;
        }

        public int getRoundsToFightMod() {
            return roundsToFightMod;
        }

    }
}
