package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleRules;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.module.dungeoncrawl.struct.LevelBlock;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

import java.util.Map;

public abstract class ManipulatorPuzzleConstructor<T extends ManipulatorPuzzle> extends PuzzleConstructor<T> {

    public ManipulatorPuzzleConstructor(String[] args) {
        super(args);
    }

    @Override
    public T create(String data, Map<Coordinates, CellScriptData> blockData, Coordinates coordinates, LevelBlock block) {
        T puzzle = super.create(data, blockData, coordinates, block);
        setupPuzzle(puzzle, blockData);
        return puzzle;
    }

    protected PuzzleRules createRules(PuzzleData puzzleData) {
        PuzzleEnums.PUZZLE_ACTION_BASE base = puzzleData.getCounterActionBase();
        int n =getBaseCounters(base);
        if (isCountDown()) {
            n= Math.round(n / puzzle.getDifficultyCoef());
        } else {
            n= Math.round(n * puzzle.getDifficultyCoef());
        }
        puzzleData.setValue(PuzzleData.PUZZLE_VALUE.COUNTERS_MAX, n);
        if (!StringMaster.isEmpty(puzzle.getData().getValue(PuzzleData.PUZZLE_VALUE.COUNTER_TYPE))) {
            //TODO
        }
        return new PuzzleRules(puzzle, PuzzleEnums.PUZZLE_RULE_ACTION.COUNT_DOWN, base);
    }

    protected boolean isCountDown() {
        return true;
    }

    protected abstract int getBaseCounters(PuzzleEnums.PUZZLE_ACTION_BASE base);

    protected Puzzle setupPuzzle(Puzzle puzzle, Map<Coordinates, CellScriptData> setupData) {
            // if (data.contains("manip(")) {
            //     initManipulator(puzzle, Coordinates.get(VariableManager.removeVarPart(data)),
            //             VariableManager.getVarPart(data));
            // }
        /**
         * we could skip this for now and do direct init!
         *
         * maybe just bind via shortcuts, to be generalized later
         */
        return puzzle;
    }
}
