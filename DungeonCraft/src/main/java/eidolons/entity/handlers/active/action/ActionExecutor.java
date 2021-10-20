package eidolons.entity.handlers.active.action;

import eidolons.ability.effects.oneshot.mechanic.ModeEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.handlers.active.Activator;
import eidolons.entity.handlers.active.ActiveMaster;
import eidolons.entity.handlers.active.Executor;
import eidolons.entity.handlers.active.Targeter;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.rules.combat.attack.AttackTypes;
import eidolons.game.battlecraft.rules.combat.attack.dual.DualWieldingRule;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.mode.STD_MODES;
import main.elements.conditions.Conditions;
import main.elements.conditions.DistanceCondition;
import main.elements.conditions.OrConditions;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.entity.ConditionMaster;

/**
 * Created by JustMe on 2/26/2017.
 */
public class ActionExecutor extends Executor {
    public ActionExecutor(DC_ActiveObj active, ActiveMaster entityMaster) {
        super(active, entityMaster);
    }

    @Override
    public boolean activate() {
        if (checkContinuousMode()) {
            return true;
        }
        return super.activate();
    }

    protected void syncActionRefWithSource() {
        super.syncActionRefWithSource();
        if (getEntity().isAttackAny()){
            getRef().setID(KEYS.ATTACK_WEAPON, getEntity().isOffhand()? getRef().getId(KEYS.OFFHAND)
             :  getRef().getId(KEYS.WEAPON));
        }
    }
    //will remove all mode buffs?
    private boolean checkContinuousMode() {
        ModeEffect effect = getAction().getModeEffect();
        if (effect == null)
            return false;

//        STD_MODES mode = (STD_MODES) getOwnerUnit().getMode();
//        switch (mode) {
//            case SEARCH: // search disabled by any mode!
//                return true;
//        }

        if (effect.getMode().isContinuous()) {
            if (checkActionDeactivatesContinuousMode()) {
                getAction().getOwnerUnit().removeBuff(effect.getMode().getBuffName());
                //deactivation overrides
                return true;
            }
        }
        if (getOwnerObj().getMode().equals(STD_MODES.GUARDING)
         || getOwnerObj().checkStatus(STATUS.GUARDING)
         ) {
            if (!effect.getMode().isDisableCounter()) {
                //GUARD is semi-compatible
                return false;
            }
        } else {
            //mode activation proceeds
            removeModeBuffs();
            return false;
        }
        return false;
    }


    public boolean checkActionDeactivatesContinuousMode() {
        //check same mode deactivated
        return (getEntity().getOwnerUnit().getBuff(getAction().getModeBuffName()) != null);

    }

    private void removeModeBuffs() {
        for (STD_MODES s : STD_MODES.values()) {
            getAction().getOwnerUnit().removeBuff(s.getBuffName());
        }

    }

    public boolean deactivate() {
        try {
            getAction().getOwnerUnit().removeBuff(getAction().getModeBuffName());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            getGame().getManager().setActivatingAction(null);
            game.getManager().reset();
            game.getManager().refreshAll();
        }

        return true;
    }

    @Override
    public DC_UnitAction getAction() {
        return (DC_UnitAction) super.getAction();
    }

    @Override
    public void actionComplete() {
        super.actionComplete();
        try {
            DualWieldingRule.checkDualAttackCadence(getAction(), getAction().getOwnerUnit());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    @Override
    protected Targeter createTargeter(DC_ActiveObj active, ActiveMaster entityMaster) {
        return new Targeter(active, entityMaster) {

            @Override
            public Targeting getTargeting() {

                initTargetingMode();
                if (AI_Manager.isRunning()) {
                    return super.getTargeting();
                }

                if (getAction().isAttackGeneric()) {
                    Conditions conditions = new OrConditions();
                    int maxRange = 0;
                    for (DC_ActiveObj attack : getAction().getAttackTypes()) {
                        if (attack.isThrow()) {
                            continue;
                        }
                        if (!attack.canBeActivated()) {
                            continue;
                        }
                        conditions.add(attack.getTargeting().getFilter().getConditions());
                        if (maxRange < attack.getRange()) {
                            maxRange = attack.getRange();
                        }
                    }
                    conditions.setFastFailOnCheck(true);
//                    conditions.addAll( (Conditions)
//                            DC_ConditionMaster.getSelectiveTargetingTemplateConditions(SELECTIVE_TARGETING_TEMPLATES.ATTACK));
                    conditions = ConditionMaster.getFilteredConditions(conditions, DistanceCondition.class);
                    conditions.add(new DistanceCondition("" + maxRange));
                    return new SelectiveTargeting(
                     SELECTIVE_TARGETING_TEMPLATES.ATTACK, conditions);

                }
                return super.getTargeting();
            }
        };
    }

    @Override
    protected Activator createActivator(DC_ActiveObj active, ActiveMaster entityMaster) {
        return new Activator(active, entityMaster) {
            @Override
            public DC_UnitAction getAction() {
                return (DC_UnitAction) super.getAction();
            }

            @Override
            public boolean canBeActivated(Ref ref, boolean first) {
                if (getAction().isAttackGeneric()) {
                    return AttackTypes.canActivateAny(active);
                }
                if (getAction().isContinuousMode()) {
                    if (checkActionDeactivatesContinuousMode()) {
                        return true;
                    }
                }
                return super.canBeActivated(ref, first);
            }
        };
    }
}
