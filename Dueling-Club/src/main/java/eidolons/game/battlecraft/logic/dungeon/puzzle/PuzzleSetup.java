package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;

public class PuzzleSetup extends PuzzleElement {

    protected static final String SEPARATOR = "=";
    protected static final String ARGS_SEPARATOR = ",";
    protected String arg;
    protected PuzzleData data;

    public PuzzleSetup(Puzzle puzzle, PuzzleData data) {
        super(puzzle);
        this.data = data;
    }

    public void started() {
    }
}
