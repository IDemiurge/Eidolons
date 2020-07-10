package eidolons.game.battlecraft.logic.dungeon.puzzle.encounter;

import eidolons.ability.conditions.shortcut.MainHeroCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleRules;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import main.elements.conditions.standard.EmptyCondition;
import main.game.logic.event.Event;

public class EncPuzzleConstructor extends PuzzleConstructor<EncounterPuzzle> {
    public EncPuzzleConstructor(String... mutatorArgs) {
        super(mutatorArgs);
    }

    @Override
    protected PuzzleEnums.PUZZLE_SOLUTION getSolution() {
        return null;
    }

    @Override
    protected EncounterPuzzle createPuzzle() {
        return new EncounterPuzzle();
    }

    @Override
    protected PuzzleRules createRules(PuzzleData puzzleData) {
        return new PuzzleRules(puzzle, PuzzleEnums.PUZZLE_RULE_ACTION.CUSTOM, PuzzleEnums.PUZZLE_ACTION_BASE.ROUND);
    }

    @Override
    protected void initExitTrigger() {
        if (puzzle.isEscapeAllowed())
            super.initExitTrigger();
    }

    @Override
    protected PuzzleResolution createResolutions(PuzzleData puzzleData) {

        createTriggerGlobal(PuzzleTrigger.PUZZLE_TRIGGER.PUNISH, new MainHeroCondition() ,
                ()-> puzzle.failed(), Event.STANDARD_EVENT_TYPE.UNIT_IS_FALLING_UNCONSCIOUS);

        createTriggerGlobal(PuzzleTrigger.PUZZLE_TRIGGER.SOLVE, new EmptyCondition() ,
                ()-> puzzle.complete(), Event.STANDARD_EVENT_TYPE.COMBAT_ENDS);

        PuzzleResolution resolutions = super.createResolutions(puzzleData);
        resolutions.setSolution(PuzzleEnums.PUZZLE_SOLUTION.KILL_ALL);
        return resolutions;
    }
}
