package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import main.data.filesys.PathFinder;

public class ArtPuzzle extends Puzzle {
    private String fileName;

    /**
     * what's different?
     */
    public String getArtPiecePath() {
        return PathFinder.getArtFolder() + "puzzles/" + getName() + ".png";

    }

    public ArtPuzzle(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void setup(PuzzleSetup... setups) {
        for (PuzzleSetup setup : setups) {
            setup.arg = getArtPiecePath();
        }
        super.setup(setups);
    }

    private String getName() {
        return fileName;
    }

    public void init() {


    }

}
