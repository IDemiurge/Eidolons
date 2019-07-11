package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import main.entity.Ref;
import main.game.bf.Coordinates;

public class PuzzleCondition extends DC_Condition {
    Puzzle puzzle;
    boolean fail;


    public PuzzleCondition(Puzzle puzzle, boolean fail) {
        this.puzzle = puzzle;
        this.fail = fail;
    }

    @Override
    public boolean check(Ref ref) {

        for (int i = 0; i < puzzle.getWidth(); i++) {
            for (int j = 0; j < puzzle.getHeight(); j++) {
                DC_Cell cell = getGame().getCellByCoordinate(Coordinates.get(i, j).getOffset(puzzle.getCoordinates()));
                if (cell.getOverlayRotation() != 0) {
                    return false;
                }
            }

        }


        return true;
    }
}
