package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;

public class ArtPuzzleHandler extends PuzzleHandler<ArtPuzzle> {
    public ArtPuzzleHandler(ArtPuzzle puzzle) {
        super(puzzle);
    }

    @Override
    protected void entered() {
        super.entered();
        //glimpse?
        //what visual effect would we do for PASSIVE?
        //
    }

    @Override
    public void afterEndTip() {
        super.afterEndTip();
    }

    @Override
    public void win() {
        super.win();
    }

    @Override
    public void failed() {
        super.failed();
    }

    @Override
    public void ended() {
        super.ended();
        //cleanup
    }

    @Override
    protected PuzzleSetup<ArtPuzzle, ?> createSetup() {
        return new ArtSetup(puzzle, puzzle.getData());
    }

    @Override
    protected void playerActionDone(DC_ActiveObj action) {

    }
}
