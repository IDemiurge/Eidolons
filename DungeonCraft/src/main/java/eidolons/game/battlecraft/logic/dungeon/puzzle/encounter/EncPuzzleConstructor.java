package eidolons.game.battlecraft.logic.dungeon.puzzle.encounter;

import eidolons.ability.conditions.shortcut.MainHeroCondition;
import eidolons.game.battlecraft.ai.explore.AggroMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleConstructor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleRules;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger.PUZZLE_TRIGGER;
import main.elements.conditions.Condition;
import main.elements.conditions.standard.CustomCondition;
import main.elements.conditions.standard.EmptyCondition;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

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
        return new PuzzleRules(puzzle, PuzzleEnums.PUZZLE_RULE_ACTION.CUSTOM,
                PuzzleEnums.PUZZLE_ACTION_BASE.ROUND) {
            @Override
            protected Condition getActionChecks() {
                return new CustomCondition() {
                    @Override
                    public boolean check(Ref ref) {
                        return ref.getGame().getState().getRound() > 0;
                    }
                };
            }
        };
    }

    @Override
    protected void initExitTrigger() {
        if (puzzle.isEscapeAllowed())
            super.initExitTrigger();
    }

    @Override
    protected PuzzleResolution createResolution() {
        return new PuzzleResolution(puzzle) {

            @Override
            protected Condition getSolveConditions() {
                return new EmptyCondition();
            }

            @Override
            public PuzzleEnums.PUZZLE_SOLUTION getSolution() {
                return PuzzleEnums.PUZZLE_SOLUTION.END_COMBAT;
            }

            @Override
            protected Event.EVENT_TYPE getSolveEvent() {
                return STANDARD_EVENT_TYPE.COMBAT_ENDS;
            }
        };
    }

    @Override
    protected PuzzleResolution createResolutions(PuzzleData puzzleData) {
        puzzle.getCustomTriggers().add(
                createTrigger(PUZZLE_TRIGGER.PUNISH, new MainHeroCondition(),
                        () -> puzzle.failed(), STANDARD_EVENT_TYPE.UNIT_IS_FALLING_UNCONSCIOUS));
        puzzle.getCustomTriggers().add(createTrigger(PUZZLE_TRIGGER.SOLVE, new CustomCondition() {
                    @Override
                    public boolean check(Ref ref) {
                        return AggroMaster.getAggroGroup().isEmpty();
                    }
                },
                () -> puzzle.complete(), STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED));

        PuzzleResolution resolutions = super.createResolutions(puzzleData);
        resolutions.setSolution(PuzzleEnums.PUZZLE_SOLUTION.KILL_ALL);
        return resolutions;
    }
}
