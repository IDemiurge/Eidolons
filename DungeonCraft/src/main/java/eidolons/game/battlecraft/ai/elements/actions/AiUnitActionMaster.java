package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.content.PROPS;
import eidolons.entity.active.ActiveObj;
import eidolons.entity.active.UnitAction;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.tools.target.AI_SpellMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_LOGIC;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.data.XList;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 3/3/2017.
 */
public class AiUnitActionMaster {
    public static List<ActionSequence> splitRangedSequence(ActionSequence sequence) {
        ArrayList<ActionSequence> list = new ArrayList<>();
        for (AiAction a : sequence.getActions()) {
            if (a instanceof AiQuickItemAction) {
                ArrayList<AiAction> aiActions = new ArrayList<>();
                aiActions.add(a);
                for (AiAction a1 : sequence.getActions()) {
                    if (!(a1 instanceof AiQuickItemAction)) {
                        aiActions.add(a1);
                    }
                }
                ActionSequence rangedSequence = new ActionSequence(aiActions, sequence.getTask(),
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
    public static List<ActiveObj> getFullActionList(GOAL_TYPE type, Unit unit) {
        // cache
        List<ActiveObj> actions = new XList<>();
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
                ActionFilter.filterAttacks(actions, unit);

                break;

            case DEFEND:
                actions.add(AiActionFactory.getUnitAction(unit, ActionEnums.DEFAULT_ACTION.Defend.name()));
                actions.add(AiActionFactory.getUnitAction(unit, ActionEnums.DEFAULT_ACTION.On_Alert.name()));
                break;

            case COWER:
                actions.add(AiActionFactory.getUnitAction(unit, "Cower"));
                break;
            case AMBUSH:
                if (!checkAddStealth(true, unit, actions)) {
                    actions.add(AiActionFactory.getUnitAction(unit, ActionEnums.DEFAULT_ACTION.On_Alert.name()));
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
                    actions.add(AiActionFactory.getUnitAction(unit, "Turn Clockwise"));
                    actions.add(AiActionFactory.getUnitAction(unit, "Turn Anticlockwise"));
                } else {
                    actions.add(AiActionFactory.getUnitAction(unit, "Move"));
                }
                break;
            case WAIT:
                actions.add(AiActionFactory.getUnitAction(unit, "Wait"));
                break;
            case PREPARE:
                actions.addAll(unit.getActionMap().get(ActionEnums.ACTION_TYPE.MODE));
                if (!unit.isLiving()) {
                    actions.remove(AiActionFactory.getUnitAction(unit, ActionEnums.DEFAULT_ACTION.Defend.name()));

                }
                actions.remove(AiActionFactory.getUnitAction(unit, ActionEnums.DEFAULT_ACTION.Defend.name()));
                actions.remove(AiActionFactory.getUnitAction(unit, ActionEnums.DEFAULT_ACTION.On_Alert.name()));
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
                                           List<ActiveObj> actions) {
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

    public static List<ActiveObj> getMoveActions(Unit unit) {
        List<ActiveObj> list = new ArrayList<>(unit.getActionMap().get(ActionEnums.ACTION_TYPE.ADDITIONAL_MOVE));
        DequeImpl<UnitAction> actionList = unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_MOVE);
        if (ListMaster.isNotEmpty(actionList)) {
            list.addAll(actionList);
        }
        list.addAll(ActionFilter.filterActives(AiEnums.GOAL_TYPE.MOVE, (unit.getSpells())));
        list.add(AiActionFactory.getUnitAction(unit, "Move"));
        return list;
    }

    public static List<ActiveObj> getActionObjectList(List<AiAction> aiActions) {
        List<ActiveObj> activeList = new ArrayList<>();
        if (aiActions != null) {
            for (AiAction object : aiActions) {
                if (object != null) {
                    activeList.add(object.getActive());
                }
            }
        }
        return activeList;
    }

    public static Collection<ActiveObj> getSpells(AI_LOGIC logic, Unit unit) {
        List<ActiveObj> list = new ArrayList<>();
        for (ActiveObj spell : unit.getSpells()) {
            if (spell.getProperty(PROPS.AI_LOGIC).equalsIgnoreCase(logic.toString())) {
                list.add(spell);
            } else {
                try {
                    if (AI_SpellMaster.getSpellLogic(spell) == logic) {
                        list.add(spell);
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
        return list;

    }
}
