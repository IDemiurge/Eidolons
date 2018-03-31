package eidolons.game.battlecraft.ai.elements.goal;

import eidolons.game.battlecraft.ai.tools.Analyzer;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.ai.tools.target.AI_SpellMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GoalManager extends AiHandler {
    public GoalManager(AiMaster master) {
        super(master);
    }

    public static Boolean isGoalVsEnemies(GOAL_TYPE g) {
        switch (g) {

        }
        return null;
    }

    public static List<GOAL_TYPE> getGoalsForUnit(UnitAI ai) {
        Unit unit = ai.getUnit();
        List<GOAL_TYPE> list = getBehaviorGoals(unit);
        if (list != null) {
            return list;
        }
        if (ai.getCurrentOrder() != null)
            if (ai.getCurrentOrder().getStrictPriority() != null)
                return new ArrayList<>(
                 Arrays.asList(ai.getCurrentOrder().getStrictPriority().getGoalTypes()));

        list = new ArrayList<>();
        if (unit.getAiType() == AiEnums.AI_TYPE.SNEAK) {
            list.add(AiEnums.GOAL_TYPE.STEALTH);
        }
        if (Analyzer.getVisibleEnemies(unit.getUnitAI()).isEmpty()) {
            list = new ListMaster<GOAL_TYPE>().getList(AiEnums.GOAL_TYPE.SEARCH);
            addNonEnemyGoals(list);
            return list;
        }
        if (unit.checkAiMod(AI_MODIFIERS.TRUE_BRUTE)) {
            return new ListMaster<GOAL_TYPE>().getList(AiEnums.GOAL_TYPE.ATTACK);
        }
        list.addAll(getDefaultGoals());

        if (unit.getAiType() == AiEnums.AI_TYPE.CASTER || unit.getAiType() == AiEnums.AI_TYPE.ARCHER) {
            if (!unit.checkPassive(UnitEnums.STANDARD_PASSIVES.FEARLESS)) {
                if (DC_PriorityManager.getMeleeDangerFactor(unit) > 0) {
                    list.add(AiEnums.GOAL_TYPE.RETREAT);
                }
            }
        }

        if (Analyzer.hasAnySpecialActions(unit)) {
            addNonEnemyGoals(list);

            addEnemyGoals(list);
        }
        list.sort(getSorter(ai));
        // SEARCH
        // FLEE
        return list;
    }

    private static Comparator<? super GOAL_TYPE> getSorter(UnitAI ai) {
        return new Comparator<GOAL_TYPE>() {
            @Override
            public int compare(GOAL_TYPE o1, GOAL_TYPE o2) {
                if (ai.getType().isCaster()) {
//                    if (o1==)
                }
                return 0;
            }
        };
    }

    public static List<GOAL_TYPE> getDefaultGoals() {
        return new ArrayList<>(Arrays.asList(GOAL_TYPE.PREPARE,
         GOAL_TYPE.ATTACK, GOAL_TYPE.DEFEND,
         GOAL_TYPE.PROTECT, GOAL_TYPE.WAIT));
    }

    private static void addEnemyGoals(List<GOAL_TYPE> list) {
        list.add(AiEnums.GOAL_TYPE.DEBUFF);
        list.add(AiEnums.GOAL_TYPE.CUSTOM_HOSTILE);
        list.add(AiEnums.GOAL_TYPE.DEBILITATE);
    }

    private static void addNonEnemyGoals(List<GOAL_TYPE> list) {
        // list.add(GOAL_TYPE.APPROACH);
        list.add(AiEnums.GOAL_TYPE.PREPARE);
        list.add(AiEnums.GOAL_TYPE.BUFF);
        list.add(AiEnums.GOAL_TYPE.ZONE_SPECIAL);
        list.add(AiEnums.GOAL_TYPE.ZONE_DAMAGE);
        list.add(AiEnums.GOAL_TYPE.SELF);
        list.add(AiEnums.GOAL_TYPE.SUMMONING);
        list.add(AiEnums.GOAL_TYPE.AUTO_BUFF);
        list.add(AiEnums.GOAL_TYPE.AUTO_DAMAGE);
        list.add(AiEnums.GOAL_TYPE.AUTO_DEBUFF);
        list.add(AiEnums.GOAL_TYPE.RESTORE);
        list.add(AiEnums.GOAL_TYPE.CUSTOM_SUPPORT);
        list.add(AiEnums.GOAL_TYPE.COATING);
    }

    private static List<GOAL_TYPE> getBehaviorGoals(Unit unit) {
        BEHAVIOR_MODE behaviorMode = unit.getUnitAI().getBehaviorMode();
        if (behaviorMode == null) {
            return null;
        }
        switch (behaviorMode) {
            case BERSERK:
                return new ListMaster<GOAL_TYPE>().getList(AiEnums.GOAL_TYPE.ATTACK);
            case CONFUSED:
                List<GOAL_TYPE> list = new ListMaster<GOAL_TYPE>().getList(AiEnums.GOAL_TYPE.WANDER,
                 AiEnums.GOAL_TYPE.MOVE, AiEnums.GOAL_TYPE.ATTACK, AiEnums.GOAL_TYPE.RETREAT);
                return new ListMaster<GOAL_TYPE>().getList(new RandomWizard<GOAL_TYPE>()
                 .getRandomListItem(list));

            case PANIC:
                return new ListMaster<GOAL_TYPE>().getList(AiEnums.GOAL_TYPE.RETREAT, AiEnums.GOAL_TYPE.COWER);
            default:
                break;
        }
        return null;
    }


    public static GOAL_TYPE getGoalFromAction(DC_ActiveObj a) {
        switch (a.getActionGroup()) {
            case ATTACK:
                return AiEnums.GOAL_TYPE.ATTACK;
            case HIDDEN:
                break;
            case ITEM:
                break;
            case MODE:
                return AiEnums.GOAL_TYPE.PREPARE;
            case MOVE:
                return AiEnums.GOAL_TYPE.RETREAT;
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
