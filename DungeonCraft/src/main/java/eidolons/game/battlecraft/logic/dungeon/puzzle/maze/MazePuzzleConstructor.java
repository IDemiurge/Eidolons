package eidolons.game.battlecraft.logic.dungeon.puzzle.maze;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.sub.MazeMarkCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.module.dungeoncrawl.struct.LevelBlock;
import main.elements.conditions.Condition;
import main.game.bf.Coordinates;

import java.util.Map;

public class MazePuzzleConstructor extends PuzzleConstructor<MazePuzzle> {

    public MazePuzzleConstructor(String... args) {
        super(args);
    }

    public MazePuzzle create(String data, Map<Coordinates, CellScriptData> blockData, Coordinates coordinates, LevelBlock block) {
        puzzle = super.create(data, blockData, coordinates, block);
        return puzzle;
    }

    @Override
    protected MazePuzzle createPuzzle() {
        return new MazePuzzle(MazePuzzle.MazeType.SKULL, MazePuzzle.MazeType.NONE);
    }

    protected boolean isReplayable() {
        return true;
    }
    @Override
    protected PuzzleResolution createResolution() {
        // DIRECTION d= DIRECTION.LEFT;// TODO
        // Coordinates back = puzzle.getEntranceCoordinates().getAdjacentCoordinate(d);
        // resolution.addPunishment(PuzzleResolution.PUZZLE_PUNISHMENT.teleport, back.toString());
        return new PuzzleResolution(puzzle) {
            @Override
            protected Condition getFailConditions() {
                return new MazeMarkCondition((MazePuzzle) puzzle);
            }
        };
    }

    @Override
    protected PuzzleEnums.PUZZLE_SOLUTION getSolution() {
        return PuzzleEnums.PUZZLE_SOLUTION.GET_TO_EXIT;
    }

    @Override
    protected PuzzleData.PUZZLE_VALUE[] getRelevantValues() {
        return new PuzzleData.PUZZLE_VALUE[]{
                PuzzleData.PUZZLE_VALUE.WIDTH,
                PuzzleData.PUZZLE_VALUE.HEIGHT,
                PuzzleData.PUZZLE_VALUE.ENTRANCE,
                PuzzleData.PUZZLE_VALUE.EXIT,
                PuzzleData.PUZZLE_VALUE.PUNISHMENT,
                PuzzleData.PUZZLE_VALUE.RESOLUTION,
                PuzzleData.PUZZLE_VALUE.TIP_INTRO,

        };
    }

    @Override
    protected boolean isAreaExit() {
        return true;
    }

    @Override
    protected boolean isPointExit() {
        return true;
    }


    @Override
    protected Condition getPuzzleEnterConditions() {
//        if (puzzle.isPale()) { TODO just need entrance range of 1 maybe
            return super.getPuzzleEnterConditions();
//        }
//        return new AreaCondition(puzzle.getCoordinates(), puzzle.getWidth(), puzzle.getHeight());
    }


}
