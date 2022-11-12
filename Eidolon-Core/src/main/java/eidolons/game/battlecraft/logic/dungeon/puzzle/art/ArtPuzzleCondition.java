package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.GridCell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import main.entity.Ref;

public class ArtPuzzleCondition extends DC_Condition {
    Puzzle puzzle;
    boolean fail;


    public ArtPuzzleCondition(Puzzle puzzle, boolean fail) {
        this.puzzle = puzzle;
        this.fail = fail;
    }

    @Override
    public boolean check(Ref ref) {

        for (int i = 0; i < puzzle.getWidth(); i++) {
            for (int j = 0; j < puzzle.getHeight(); j++) {
                GridCell cell = getGame().getCell(puzzle.getAbsoluteCoordinate(i, j));
                if (cell.getOverlayRotation()%360 != 0) {
                    return false;
                }
            }

        }


        return true;
    }
}
