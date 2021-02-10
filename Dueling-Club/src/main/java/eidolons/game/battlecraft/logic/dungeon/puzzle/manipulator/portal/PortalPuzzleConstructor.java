package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.portal;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import main.elements.conditions.Condition;

public class PortalPuzzleConstructor extends PuzzleConstructor<PortalPuzzle> {
    @Override
    protected PuzzleEnums.PUZZLE_SOLUTION getSolution() {
        return PuzzleEnums.PUZZLE_SOLUTION.SLOTS;
    }

    @Override
    protected PuzzleResolution createResolution() {
        PuzzleResolution res = super.createResolution();
        res.addResolutions(PuzzleEnums.PUZZLE_RESOLUTION.open_portal,
                puzzleData.getValue(PuzzleData.PUZZLE_VALUE.ARG));
        return res;
    }


    @Override
    protected PuzzleData.PUZZLE_VALUE[] getRelevantValues() {
        return new PuzzleData.PUZZLE_VALUE[]{

        };
    }


    @Override
    protected Condition getPuzzleEnterConditions() {
        //on event?
        return super.getPuzzleEnterConditions();
    }

    @Override
    protected boolean isAreaEnter() {
        return true;
    }

    @Override
    protected boolean isAreaExit() {
        return false;
    }

    @Override
    protected PortalPuzzle createPuzzle() {
        return new PortalPuzzle();
    }
}
