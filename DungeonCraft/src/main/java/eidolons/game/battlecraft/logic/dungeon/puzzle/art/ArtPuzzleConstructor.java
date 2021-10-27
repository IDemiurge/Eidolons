package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.ManipulatorPuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import main.elements.conditions.Condition;
import main.game.bf.Coordinates;

import java.util.Map;

public class ArtPuzzleConstructor extends ManipulatorPuzzleConstructor<ArtPuzzle> {

    public ArtPuzzleConstructor(String[] args) {
        super(args);
    }

    @Override
    protected ArtPuzzle createPuzzle() {
        return new ArtPuzzle();
    }

    @Override
    public ArtPuzzle create(String data, Map<Coordinates, CellScriptData> blockData, Coordinates coordinates, LevelBlock block) {
        return super.create(data, blockData, coordinates, block);
    }
    @Override
    protected boolean isAreaEnter() {
        return true;
    }
    @Override
    protected boolean isAreaExit() {
        return true;
    }
    @Override
    protected int getBaseCounters(PuzzleEnums.PUZZLE_ACTION_BASE base) {
        switch (base) {
            case MOVE:
                return puzzle.getWidth()*puzzle.getHeight()*puzzle.getRotateChance()/100 ;
            case ACTION:
                return puzzle.getWidth()*puzzle.getHeight()*puzzle.getRotateChance()/100*4;
            case FACING:
              return puzzle.getWidth()*puzzle.getHeight()*puzzle.getRotateChance()/100*2;
        }
        return 0;
    }





    @Override
    protected PuzzleEnums.PUZZLE_SOLUTION getSolution() {
        return PuzzleEnums.PUZZLE_SOLUTION.MOSAIC;
    }
    @Override
    protected PuzzleData.PUZZLE_VALUE[] getRelevantValues() {
        return new PuzzleData.PUZZLE_VALUE[]{
                PuzzleData.PUZZLE_VALUE.WIDTH,
                PuzzleData.PUZZLE_VALUE.HEIGHT,
                PuzzleData.PUZZLE_VALUE.ENTRANCE,
                PuzzleData.PUZZLE_VALUE.EXIT,
                PuzzleData.PUZZLE_VALUE.COUNTER_TYPE,
                PuzzleData.PUZZLE_VALUE.RESOLUTION,
                PuzzleData.PUZZLE_VALUE.TIP_INTRO,
                PuzzleData.PUZZLE_VALUE.ARG,

        };
    }

    @Override
    protected Condition getPuzzleEnterConditions() {
        return super.getPuzzleEnterConditions();
    }

    /**
     * exit via veil as well
     *
     */
}
