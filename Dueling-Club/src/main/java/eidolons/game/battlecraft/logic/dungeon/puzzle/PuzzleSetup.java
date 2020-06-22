package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;

public abstract class PuzzleSetup<T extends Puzzle, R> extends PuzzleElement<T> {

    protected static final String SEPARATOR = "=";
    protected static final String ARGS_SEPARATOR = ",";
    protected String arg;
    protected PuzzleData data;

    public PuzzleSetup(T puzzle, PuzzleData data) {
        super(puzzle);
        this.data = data;
    }

    public void started() {
    }
    public abstract R reset();
}
