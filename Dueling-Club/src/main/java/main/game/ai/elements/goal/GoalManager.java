package main.game.ai.elements.goal;

import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.game.ai.tools.Analyzer;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.game.ai.tools.target.AI_SpellMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GoalManager {

    public static List<GOAL_TYPE> getGoalsForUnit(UnitAI ai) {
        Unit unit = ai.getUnit();
        List<GOAL_TYPE> list = getBehaviorGoals(unit);
        if (list != null) {
            return list;
        }

        list = new LinkedList<>();
        if (unit.getAiType() == AiEnums.AI_TYPE.SNEAK) {
            list.add(GOAL_TYPE.STEALTH);
        }
        if (Analyzer.getVisibleEnemies(unit.getUnitAI()).isEmpty()) {
            list = new ListMaster<GOAL_TYPE>().getList(GOAL_TYPE.SEARCH);
            addNonEnemyGoals(list);
            return list;
        }
        if (unit.checkAiMod(AI_MODIFIERS.TRUE_BRUTE)) {
            return new ListMaster<GOAL_TYPE>().getList(GOAL_TYPE.ATTACK);
        }
        list.addAll(getDefaultGoals());

        if (unit.getAiType() == AiEnums.AI_TYPE.CASTER || unit.getAiType() == AiEnums.AI_TYPE.ARCHER) {
            if (!unit.checkPassive(UnitEnums.STANDARD_PASSIVES.FEARLESS)) {
                if (DC_PriorityManager.getMeleeDangerFactor(unit) > 0) {
                    list.add(GOAL_TYPE.RETREAT);
                }
            }
        }

        if (Analyzer.hasSpecialActions(unit)) {
            addNonEnemyGoals(list);

            addEnemyGoals(list);
        }
        // SEARCH
        // FLEE
        return list;
    }

    public static List<GOAL_TYPE> getDefaultGoals() {
        return new LinkedList<>(Arrays.asList(new GOAL_TYPE[]{GOAL_TYPE.PREPARE,
                GOAL_TYPE.DEFEND, GOAL_TYPE.ATTACK, GOAL_TYPE.WAIT,}));
    }

    private static void addEnemyGoals(List<GOAL_TYPE> list) {
        list.add(GOAL_TYPE.DEBUFF);
        list.add(GOAL_TYPE.CUSTOM_HOSTILE);
        list.add(GOAL_TYPE.DEBILITATE);
    }

    private static void addNonEnemyGoals(List<GOAL_TYPE> list) {
        // list.add(GOAL_TYPE.APPROACH);
        list.add(GOAL_TYPE.BUFF);
        list.add(GOAL_TYPE.ZONE_SPECIAL);
        list.add(GOAL_TYPE.ZONE_DAMAGE);
        list.add(GOAL_TYPE.SELF);
        list.add(GOAL_TYPE.SUMMONING);
        list.add(GOAL_TYPE.AUTO_BUFF);
        list.add(GOAL_TYPE.AUTO_DAMAGE);
        list.add(GOAL_TYPE.AUTO_DEBUFF);
        list.add(GOAL_TYPE.RESTORE);
        list.add(GOAL_TYPE.CUSTOM_SUPPORT);
        list.add(GOAL_TYPE.COATING);
    }

    private static List<GOAL_TYPE> getBehaviorGoals(Unit unit) {
        BEHAVIOR_MODE behaviorMode = unit.getUnitAI().getBehaviorMode();
        if (behaviorMode == null) {
            return null;
        }
        switch (behaviorMode) {
            case BERSERK:
                return new ListMaster<GOAL_TYPE>().getList(GOAL_TYPE.ATTACK);
            case CONFUSED:
                List<GOAL_TYPE> list = new ListMaster<GOAL_TYPE>().getList(GOAL_TYPE.WANDER,
                        GOAL_TYPE.MOVE, GOAL_TYPE.ATTACK, GOAL_TYPE.RETREAT);
                return new ListMaster<GOAL_TYPE>().getList(new RandomWizard<GOAL_TYPE>()
                        .getRandomListItem(list));

            case PANIC:
                return new ListMaster<GOAL_TYPE>().getList(GOAL_TYPE.RETREAT, GOAL_TYPE.COWER);
            default:
                break;
        }
        return null;
    }


    public static GOAL_TYPE getGoalFromAction(DC_ActiveObj a) {
        switch (a.getActionGroup()) {
            case ATTACK:
                return GOAL_TYPE.ATTACK;
            case HIDDEN:
                break;
            case ITEM:
                break;
            case MODE:
                return GOAL_TYPE.PREPARE;
            case MOVE:
                return GOAL_TYPE.RETREAT;
            case SPECIAL:
                break;
            case SPELL:
                break;
            case TURN:
                break;
            default:
                break;

        }
        GOAL_TYPE goal = AI_SpellMaster.getGoal(a);

        return goal;
    }

}
