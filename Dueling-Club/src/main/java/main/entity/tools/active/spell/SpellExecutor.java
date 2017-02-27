package main.entity.tools.active.spell;

import main.client.cc.logic.spells.DivinationMaster;
import main.content.PARAMS;
import main.content.enums.entity.SpellEnums;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.tools.active.ActiveMaster;
import main.entity.tools.active.Executor;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 2/26/2017.
 */
public class SpellExecutor extends Executor{

    public SpellExecutor(DC_ActiveObj active, ActiveMaster entityMaster) {
        super(active, entityMaster);
    }

    @Override
    public DC_SpellObj getAction() {
        return (DC_SpellObj) super.getAction();
    }
    public DC_SpellObj getSpell() {
        return (DC_SpellObj) super.getAction();
    }
    @Override
    public void actionComplete() {

        super.actionComplete();

        if (getSpell(). getSpellPool() == SpellEnums.SPELL_POOL.DIVINED) {
            if (DivinationMaster.rollRemove(getSpell() )) {
                if (getSpell().getBuff(DivinationMaster.BUFF_FAVORED) != null) {
                    getSpell(). removeBuff(DivinationMaster.BUFF_FAVORED);
                } else {
                    getSpell(). remove();
                }
            }
        }

    }

    @Override
    public void payCosts() {
        // TODO
//        if (channeling) {
//            // ChannelingRule.getChannelingCosts(this).pay(ref); // new
//            // Channeling rules!
//            addCooldown();
//            channelingResolveCosts.pay(ref);
//            channeling = false;
//            return;
//        }
        super.payCosts();
    }

//    public boolean activateChanneling() {
//        initCosts();
//        initChannelingCosts();
//        game.getLogManager().log(">> " + ownerObj.getName() + " has begun Channeling " + getName());
//        boolean result = ( checkExtraAttacksDoNotInterrupt(ENTRY_TYPE.ACTION));
//        if (result) {
//            this.channeling = true;
//            ChannelingRule.playChannelingSound(getSpell(), HeroAnalyzer.isFemale(ownerObj));
//            result = ChannelingRule.activateChanneing(getSpell());
//        }
//        if (result) {
////            if (getOwner().isMe()) {
////                communicate(ref);
////            }
//        }
//        channelingActivateCosts.pay(ref);
//        actionComplete();
//        return result;
//    }


//    @Override
//    public boolean activatedOn(Ref ref) {
//        if (getGame().isOnline()) {
//            if (getOwnerObj().isActiveSelected()) {
//                if (!getActivator(). isChanneling()) {
//                    if (isChanneling()) {
//                        return activateChanneling();
//                    }
//                }
//            }
//        }
//
//        ownerObj.getRef().setID(KEYS.SPELL, id);
//        if (!isQuietMode()) {
//            if (!new Event(STANDARD_EVENT_TYPE.SPELL_ACTIVATED, ref).fire()) {
//                return false;
//            }
//        }
//        return super.activatedOn(ref);
//    }
//    @Override
//    public boolean activate() {
//        ownerObj.getRef().setID(Ref.KEYS.SPELL, id);
//        SoundMaster.playEffectSound(SOUNDS.PRECAST, getSpell());
//        if (isChanneling()) {
//            return activateChanneling();
//        } else {
//            return super.activate();
//        }
//    }
//    @Override
//    public boolean resolve() {
//
//        if (!isQuietMode()) {
//            if (!new Event(STANDARD_EVENT_TYPE.SPELL_BEING_RESOLVED, ref).fire()) {
//                return false;
//            }
//        }
//        if (isInterrupted()) {
//            return true; // TODO group effects blocked?!
//        }
//        boolean result;
//
//        applySpellpowerMod();
//        SoundMaster.playEffectSound(SOUNDS.RESOLVE, getSpell());
//        result = super.resolve();
//        if (result) {
//            applyImpactSpecialEffect();
//        }
//
//        if (!isQuietMode()) {
//            new Event(STANDARD_EVENT_TYPE.SPELL_RESOLVED, ref).fire();
//        }
//
//        if (!result) {
//            if (channeling) {
//                SoundMaster.playEffectSound(SOUNDS.FAIL, getSpell());
//            } else
//            // try fail sound?
//            {
//                SoundMaster.playStandardSound(STD_SOUNDS.SPELL_RESISTED);
//            }
//        }
//
//        return result;
//    }
//
//    private void applyImpactSpecialEffect() {
//        SPECIAL_EFFECTS_CASE case_type = SPECIAL_EFFECTS_CASE.SPELL_IMPACT;
//        // TODO will this ref have any {event} or {amount}?
//        // perhaps this sort of thing should be stored in a special way, not
//        // just in effect's ref!
//
//        // TODO spell itself should also have special effects available and
//        // separate from unit's!
//        if (ref.getTargetObj() instanceof DC_UnitModel) {
//            ownerObj.applySpecialEffects(case_type, (BattleFieldObject) ref.getTargetObj(), ref);
//        }
//        if (ref.getGroup() != null) {
//            for (Obj unit : ref.getGroup().getObjects()) {
//                if (unit != ref.getTargetObj()) {
//                    if (unit instanceof DC_UnitModel) {
//                        ownerObj.applySpecialEffects(case_type, (BattleFieldObject) unit, ref);
//                    }
//                }
//            }
//        }
//    }

    private void applySpellpowerMod() {
        ownerObj.modifyParameter(PARAMS.SPELLPOWER, getIntParam(PARAMS.SPELLPOWER_BONUS));

        Integer perc = getIntParam(PARAMS.SPELLPOWER_MOD);
        if (perc != 100) {
            ownerObj.multiplyParamByPercent(PARAMS.SPELLPOWER, MathMaster.getFullPercent(perc),
             false);
        }

    }
}
