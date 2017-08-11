package main.game.battlecraft.logic.battle.universal;

import main.game.battlecraft.logic.battle.universal.BattleOptions.ARENA_GAME_OPTIONS;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

public class BattleOptions extends DataUnit<ARENA_GAME_OPTIONS> {
    private int battleLevel;

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

    public int getBattleLevel() {
        return battleLevel;
    }

    public void setBattleLevel(int battleLevel) {
        this.battleLevel = battleLevel;
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
        NEOPHYTE(100, 175, 75, 200, 250),
        NOVICE(125, 150, 85, 150, 200),
        DISCIPLE(150, 125, 100, 125, 150),
        ADEPT(200, 100, 125, 100, 100),
        CHAMPION(300, 75, 150, 75, 80),
        AVATAR(450, 50, 200, 60, 65);
        private int powerPercentage;
        private int roundsToFightMod;
        private int healthPercentageEnemy;
        private int healthPercentageAlly;
        private int healthPercentageMainHero;

        DIFFICULTY(int powerPercentage, int roundsToFightMod, int healthPercentageEnemy, int healthPercentageAlly, int healthPercentageMainHero) {
            this.powerPercentage = powerPercentage;
            this.roundsToFightMod = roundsToFightMod;
            this.healthPercentageEnemy = healthPercentageEnemy;
            this.healthPercentageAlly = healthPercentageAlly;
            this.healthPercentageMainHero = healthPercentageMainHero;
        }

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

        public int getHealthPercentageEnemy() {
            return healthPercentageEnemy;
        }



        public int getHealthPercentageAlly() {
            return healthPercentageAlly;
        }



        public int getHealthPercentageMainHero() {
            return healthPercentageMainHero;
        }


    }
}
