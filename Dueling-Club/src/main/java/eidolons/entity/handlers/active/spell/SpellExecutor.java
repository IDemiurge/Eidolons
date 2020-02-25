package eidolons.entity.handlers.active.spell;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.entity.handlers.active.ActiveMaster;
import eidolons.entity.handlers.active.Executor;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.DC_UnitModel;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.herocreator.logic.HeroAnalyzer;
import eidolons.game.module.herocreator.logic.spells.DivinationMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.SpellEnums;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

/**
 * Created by JustMe on 2/26/2017.
 */
public class SpellExecutor extends Executor {

    private boolean channeling;

    public SpellExecutor(DC_ActiveObj active, ActiveMaster entityMaster) {
        super(active, entityMaster);
    }

    @Override
    public Spell getAction() {
        return (Spell) super.getAction();
    }

    public Spell getSpell() {
        return (Spell) super.getAction();
    }

    @Override
    public SpellInitializer getInitializer() {
        return (SpellInitializer) super.getInitializer();
    }

    @Override
    public void actionComplete() {

        super.actionComplete();

        if (getSpell().getSpellPool() == SpellEnums.SPELL_POOL.DIVINED) {
//            if (DivinationMaster.rollRemove(getSpell())) {
                if (getSpell().getBuff(DivinationMaster.BUFF_FAVORED) != null) {
                    getSpell().removeBuff(DivinationMaster.BUFF_FAVORED);
                } else {
                    getSpell().remove();
                }
//            }
        }

    }

    @Override
    public void payCosts() {
        // TODO
        if (channeling) {
            // Channeling rules!
            addCooldown();
            getSpell().getChannelingResolveCosts().pay(getRef());
            return;
        }
        super.payCosts();
    }

    public boolean activateChanneling() {
        getAction().initCosts();
        getInitializer().initChannelingCosts();
        game.getLogManager().log(">> " + getAction().getOwnerObj().getName() + " has begun Channeling " + getName());
        boolean result = (checkExtraAttacksDoNotInterrupt(ENTRY_TYPE.ACTION));
        if (result) {
            this.channeling = true;
            getAction().setChannelingNow(channeling);
            ChannelingRule.playChannelingSound(getSpell(), HeroAnalyzer.isFemale(getAction().getOwnerObj()));
            result = ChannelingRule.activateChanneing(getSpell());

        }
        if (result) {
//            if (getOwner().isMe()) {
//                communicate(ref);
//            }
        }
        getSpell().getChannelingActivateCosts().pay(getRef());
        actionComplete();
        return result;
    }


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
    @Override
    public boolean activate() {
        getAction().getOwnerObj().getRef().setID(Ref.KEYS.SPELL, getId());
        DC_SoundMaster.playEffectSound(SOUNDS.PRECAST, getSpell());
        if (!ExplorationMaster.isExplorationOn())
            if (!channeling)
                if (getAction().isChanneling()) {
                    return activateChanneling();
                }
        channeling = false;
        getAction().setChannelingNow(channeling);
        return super.activate();

    }

    @Override
    public void resolve() {
        DC_SoundMaster.playEffectSound(SOUNDS.DARK, getAction().getOwnerObj());
        DC_SoundMaster.playEffectSound(SOUNDS.EVIL, getAction().getOwnerObj());
        if (!getRef().isQuiet()) {
            result = new Event(STANDARD_EVENT_TYPE.SPELL_BEING_RESOLVED, getRef()).fire();
            if (!result) {
                return;
            }
        }
        if (isInterrupted()) {
            return;
//            return true; // TODO group effects blocked?!
        }

        applySpellpowerMod();
        DC_SoundMaster.playEffectSound(SOUNDS.RESOLVE, getSpell());
        super.resolve();
        if (result) {
            applyImpactSpecialEffect();
        }

        if (!getRef().isQuiet()) {
            new Event(STANDARD_EVENT_TYPE.SPELL_RESOLVED, getRef()).fire();
        }
        if (!result) {
            if (channeling) {
                DC_SoundMaster.playEffectSound(SOUNDS.FAIL, getSpell());
                if (!getRef().isQuiet()) {
                    new Event(STANDARD_EVENT_TYPE.CHANNELING_FAIL, getRef()).fire();
                }
            } else
            // try fail sound?
            {
                if (!getRef().isQuiet()) {
                    new Event(STANDARD_EVENT_TYPE.SPELL_RESISTED, getRef()).fire();
                }
                DC_SoundMaster.playStandardSound(STD_SOUNDS.SPELL_RESISTED);
            }
        } else {

            if (!getRef().isQuiet()) {
                if (getAction().isChannelingNow())
                {
                    new Event(STANDARD_EVENT_TYPE.CHANNELING_STARTED, getRef()).fire();
                }
            }
        }

    }

    //
    private void applyImpactSpecialEffect() {
        SPECIAL_EFFECTS_CASE case_type = SPECIAL_EFFECTS_CASE.SPELL_IMPACT;
        // TODO will this ref have any {event} or {amount}?
        // perhaps this sort of thing should be stored in a special way, not
        // just in effect's ref!

        // TODO spell itself should also have special effects available and
        // separate from unit's!
        if (getRef().getTargetObj() instanceof DC_UnitModel) {
            getAction().getOwnerObj().applySpecialEffects(case_type, (BattleFieldObject) getRef().getTargetObj(), getRef());
        }
        if (getRef().getGroup() != null) {
            for (Obj unit : getRef().getGroup().getObjects()) {
                if (unit != getRef().getTargetObj()) {
                    if (unit instanceof DC_UnitModel) {
                        getAction().getOwnerObj().applySpecialEffects(case_type, (BattleFieldObject) unit, getRef());
                    }
                }
            }
        }
    }

    private void applySpellpowerMod() {
        getAction().getOwnerObj().modifyParameter(PARAMS.SPELLPOWER, getIntParam(PARAMS.SPELLPOWER_BONUS));
        Integer perc = getIntParam(PARAMS.SPELLPOWER_MOD);
        if (getRef().getTargetObj() == getAction().getOwnerUnit()) {
            perc=applySelfBuffPenalty(perc);
        }
        if (perc != 100) {
            getAction().getOwnerObj().multiplyParamByPercent(PARAMS.SPELLPOWER, MathMaster.getFullPercent(perc),
                    false);
        }

    }

    private Integer applySelfBuffPenalty(Integer perc) {
        if (getEntity().checkBool(GenericEnums.STD_BOOLS.BUFFING)) {
            Integer mod = getEntity().getOwnerUnit().getIntParam(PARAMS.SELF_BUFF_MOD);
            if (mod ==0) {
                getGame().getLogManager().log(getAction().getOwnerUnit().getNameIfKnown()+ ": applying " +
                        getName() + " to self incurs " +
                        (100-mod) + "% Spellpower penalty!");
                mod = Integer.valueOf(PARAMS.SELF_BUFF_MOD.getDefaultValue());
            }
            return perc*mod/100;
        }
        return perc;
    }
}
