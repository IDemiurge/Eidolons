package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.sub;

import eidolons.ability.conditions.DC_Condition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import main.entity.Ref;
import main.game.bf.Coordinates;

public class MazeMarkCondition extends DC_Condition {
    MazePuzzle puzzle;

    public MazeMarkCondition(MazePuzzle puzzle ) {
        this.puzzle = puzzle;
    }

    @Override
    public boolean check(Ref ref) {
        if (puzzle.getMarkedCells() != null)
        for (Coordinates wall : puzzle.getMarkedCells()) {
            if (ref.getSourceObj().getCoordinates().equals(puzzle.getAbsoluteCoordinate(wall))) {
                return true;
            }

        }

        return false;
    }
}
