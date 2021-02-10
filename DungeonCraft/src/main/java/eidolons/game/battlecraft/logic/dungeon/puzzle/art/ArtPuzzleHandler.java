package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;

public class ArtPuzzleHandler extends PuzzleHandler<ArtPuzzle> {
    public ArtPuzzleHandler(ArtPuzzle puzzle) {
        super(puzzle);
    }

    @Override
    protected PuzzleSetup<ArtPuzzle, ?> createSetup() {
        return new ArtSetup(puzzle, puzzle.getData());
    }

}
