package eidolons.game.battlecraft.logic.dungeon.puzzle.maze;

import eidolons.ability.conditions.DC_Condition;
import main.entity.Ref;
import main.game.bf.Coordinates;

public class MazePuzzleCondition extends DC_Condition {
    MazePuzzle puzzle;

    public MazePuzzleCondition(MazePuzzle puzzle ) {
        this.puzzle = puzzle;
    }

    @Override
    public boolean check(Ref ref) {
        if (puzzle.getMazeWalls() != null)
        for (Coordinates wall : puzzle.getMazeWalls()) {
            if (ref.getSourceObj().getCoordinates().equals(puzzle.getAbsoluteCoordinate(wall))) {
                return true;
            }

        }

        return false;
    }
}
