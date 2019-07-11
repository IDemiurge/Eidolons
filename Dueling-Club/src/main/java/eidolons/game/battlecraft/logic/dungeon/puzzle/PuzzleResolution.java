package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.system.DC_ConditionMaster;
import main.data.XLinkedMap;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.system.entity.ConditionMaster;

import java.util.Map;

public class PuzzleResolution extends PuzzleElement {
    Map<Puzzle_Punishment, String> punishments;
    Map<Puzzle_Resolution, String> resolutions;

    public PuzzleResolution(Puzzle puzzle) {
        super(puzzle);
        punishments = new XLinkedMap<>();
        punishments.put(Puzzle_Punishment.death, "");

        resolutions = new XLinkedMap<>();
        resolutions.put(Puzzle_Resolution.teleport,
                "0-0"
                //puzzle.getExit()
        );
    }

    public enum Puzzle_Resolution {
        remove_wall,
        unseal_door,
        teleport,


    }

    public enum Puzzle_Punishment {
        battle,
        spell,
        teleport,
        death,
        ;
    }
    private Runnable createPunishAction() {
        //battle?
        return () -> {

        };
    }


    private void createSolveTrigger() {
        Event.EVENT_TYPE event = getSolveEvent();
        Condition checks = getSolveConditions();
        Runnable action = createSolveAction();

        puzzle.createTrigger(event, checks, action);
    }

    private Event.EVENT_TYPE getSolveEvent() {
        return Event.STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING;
    }

    private Condition getSolveConditions() {
        return
                new Conditions(
                        DC_ConditionMaster.getInstance().getConditionFromTemplate(ConditionMaster.CONDITION_TEMPLATES.MAINHERO,
                                "", ""), new PuzzleCondition(puzzle, false));
    }

    private Runnable createSolveAction() {
        createPunishAction();
//        createWinAction();
//        createRewardAction();
        return () -> {
            for (Puzzle_Resolution resolution : resolutions.keySet()) {
                applyResolution(resolution, resolutions.get(resolution));

            }
        };
    }

    private void applyResolution(Puzzle_Resolution resolution, String s) {
        switch (resolution) {
            case remove_wall:
                break;
            case unseal_door:
                break;
            case teleport:
                Eidolons.getGame().getBattleMaster().getScriptManager().execute(CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION.REPOSITION,
                        Ref.getSelfTargetingRefCopy(Eidolons.getMainHero()), s);
                break;
        }
    }


    private void createActionTriggers() {

//        Event.EVENT_TYPE event = getActionEvent();
//        Condition checks = getActionConditions();
//        Runnable action = createAction();
//
//        createTrigger(event, checks, action);
    }
}
