package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.dungeon.puzzle.construction.PuzzleActions;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleTrigger;
import eidolons.system.ConditionsUtils;
import main.data.XLinkedMap;
import main.elements.conditions.Condition;
import main.game.logic.event.Event;
import main.system.entity.ConditionMaster;

import java.util.Map;

public class PuzzleResolution extends PuzzleElement {

    public void addPunishment(PUZZLE_PUNISHMENT punishment, String s) {
        punishments.put(punishment, s);
    }
    public void addResolutions(PUZZLE_RESOLUTION resolution, String s) {
        resolutions.put(resolution, s);
    }

    public enum PUZZLE_RESOLUTION {
        remove_wall,
        unseal_door,
        teleport,
        tip,
    }

    public enum PUZZLE_PUNISHMENT {
        battle,
        spell,
        teleport,
        death,
        ANIMATE_ENEMIES,
        tip,
        ;
    }

    Map<PUZZLE_PUNISHMENT, String> punishments;
    Map<PUZZLE_RESOLUTION, String> resolutions;

    PuzzleMaster.PUZZLE_SOLUTION solution;

    public PuzzleResolution(Puzzle puzzle) {
        super(puzzle);
        punishments = new XLinkedMap<>();
        resolutions = new XLinkedMap<>();
    }

    public PuzzleMaster.PUZZLE_SOLUTION getSolution() {
        return solution;
    }

    public void setSolution(PuzzleMaster.PUZZLE_SOLUTION solution) {
        this.solution = solution;
    }

        protected Runnable createPunishAction() {
        //battle?
        return () -> {
            for (PUZZLE_PUNISHMENT punishment : punishments.keySet()) {
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
        Condition checks = getPunishConditions();
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
                        ConditionsUtils.fromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAINHERO),
                        ConditionsUtils.forPuzzleSolution(solution, puzzle));
    }

    protected Runnable createSolveAction() {
//        createPunishAction();
//        createWinAction();
//        createRewardAction();
        return () -> {
            for (PUZZLE_RESOLUTION resolution : resolutions.keySet()) {
              PuzzleActions.resolution(resolution, puzzle, resolutions.get(resolution));
            }
            puzzle.finished();
        };
    }

    protected Event.EVENT_TYPE getPunishEvent() {
//TODO
        return Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING;
    }

    protected Condition getPunishConditions() {
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
