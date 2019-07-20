package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

public class PuzzleData extends DataUnit<PuzzleData.PUZZLE_VALUE> {

    public PuzzleMaster.PUZZLE_ACTION_BASE getCounterActionBase() {
        PuzzleMaster.PUZZLE_ACTION_BASE base = new EnumMaster<PuzzleMaster.PUZZLE_ACTION_BASE>().
                retrieveEnumConst(PuzzleMaster.PUZZLE_ACTION_BASE.class, getValue(PUZZLE_VALUE.COUNTER_TYPE));
        if (base == null) {
            base = PuzzleMaster.PUZZLE_ACTION_BASE.FACING;
        }
        return base;
    }

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
        VFX_PRESET, DIFFICULTY_COEF, COUNTER_TYPE, COUNTERS_MAX, COUNTER_DESCRIPTION, SOULFORCE_REWARD, TIP_FAIL,


    }
}
