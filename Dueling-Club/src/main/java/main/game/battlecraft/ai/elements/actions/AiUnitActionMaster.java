package main.game.battlecraft.ai.elements.actions;

import main.content.PROPS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_LOGIC;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.data.XList;
import main.entity.active.DC_ActionManager;
import main.entity.active.DC_ActionManager.STD_MODE_ACTIONS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.tools.target.AI_SpellMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiUnitActionMaster {
    public static List<ActionSequence> splitRangedSequence(ActionSequence sequence) {
        ArrayList<ActionSequence> list = new ArrayList<>();
        for (Action a : sequence.getActions()) {
            if (a instanceof AiQuickItemAction) {
                ArrayList<Action> actions = new ArrayList<>();
                actions.add(a);
                for (Action a1 : sequence.getActions()) {
                    if (!(a1 instanceof AiQuickItemAction)) {
                        actions.add(a1);
                    }
                }
                ActionSequence rangedSequence = new ActionSequence(actions, sequence.getTask(),
                        sequence.getAi());
                list.add(rangedSequence);
            }
        }
        if (list.isEmpty()) {
            list.add(sequence);
        }
        return list;
    }

    //returns all of unit's active that we want to preCheck for execution
    public static List<DC_ActiveObj> getFullActionList(GOAL_TYPE type, Unit unit) {
        // cache
        List<DC_ActiveObj> actions = new XList<>();
        switch (type) {
            case PROTECT:
                actions.add(AiActionFactory.getUnitAction(unit, "Guard Mode"));
                break;
            case PATROL:
            case WANDER:
            case RETREAT:
            case MOVE:
            case APPROACH:
                // dummy action!
                actions.add(AiActionFactory.getUnitAction(unit, "Move"));
                break;

            case ATTACK:
                if (unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK) != null) {
                    actions.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.STANDARD_ATTACK));
                }
                if (unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK) != null) {
                    actions.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK));
                }

                actions.remove(AiActionFactory.getUnitAction(unit, DC_ActionManager.OFFHAND_ATTACK));
                DC_UnitAction
                        action = unit.getAction(
                        "Throw", false);
                actions.remove(action);
                action = unit.getAction(
                        "Throw", false);
                actions.remove(action);
                break;

            case DEFEND:
                actions.add(AiActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));
                actions.add(AiActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
                break;

            case COWER:
                actions.add(AiActionFactory.getUnitAction(unit, "Cower"));
                break;
            case AMBUSH:
                if (!checkAddStealth(true, unit, actions)) {
                    actions.add(AiActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
                }
                break;
            case STALK:
                if (!checkAddStealth(false, unit, actions)) {
                    actions.add(AiActionFactory.getUnitAction(unit, "Move"));
                }
                break;
            case STEALTH:
                checkAddStealth(false, unit, actions);
                break;
            case SEARCH: // can it be MOVE?
                if (unit.getBuff("Search Mode") == null) {
                    actions.add(AiActionFactory.getUnitAction(unit, "Search Mode"));
                } else {
                    actions.add(AiActionFactory.getUnitAction(unit, "Move"));
                }
                break;
            case WAIT:
                actions.add(AiActionFactory.getUnitAction(unit,  "Wait"));
                break;
            case PREPARE:
                actions.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.MODE));
                if (!unit.isLiving()) {
                    actions.remove(AiActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));

                }
                actions.remove(AiActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));
                actions.remove(AiActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
                break;
        }
        actions.addAll(ActionFilter.filterActives(type, (unit.getSpells())));
        actions.addAll(ActionFilter.filterActives(type, (unit.getQuickItemActives())));
        if (type.isFilterByCanActivate()) {
            actions = ActionFilter.filterByCanActivate(unit, actions);
        }
        return actions;
    }

    private static boolean checkAddStealth(boolean hidePref, Unit unit,
                                           List<DC_ActiveObj> actions) {
        if (unit.getBuff("Stealth Mode") != null) {
            return false;
        }
        if (unit.getBuff("Hide Mode") != null) {
            return false;
        }

        if (!hidePref) {
            if (AiActionFactory.getUnitAction(unit, "Stealth Mode") != null) {
                actions.add(AiActionFactory.getUnitAction(unit, "Stealth Mode"));
                return true;
            }
        }
        if (AiActionFactory.getUnitAction(unit, "Hide Mode") != null) {
            actions.add(AiActionFactory.getUnitAction(unit, "Hide Mode"));
            return true;
        } else if (AiActionFactory.getUnitAction(unit, "Stealth Mode") != null) {
            actions.add(AiActionFactory.getUnitAction(unit, "Stealth Mode"));
            return true;
        }
        return false;

    }

    public static List<DC_ActiveObj> getMoveActions(Unit unit) {
        List<DC_ActiveObj> list = new ArrayList<>();
        list.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.ADDITIONAL_MOVE));
        DequeImpl<DC_UnitAction> actionList = unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_MOVE);
        if (ListMaster.isNotEmpty(actionList)) {
            list.addAll(actionList);
        }
        list.addAll(ActionFilter.filterActives(AiEnums.GOAL_TYPE.MOVE, (unit.getSpells())));
        list.add(AiActionFactory.getUnitAction(unit, "Move"));
        return list;
    }

    public static List<DC_ActiveObj> getActionObjectList(List<Action> actions) {
        List<DC_ActiveObj> activeList = new ArrayList<>();
        if (actions != null) {
            for (Action object : actions) {
                if (object != null) {
                    activeList.add(object.getActive());
                }
            }
        }
        return activeList;
    }

    public static Collection<DC_ActiveObj> getSpells(AI_LOGIC logic, Unit unit) {
        List<DC_ActiveObj> list = new ArrayList<>();
        for (DC_ActiveObj spell : unit.getSpells()) {
            if (spell.getProperty(PROPS.AI_LOGIC).equalsIgnoreCase(logic.toString())) {
                list.add(spell);
            } else {
                try {
                    if (AI_SpellMaster.getSpellLogic(spell) == logic) {
                        list.add(spell);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;

    }
}
