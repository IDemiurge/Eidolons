package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import eidolons.system.ConditionsUtils;
import main.data.XLinkedMap;
import main.elements.conditions.Condition;
import main.game.logic.event.Event;
import main.system.entity.ConditionMaster;

import java.util.Map;

public class PuzzleResolution extends PuzzleElement {

    public void addPunishment(PuzzleEnums.PUZZLE_PUNISHMENT punishment, String s) {
        punishments.put(punishment, s);
    }
    public void addResolutions(PuzzleEnums.PUZZLE_RESOLUTION resolution, String s) {
        resolutions.put(resolution, s);
    }

    Map<PuzzleEnums.PUZZLE_PUNISHMENT, String> punishments;
    Map<PuzzleEnums.PUZZLE_RESOLUTION, String> resolutions;

    PuzzleEnums.PUZZLE_SOLUTION solution;

    public PuzzleResolution(Puzzle puzzle) {
        super(puzzle);
        punishments = new XLinkedMap<>();
        resolutions = new XLinkedMap<>();
    }

    public PuzzleEnums.PUZZLE_SOLUTION getSolution() {
        return solution;
    }

    public void setSolution(PuzzleEnums.PUZZLE_SOLUTION solution) {
        this.solution = solution;
    }

        protected Runnable createPunishAction() {
        //battle?
        return () -> {
            for (PuzzleEnums.PUZZLE_PUNISHMENT punishment : punishments.keySet()) {
                PuzzleActions.punishment(puzzle, punishment, punishments.get(punishment));
            }
        };
    }
    public void started() {
        createSolveTrigger();
        createPunishTrigger();
        createActionTriggers();
    }
    protected void createPunishTrigger() {
        Condition checks = getFailConditions();
        if (checks == null) {
            return;
        }
        Event.EVENT_TYPE event = getPunishEvent();
        Runnable action = createPunishAction();

        puzzle.createTrigger(PuzzleTrigger.PUZZLE_TRIGGER.PUNISH, event, checks, action );
    }


    protected void createSolveTrigger() {
        Event.EVENT_TYPE event = getSolveEvent();
        Condition checks = getSolveConditions();
        Runnable action = createSolveAction();

        puzzle.createTrigger(PuzzleTrigger.PUZZLE_TRIGGER.SOLVE, event, checks, action);
    }

    protected Event.EVENT_TYPE getSolveEvent() {
        return Event.STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE;
    }

    protected Condition getSolveConditions() {
        return
                ConditionsUtils.join(
                        ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAIN_HERO),
                        ConditionsUtils.forPuzzleSolution(solution, puzzle));
    }

    protected Runnable createSolveAction() {
//        createPunishAction();
//        createWinAction();
//        createRewardAction();
        return () -> {
            for (PuzzleEnums.PUZZLE_RESOLUTION resolution : resolutions.keySet()) {
              PuzzleActions.resolution(resolution, puzzle, resolutions.get(resolution));
            }
            puzzle.complete();
        };
    }

    protected Event.EVENT_TYPE getPunishEvent() {
        //TODO sometimes we should check on other events too...
        return Event.STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE;
    }

    protected Condition getFailConditions() {
       return ConditionsUtils.forPuzzlePunishment(puzzle, punishments.keySet());
    }
    
    
    protected void createActionTriggers() {

//        Event.EVENT_TYPE event = getActionEvent();
//        Condition checks = getActionConditions();
//        Runnable action = createAction();
//
//        createTrigger(event, checks, action);
    }
}
