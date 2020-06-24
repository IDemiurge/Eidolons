package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

public class PuzzleEnums {
    public enum puzzle_type {
        art,
        maze, voidmaze,

    }

    public enum PUZZLE_ACTION_BASE {
        MOVE,
        MOVE_AFTER, ACTION, FACING,


    }

    public enum PUZZLE_ACTION {
        ROTATE_MOSAIC_CELL_CLOCKWISE,
        ROTATE_MOSAIC_CELL_ANTICLOCKWISE,

    }

    public enum PUZZLE_SOLVE_MUTATOR {
        ATTACK,
        MESSAGE,

    }

    public enum PUZZLE_TIMED_MUTATOR {
        FADING_LIGHT,
        FALLING_CEILING,
        RISING_WATER,

    }

    public enum PUZZLE_RULE_MUTATOR {
        TELEPORTERS,

    }

    public enum PUZZLE_SOLUTION {
        GET_TO_EXIT,
        MOSAIC,
        SHAPE,
        PATH,

        SLOTS,

        FIND_SECRET,
        DISCOVER_PATTERN,

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
