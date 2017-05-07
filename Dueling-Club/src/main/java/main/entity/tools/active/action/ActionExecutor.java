package main.entity.tools.active.action;

import main.ability.effects.oneshot.mechanic.ModeEffect;
import main.content.mode.STD_MODES;
import main.elements.conditions.Conditions;
import main.elements.conditions.DistanceCondition;
import main.elements.conditions.OrConditions;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.tools.active.Activator;
import main.entity.tools.active.ActiveMaster;
import main.entity.tools.active.Executor;
import main.entity.tools.active.Targeter;
import main.game.battlecraft.ai.AI_Manager;
import main.game.battlecraft.rules.combat.attack.dual.CadenceRule;
import main.system.entity.ConditionMaster;
import main.system.math.Formula;

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

    private boolean checkContinuousMode() {
        ModeEffect effect = getAction().getModeEffect();
        if (effect == null) {
            return false;
        }
        if (effect.getMode().isContinuous()) {
            return false;
        }
        if (checkContinuousModeDeactivate()) {
            deactivate();
            return true;
        }

        removeModeBuffs();
        return false;
    }

    private void removeModeBuffs() {
        for (STD_MODES s : STD_MODES.values()) {
            ownerObj.removeBuff(s.getBuffName());
        }

    }

    public boolean deactivate() {
        try {
            ownerObj.removeBuff(getAction().getModeBuffName());
        } catch (Exception e) {
            e.printStackTrace();
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
            CadenceRule.checkDualAttackCadence(getAction(), ownerObj);
        } catch (Exception e) {
            e.printStackTrace();
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
                    for (DC_ActiveObj attack : getAction().getSubActions()) {
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
                    conditions = ConditionMaster.getFilteredConditions(conditions, DistanceCondition.class);
                    conditions.add(new DistanceCondition("" + maxRange));
                    SelectiveTargeting selectiveTargeting = new SelectiveTargeting(
                     SELECTIVE_TARGETING_TEMPLATES.ATTACK, conditions, new Formula("1"));
                    return selectiveTargeting;

                }
                return super.getTargeting();
            }
        };
    }

    public boolean checkContinuousModeDeactivate() {
        if (getAction().getModeEffect() == null) {
            return false;
        }
        return (getEntity().getOwnerObj().getBuff(getAction().getModeBuffName()) != null);

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
                    for (DC_ActiveObj attack : getAction().getSubActions()) {
                        if (attack.canBeActivated(ref, true)) {
                            return true;
                        }
                    }
                }
                if (getAction().isContinuousMode()) {
                    if (checkContinuousModeDeactivate()) {
                        return true;
                    }
                }
                return super.canBeActivated(ref, first);
            }
        };
    }
}
