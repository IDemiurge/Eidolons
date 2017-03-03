package main.game.ai.elements.actions;

import main.content.PROPS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums.AI_LOGIC;
import main.data.XList;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.actions.sequence.ActionSequence;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.game.ai.tools.target.AI_SpellMaster;
import main.game.logic.generic.DC_ActionManager;
import main.game.logic.generic.DC_ActionManager.STD_ACTIONS;
import main.game.logic.generic.DC_ActionManager.STD_MODE_ACTIONS;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiUnitActionMaster {
    public static List<ActionSequence> splitRangedSequence(ActionSequence sequence) {
        ArrayList<ActionSequence> list = new ArrayList<>();
        for (Action a : sequence.getActions()) {
            if (a instanceof QuickItemAction) {
                ArrayList<Action> actions = new ArrayList<>();
                actions.add(a);
                for (Action a1 : sequence.getActions()) {
                    if (!(a1 instanceof QuickItemAction)) {
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

    //returns all of unit's active that we want to check for execution
    public static List<DC_ActiveObj> getFullActionList(GOAL_TYPE type, Unit unit) {
        // cache
        List<DC_ActiveObj> actions = new XList<>();
        switch (type) {

            case PATROL:
            case WANDER:
            case RETREAT:
            case MOVE:
            case APPROACH:
                // dummy action!
                actions.add(ActionFactory.getUnitAction(unit, "Move"));
                break;

            case ATTACK:
                if (unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK) != null) {
                    actions.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.STANDARD_ATTACK));
                }
                if (unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK) != null) {
                    actions.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK));
                }

                actions.remove(ActionFactory.getUnitAction(unit, DC_ActionManager.OFFHAND_ATTACK));
                actions.remove(ActionFactory.getUnitAction(unit, DC_ActionManager.THROW_MAIN));
                actions.remove(ActionFactory.getUnitAction(unit, DC_ActionManager.THROW_OFFHAND));
                break;

            case DEFEND:
                actions.add(ActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));
                actions.add(ActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
                break;

            case COWER:
                actions.add(ActionFactory.getUnitAction(unit, "Cower"));
                break;
            case AMBUSH:
                if (!checkAddStealth(true, unit, actions)) {
                    actions.add(ActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
                }
                break;
            case STALK:
                if (!checkAddStealth(false, unit, actions)) {
                    actions.add(ActionFactory.getUnitAction(unit, "Move"));
                }
                break;
            case STEALTH:
                checkAddStealth(false, unit, actions);
                break;
            case SEARCH: // can it be MOVE?
                if (unit.getBuff("Search Mode") == null) {
                    actions.add(ActionFactory.getUnitAction(unit, "Search Mode"));
                } else {
                    actions.add(ActionFactory.getUnitAction(unit, "Move"));
                }
                break;
            case WAIT:
                actions.add(ActionFactory.getUnitAction(unit, STD_ACTIONS.Wait.name()));
                break;
            case PREPARE:
                actions.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.MODE));
                if (!unit.isLiving()) {
                    actions.remove(ActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));

                }
                actions.remove(ActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));
                actions.remove(ActionFactory.getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
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
            if (ActionFactory.getUnitAction(unit, "Stealth Mode") != null) {
                actions.add(ActionFactory.getUnitAction(unit, "Stealth Mode"));
                return true;
            }
        }
        if (ActionFactory.getUnitAction(unit, "Hide Mode") != null) {
            actions.add(ActionFactory.getUnitAction(unit, "Hide Mode"));
            return true;
        } else if (ActionFactory.getUnitAction(unit, "Stealth Mode") != null) {
            actions.add(ActionFactory.getUnitAction(unit, "Stealth Mode"));
            return true;
        }
        return false;

    }

    public static List<DC_ActiveObj> getMoveActions(Unit unit) {
        List<DC_ActiveObj> list = new LinkedList<>();
        list.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.ADDITIONAL_MOVE));
        List<DC_UnitAction> actionList = unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_MOVE);
        if (ListMaster.isNotEmpty(actionList)) {
            list.addAll(actionList);
        }
        list.addAll(ActionFilter.filterActives(GOAL_TYPE.MOVE, (unit.getSpells())));
        list.add(ActionFactory.getUnitAction(unit, "Move"));
        return list;
    }

    public static List<DC_ActiveObj> getActionObjectList(List<Action> actions) {
        List<DC_ActiveObj> activeList = new LinkedList<>();
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
        List<DC_ActiveObj> list = new LinkedList<>();
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
