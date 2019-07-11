package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import main.system.data.DataUnit;

public class PuzzleData extends DataUnit<PuzzleData.PUZZLE_VALUE> {

    int width;
    int height;

    public enum PUZZLE_VALUE{
        SEAL_BEHIND,
        SEAL_ALL,
        CANNOT_RETURN,

        AMBIENCE,
        VFX_PRESET,



    }
}
