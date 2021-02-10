package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

public class PuzzleEnums {
    public enum puzzle_type {
        art, maze, voidmaze, encounter,
    }

    public enum PUZZLE_ACTION_BASE {
        MOVE,
        MOVE_AFTER, ACTION, FACING,
        ROUND, ROUND_BEFORE,
    }

    public enum PUZZLE_RULE_ACTION{
        FAIL,
        WIN,
        DEATH,
        COUNT_DOWN,
        SPAWN, CUSTOM,
    }
    public enum PUZZLE_ACTION {
        ROTATE_MOSAIC_CELL_CLOCKWISE,
        ROTATE_MOSAIC_CELL_ANTICLOCKWISE,
        SPAWN_ENEMIES,
    }

    public enum PUZZLE_SOLUTION {
        GET_TO_EXIT,
        END_COMBAT,
        MOSAIC,
        SHAPE,
        PATH,

        SLOTS,

        FIND_SECRET,
        DISCOVER_PATTERN, KILL_ALL,

    }

    public enum PUZZLE_RESOLUTION {
        remove_wall,
        unseal_door,
        teleport,
        tip,
        awaken,
        open_portal
    }

    public enum PUZZLE_PUNISHMENT {
        battle,
        spell,
        teleport,
        death,
        awaken,
        tip,
        ;
    }

}
