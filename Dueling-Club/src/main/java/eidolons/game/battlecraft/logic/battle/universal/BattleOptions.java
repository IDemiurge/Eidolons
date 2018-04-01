package eidolons.game.battlecraft.logic.battle.universal;

import eidolons.game.battlecraft.logic.battle.universal.BattleOptions.ARENA_GAME_OPTIONS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DIFFICULTY;
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
        return GenericEnums.DIFFICULTY.DISCIPLE;
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

}
