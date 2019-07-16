package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.ManipulatorPuzzle;
import main.data.filesys.PathFinder;

public class ArtPuzzle extends ManipulatorPuzzle {


    @Override
    public void setup(PuzzleSetup... setups) {
//        for (PuzzleSetup setup : setups) {
//            setup.arg = getArtPiecePath();
//        }
        super.setup(setups);
    }

    public void init() {


    }
    protected int getRotateChance() {
        return 60;
    }
}
