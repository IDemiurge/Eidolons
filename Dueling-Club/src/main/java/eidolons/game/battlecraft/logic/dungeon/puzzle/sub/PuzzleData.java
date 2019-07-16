package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import main.system.data.DataUnit;

public class PuzzleData extends DataUnit<PuzzleData.PUZZLE_VALUE> {

    public enum PUZZLE_VALUE {
        WIDTH,
        HEIGHT,

        ENTRANCE,
        EXIT,

        PUNISHMENT,
        RESOLUTION,

        TIP,
        PALE,
        ARG,
        SEAL_BEHIND,
        SEAL_ALL,
        CANNOT_RETURN,

        AMBIENCE,
        VFX_PRESET, DIFFICULTY_COEF, COUNTER_TYPE, COUNTERS_MAX,


    }
}
