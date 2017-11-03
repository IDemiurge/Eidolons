package main.game.battlecraft.rules.combat.attack.extra_attack;

import main.content.enums.entity.ActionEnums;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.LinkedList;
import java.util.List;

public class ExtraAttacksRule {

    public static boolean checkInterrupted(DC_ActiveObj action, ENTRY_TYPE enclosingEntryType) {
        boolean result = false;
        String message = StringMaster
                .getMessagePrefix(true, action.getOwnerObj().getOwner().isMe())
                + StringMaster.getPossessive(action.getOwnerObj().getNameIfKnown())
                + " "
                + action.getDisplayedName() + " has been interrupted";
        if (InstantAttackRule.checkInstantAttacksInterrupt(action)) {
            result = true;
            message += " by an Instant Attack";
        } else if (AttackOfOpportunityRule.checkAttacksOfOpportunityInterrupt(action)) {
            result = true;
            message += " by an Attack of Opportunity";
        }
        // action.getGame().getLogManager().newLogEntryNode(type, args)
        if (!result) {
            result = (checkSourceInterrupted(action));
        }
        if (result) {
            action.getGame().getLogManager().log(LOG.GAME_INFO, message, enclosingEntryType);
        }
        return result;
    }

    private static boolean checkSourceInterrupted(DC_ActiveObj action) {
        if (action.getOwnerObj().isDead()) {
            return true;
        }
        if (action.getOwnerObj().isDisabled()) {
            return true;
        }
        return false;
    }

    public static List<DC_ActiveObj> getCounterAttacks(DC_ActiveObj triggeringAction,
                                                       Unit unit) {
        List<DC_ActiveObj> list = new LinkedList<>();
        if (unit.getActionMap().get(ActionEnums.ACTION_TYPE.STANDARD_ATTACK) == null) {
            return list;
        }
        for (DC_UnitAction a : unit.getActionMap().get(ActionEnums.ACTION_TYPE.STANDARD_ATTACK)) {
            // offhand?
            if (a.isMelee() && !a.isAttackGeneric())
            // auto-atk range?
            {
                list.add(a);
            }
        }
        return list;
    }

}
