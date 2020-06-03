package eidolons.game.battlecraft.logic.dungeon.puzzle.maze;

import eidolons.ability.conditions.DC_Condition;
import main.entity.Ref;
import main.game.bf.Coordinates;

public class MazePuzzleFailCondition extends DC_Condition {
    MazePuzzle puzzle;

    public MazePuzzleFailCondition(MazePuzzle puzzle ) {
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
