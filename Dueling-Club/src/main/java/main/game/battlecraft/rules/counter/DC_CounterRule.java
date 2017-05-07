package main.game.battlecraft.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.attachment.AddBuffEffect;
import main.ability.effects.common.AddStatusEffect;
import main.ability.effects.continuous.ContinuousEffect;
import main.ability.effects.continuous.CustomTargetEffect;
import main.ability.effects.oneshot.mechanic.ModifyCounterEffect;
import main.ability.targeting.TemplateAutoTargeting;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.data.filesys.PathFinder;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.battlecraft.rules.magic.ImmunityRule;
import main.system.auxiliary.log.LogMaster.LOG;

import java.util.HashMap;
import java.util.Map;

/**
 * Apply buff depending on the number of counters
 *
 * @author JustMe
 */

public abstract class DC_CounterRule {

    protected Effects effects;
    protected DC_Game game;
    // protected Map<Entity, BuffObj> buffCache = new HashMap<Entity,
    // BuffObj>();
    protected Unit unit;
    protected Map<Unit, AddBuffEffect> effectCache;
    Map<BattleFieldObject, Effects> effectsCache = new HashMap<>();

    public DC_CounterRule(DC_Game game) {
        this.game = game;
    }

    public String getCounterName() {
        return getCounter().getName();
    }

    public COUNTER getCounter() {
        return null;
    }

    public abstract int getCounterNumberReductionPerTurn(Unit unit);

    public int getMaxNumberOfCounters(Unit unit) {
        return Integer.MAX_VALUE;
    }

    public abstract String getBuffName();

    public final String getEmitterPath() {
        return PathFinder.getSfxPath() + "counters\\" + getCounterName();
    }

    public void initEffects() {
        effects = new Effects();
        STATUS status = getStatus();
        if (status != null) {
            effects.add(new AddStatusEffect(status.toString()));
        }
        Effect e = getEffect();
        if (e != null) {
            effects.add(e);
        }

        effects.setForcedLayer(getEffectLayer());
    }

    protected Integer getEffectLayer() {
        return Effect.BUFF_RULE;
    }

    protected abstract Effect getEffect();

    protected String getCounterRef() {
        return "{" + getObjRef() + "_"
         // + StringMaster.COUNTER_CHAR
         + getCounterName() + "}";
    }

    protected String getObjRef() {
        return "Source";
    }

    protected Effect getSpecialRoundEffects() {
        return null;
    }

    protected void log(String string) {
        game.getLogManager().log(LOG.GAME_INFO, string);
    }

    // only first time
    protected String getAppliedLogString() {
        return getName() + " applied on " + unit.getName();
    }

    protected String getLiftedLogString() {
        return getName() + " lifted from " + unit.getName();
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    protected String getCounterModifiedLogString(int counterMod) {
        return unit.getName() + "'s " + getCounterName()
         + " number modified by " + counterMod;
    }

    public void newTurn() {
        for (Unit unit : game.getUnits()) {
            if (getNumberOfCounters(unit) <= 0) {
                continue;
            }
            applyCountersInteractions(unit);
            applyCountersConversions(unit);
            applyCountersTranfers(unit);
            // log TODO spread
            int counterMod = getCounterNumberReductionPerTurn(unit);
            if (counterMod != 0) {
                log(getCounterModifiedLogString(counterMod));
                unit.modifyCounter(getCounterName(), -counterMod);
            }
            Effect oneshotEffects = getSpecialRoundEffects();
            if (oneshotEffects != null) {
                oneshotEffects.apply(Ref.getSelfTargetingRefCopy(unit));
            }
        }
    }

    public boolean checkApplies(Unit unit) {
        return true;
    }

    public boolean check(Unit unit) {
        if (!checkApplies(unit)) {
            return false;
        }
        this.unit = unit;
        if (getNumberOfCounters(unit) <= 0) {
            if (!isAppliedAlways()) {
                // if (checkAlreadyApplied(unit)) TODO that's bullshit!
                // log(getLiftedLogString());
//                removeBuff(unit); // may not be needed now
                removeEffects(unit);
                return false;
            }
        }
        if (ImmunityRule.checkImmune(unit, getCounterName())) {
            return false;
        }

        // if (!checkAlreadyApplied(unit))
        // log(getAppliedLogString());

        Ref ref = unit.getRef().getCopy();
        ref.setTarget(unit.getId());
        initEffects();
        addBuff(unit);

        if (getSpread() != null) {
            new CustomTargetEffect(new TemplateAutoTargeting(
             AUTO_TARGETING_TEMPLATES.ADJACENT),
             new ModifyCounterEffect(getCounterName(),
              MOD.MODIFY_BY_CONST,

              getSpread())).apply(Ref
             .getSelfTargetingRefCopy(unit));
        }

        // TODO ++ APPLY THRU to cells!

        // TODO perhaps I should *BUILD THE LOG STRING* up to this point and log
        // it only once!
        return true;
        // effects.apply(ref);

    }

    protected boolean checkAlreadyApplied(Unit unit) {
        return unit.getBuff(getBuffName()) != null;
    }

    protected boolean isAppliedAlways() {
        // for some weirdo rules unique to a certain battlefield
        return false;
    }

    protected String getSpread() {
        return null;
    }

    protected void applyCountersTranfers(Unit unit) {
    }

    protected void applyCountersConversions(Unit unit) {
    }

    protected void applyCountersInteractions(Unit unit) {

//        if (getClashingCounter() != null) {
//            int c = unit.getCounter(getClashingCounter());
//            unit.modifyCounter(getClashingCounter(), -c);
//            unit.modifyCounter(getCounterName(), -c);
//        }
    }

    public abstract STATUS getStatus();

    protected Integer getNumberOfCounters(Unit unit) {
        return Math.min(getMaxNumberOfCounters(unit),
         unit.getCounter(getCounterName()));
        // return unit.getCounter(getCounterName());
    }

    protected void removeBuff(Unit unit) {
        unit.removeBuff(getBuffName());
    }

    protected void removeEffects(Unit unit) {
        Effect effects = getWrappedEffects(unit);
        effects.remove();
    }

    protected void applyEffects(Unit unit) {
        Effect effects = getWrappedEffects(unit);
        effects.apply(Ref.getSelfTargetingRefCopy(unit));
        if (effects instanceof AddBuffEffect){
            ((AddBuffEffect) effects).getBuff().setCounterRef(getCounterName());
        }
        //TODO animation?
//        startContinuousAnimation();
//        playAddAnimation();
//        playIntensifyAnimation();
//        playTickAnimation();
    }

    private Effect getWrappedEffects(Unit unit) {
        if (getEffect()==null )
            return new Effects() ;
        if (getBuffName() != null) {
            return getBuffEffect();
        }
        Effects effect = effectsCache.get(unit);
        if (effect == null) {
            effect = new Effects(ContinuousEffect.transformEffectToContinuous(getEffect()));
            effectsCache.put(unit, effect);
        }
        return effect;
    }

    private Effect getBuffEffect() {
        AddBuffEffect effect = getEffectCache().get(unit);
        if (effect == null) {
            effect = new AddBuffEffect(getBuffName(), effects);
            if (isUseBuffCache()) {
                getEffectCache().put(unit, effect);
            }
        }
        return effect;
    }

    protected void addBuff(Unit unit) {
        applyEffects(unit);
//        if (unit.hasBuff(getBuffName())) {
//            unit.removeBuff(getBuffName());
//        }
//        AddBuffEffect effect = null;
//        if (isUseBuffCache()) {
//            effect = getEffectCache().get(unit); // buff effect cache!
//        }
//        if (effect == null) {
//            // Ref REF = new Ref(unit.getGame(), unit.getId());
//            // REF.setTarget(unit.getId());
//            // REF.setBasis(unit.getId());
//            // buff = new DC_BuffObj(DataManager.getType(getBuffName(),
//            // OBJ_TYPES.BUFFS), Player.NEUTRAL, game, REF, effects,
//            // ContentManager.INFINITE_VALUE, null);
//            // buff.setCounterRef(getCounterName());
//            effect = new AddBuffEffect(getBuffName(), effects);
//            if (isUseBuffCache()) {
//                getEffectCache().put(unit, effect);
//            }
//            // else getUnitList().add(unit); // for logging!
//
//            // disappear
//            // tooltip should update number of counters
//            // could status also have the number?
//        }
//
//        effect.apply(Ref.getSelfTargetingRefCopy(unit)); // retain/duration? has
//        // to
//        effect.getBuff().setCounterRef(getCounterName());

    }

    protected boolean isUseBuffCache() {
        return true;
    }

    public Map<Unit, AddBuffEffect> getEffectCache() {
        if (effectCache == null) {
            effectCache = new HashMap<>();
        }
        return effectCache;
    }

}
