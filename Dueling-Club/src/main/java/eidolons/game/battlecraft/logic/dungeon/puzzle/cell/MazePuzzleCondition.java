package eidolons.game.battlecraft.logic.dungeon.puzzle.cell;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
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
