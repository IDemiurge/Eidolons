package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;

public class PuzzleElement {
    protected Puzzle puzzle;

    public PuzzleElement(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public void started() {
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }
}
