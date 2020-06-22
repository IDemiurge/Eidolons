package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import main.game.bf.Coordinates;

public class PuzzleElement<T extends Puzzle> {
    protected T puzzle;

    public PuzzleElement(T puzzle) {
        this.puzzle = puzzle;
    }

    public void started() {
    }
    public void ended() {
    }

    public T getPuzzle() {
        return puzzle;
    }

    protected PuzzleMaster getMaster() {
        return puzzle.getMaster();
    }

    public int getWidth() {
        return puzzle.getWidth();
    }

    public int getHeight() {
        return puzzle.getHeight();
    }

    public PuzzleData getData() {
        return puzzle.getData();
    }

    public boolean isActive() {
        return puzzle.isActive();
    }

    public boolean isSolved() {
        return puzzle.isSolved();
    }

    public boolean isFailed() {
        return puzzle.isFailed();
    }

    public Coordinates getCoordinates() {
        return puzzle.getCoordinates();
    }

    public PuzzleHandler getHandler() {
        return puzzle.getHandler();
    }
    public Coordinates getExitCoordinates() {
        return puzzle.getExitCoordinates();
    }

    public Coordinates getEntranceCoordinates() {
        return puzzle.getEntranceCoordinates();
    }

    public Coordinates getAbsoluteCoordinate(Coordinates wall) {
        return puzzle.getAbsoluteCoordinate(wall);
    }

    public Coordinates getAbsoluteCoordinate(int i, int j) {
        return puzzle.getAbsoluteCoordinate(i, j);
    }

    public float getDifficultyCoef() {
        return puzzle.getDifficultyCoef();
    }
}
