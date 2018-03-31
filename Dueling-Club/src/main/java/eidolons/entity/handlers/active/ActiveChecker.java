package eidolons.entity.handlers.active;

import eidolons.ability.effects.containers.customtarget.ShapeEffect;
import eidolons.ability.effects.containers.customtarget.ZoneEffect;
import main.content.DC_TYPE;
import eidolons.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TAGS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import main.entity.handlers.EntityChecker;
import main.entity.handlers.EntityMaster;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;

/**
 * Created by JustMe on 2/23/2017.
 */
public class ActiveChecker extends EntityChecker<DC_ActiveObj> {

    private Boolean zone;
    private Boolean missile;

    public ActiveChecker(DC_ActiveObj entity,
                         EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }


    public boolean checkStatus(STATUS blocked) {
        return getEntity().checkStatus(blocked);
    }

    public boolean isThrow() {
        if (getName().contains(ActionEnums.ACTION_TAGS.THROW.toString())) {
            return true;
        }
        if (getEntity() instanceof DC_QuickItemAction) {
            DC_QuickItemAction itemActiveObj = (DC_QuickItemAction) getEntity();
            if (!itemActiveObj.getItem().isAmmo()) {
                if (itemActiveObj.getItem().getWrappedWeapon() != null) {
                    return true;
                }
            }
        }
        return checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.THROW.toString())
         || checkProperty(G_PROPS.GROUP, ActionEnums.ACTION_TAGS.THROW.toString());
    }

    public boolean isBlocked() {
        getEntity().setCustomTooltip(null);
        if (!AI_Manager.isOff())
            if (getEntity().getOwnerObj().isAiControlled()) {
                getEntity().getCosts().setReason("This unit is AI controlled!");
                return true;
            }
        if (!getEntity().getOwnerObj().isMine()) {
            getEntity().setCustomTooltip("You do not control this unit!");
            return true;
        }
        if (getGame().getManager().getActiveObj() != null) {
            if (!getGame().getManager().getActiveObj().isMine()) {
                getEntity().setCustomTooltip("Wait for the enemy's turn!");
                return true;
            }
        }
        if (!getGame().isOffline()) {
//            if (getGame().isHost()) {
//                if (getGame().getConnector().isWaiting()) {
//                    getEntity().setCustomTooltip("Wait for the other players to join!");
//                    return true;
//                }
//            }
        }
        return false;
    }

    public boolean isMove() {
        return getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.MOVE;
    }

    public boolean isTurn() {
        return getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.TURN;
    }

    public boolean isMelee() {
        if (getIntParam(PARAMS.RANGE) > 1) {
            return false;
        }
        return !isRanged();
    }

    public boolean isStandardAttack() {
        return getActionType() == ActionEnums.ACTION_TYPE.STANDARD_ATTACK;
    }


    public boolean isAttackGeneric() {
        return getName().equals(DC_ActionManager.ATTACK)
         || getName().equals(DC_ActionManager.OFFHAND_ATTACK);
    }

    public boolean isOffhand() {
        return checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.OFF_HAND.toString());
    }

    public boolean isTopDown() {
        return checkProperty(getTagProp(), ActionEnums.ACTION_TAGS.TOP_DOWN.toString());
    }

    protected PROPERTY getTagProp() {
        return G_PROPS.ACTION_TAGS;
    }

    public boolean isRanged() {
        if (getEntity().getActionGroup() != ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
            return false;
        }
        return (checkProperty(G_PROPS.GROUP, ActionEnums.ACTION_TAGS.RANGED.toString()) || checkProperty(
         G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.RANGED.toString()));
        // return false;
        // return getIntParam(PARAMS.RANGE) > 1;
    }

    public boolean isMissile() {
        if (missile != null) {
            return missile;
        }
        if (getEntity().checkProperty(G_PROPS.SPELL_TAGS, SpellEnums.SPELL_TAGS.MISSILE.toString())) {
            missile = true;
            return true;
        }
        if (getEntity().checkProperty(G_PROPS.ACTION_TAGS, ActionEnums.ACTION_TAGS.MISSILE.toString())) {
            missile = true;
            return true;
        }
        missile = false;
        return false;
    }

    public boolean isZone() {
        if (zone != null) {
            return zone;
        }
        if (EffectFinder.check(getEntity().getAbilities(), ZoneEffect.class)) {
            zone = true;
            return true;
        }
        if (EffectFinder.check(getEntity().getAbilities(), ShapeEffect.class)) {
            zone = true;
            return true;
        }
        zone = false;
        return false;
    }

    public ACTION_TYPE_GROUPS getActionGroup() {
        return getEntity().getActionGroup();
    }

    public ACTION_TYPE getActionType() {
        return getEntity().getActionType();
    }


    public boolean isCancellable() {
        return checkProperty(G_PROPS.STD_BOOLS, STD_BOOLS.CANCELLABLE
         .toString());
    }

    public boolean isRangedTouch() {
        return getEntity().isRangedTouch();
    }

    public boolean isAttack() {
        return getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
    }

    public boolean isAttackAny() {
        return getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK || isAttackGeneric() || isStandardAttack();
    }


    public boolean isCancelDefault() {
        if (isAttackGeneric()) {
            return true;
        }
        return checkBool(GenericEnums.STD_BOOLS.CANCEL_FOR_FALSE);
    }

    public boolean isResistible() {

        return getEntity().getResistanceType() == SpellEnums.RESISTANCE_TYPE.CHANCE_TO_BLOCK;
    }

    public boolean isSubActionOnly() {
        return isAttackGeneric();
    }

    public boolean isSpell() {
        return getEntity().getOBJ_TYPE_ENUM() == DC_TYPE.SPELLS;
    }

    public boolean isChanneling() {
        return false;
    }

    public boolean isDualAttack() {
        return checkProperty(G_PROPS.ACTION_TAGS, ACTION_TAGS.DUAL.toString());
    }


    public boolean isPotentiallyHostile() {
        if (getActionGroup() == ACTION_TYPE_GROUPS.MOVE)
            return false;
        if (getActionGroup() == ACTION_TYPE_GROUPS.MODE)
            return false;
        return getActionGroup() != ACTION_TYPE_GROUPS.TURN;
    }
}
