package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.ability.conditions.puzzle.VoidCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzleConstructor;
import main.elements.conditions.Condition;

public class VoidPuzzleConstructor extends MazePuzzleConstructor {
    public VoidPuzzleConstructor(String... args) {
        super(args);
    }

    @Override
    protected PuzzleResolution createResolution() {
        PuzzleResolution resolution = new PuzzleResolution(puzzle) {
            @Override
            protected Condition getFailConditions() {
                return new VoidCondition(  );
            }
        };
        return resolution;
    }

    @Override
    protected MazePuzzle createPuzzle() {
            return new VoidPuzzle();
    }
}
