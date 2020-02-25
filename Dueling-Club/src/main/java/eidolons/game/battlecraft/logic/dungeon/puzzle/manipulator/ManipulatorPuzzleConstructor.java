package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import eidolons.game.battlecraft.logic.dungeon.puzzle.*;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

public abstract class ManipulatorPuzzleConstructor<T extends ManipulatorPuzzle> extends PuzzleConstructor<T> {

    public ManipulatorPuzzleConstructor(String[] args) {
        super(args);
    }

    @Override
    public T create(String data, String blockData, Coordinates coordinates, LevelBlock block) {
        T puzzle = super.create(data, blockData, coordinates, block);
        setupPuzzle(puzzle, blockData);




        return puzzle;
    }

    protected PuzzleRules createRules(PuzzleData puzzleData) {
        PuzzleMaster.PUZZLE_ACTION_BASE base = puzzleData.getCounterActionBase();
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
        return new PuzzleRules(puzzle, PuzzleRules.PUZZLE_RULE_ACTION.COUNT_DOWN, base);
    }

    protected boolean isCountDown() {
        return true;
    }

    protected abstract int getBaseCounters(PuzzleMaster.PUZZLE_ACTION_BASE base);


    protected void initManipulator(Puzzle puzzle,
                                   Coordinates c, String data) {
        GuiEventManager.trigger(GuiEventType.INIT_MANIPULATOR, new Manipulator(puzzle,
                Manipulator.Manipulator_template.rotating_cross,
                c, data));
    }

    protected Puzzle setupPuzzle(Puzzle puzzle, String setupData) {

        for (String data : ContainerUtils.openContainer(setupData)) {
            if (data.contains("manip(")) {
                initManipulator(puzzle, Coordinates.get(VariableManager.removeVarPart(data)),
                        VariableManager.getVarPart(data));
            }
        }
        /**
         * we could skip this for now and do direct init!
         *
         * maybe just bind via shortcuts, to be generalized later
         *
         */
        return puzzle;
    }
}
