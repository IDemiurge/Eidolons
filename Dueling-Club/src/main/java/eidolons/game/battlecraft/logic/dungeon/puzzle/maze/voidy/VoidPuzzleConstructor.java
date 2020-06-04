package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.ability.conditions.puzzle.VoidCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleRules;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.elements.conditions.Condition;
import main.elements.conditions.OrConditions;
import main.elements.conditions.standard.PositionCondition;
import main.game.bf.Coordinates;

public class VoidPuzzleConstructor extends MazePuzzleConstructor {

    public VoidPuzzleConstructor(String... args) {
        super(args);
    }

    @Override
    protected PuzzleResolution createResolutions(PuzzleData puzzleData) {
        return super.createResolutions(puzzleData);
    }

    @Override
    public MazePuzzle create(String data, String blockData,
                             Coordinates coordinates, LevelBlock block) {
        MazePuzzle mazePuzzle = super.create(data, blockData, coordinates, block);
        return mazePuzzle;
    }

    @Override
    protected boolean isAreaExit() {
        return false;
    }

    @Override
    protected PuzzleResolution createResolution() {
        PuzzleResolution resolution = new PuzzleResolution(puzzle) {
            @Override
            protected Condition getFailConditions() {
                OrConditions conditions = new OrConditions();
                for (Coordinates falseExit : VoidPuzzleConstructor.this.
                        getPuzzle().getFalseExits()) {
                    conditions.add(new PositionCondition(puzzle.getAbsoluteCoordinate(falseExit)));
                }
                conditions.add(new VoidCondition());

                return conditions;
            }

            @Override
            protected Condition getSolveConditions() {
                return super.getSolveConditions();
            }
        };
        return resolution;
    }

    @Override
    protected PuzzleRules createRules(PuzzleData puzzleData) {
        return super.createRules(puzzleData);
    }

    private VoidPuzzle getPuzzle() {
        return (VoidPuzzle) puzzle;
    }

    @Override
    protected MazePuzzle createPuzzle() {
            return new VoidPuzzle();
    }

}
