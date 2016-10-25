package main.rules;

import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.IMMUNITIES;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.PARAMS;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_UnitObj;
import main.game.MicroGame;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.game.player.Player;
import main.system.ai.tools.ParamAnalyzer;
import main.system.math.PositionMaster;

public class MoraleKillingRule extends DC_RuleImpl {
    final String FORMULA = "{SOURCE_POWER}/3";
    // "{SOURCE_LEVEL}*{SOURCE_LEVEL}";
    private ModifyValueEffect boostEffect = new ModifyValueEffect(PARAMS.C_MORALE,
            MOD.MODIFY_BY_CONST, FORMULA);
    private Conditions reduction_conditions = new Conditions();

    private ModifyValueEffect reductionEffect = new ModifyValueEffect(PARAMS.C_MORALE,
            MOD.MODIFY_BY_CONST, FORMULA);

    private Conditions boost_conditions = new Conditions();

    public MoraleKillingRule(MicroGame game) {
        super(game);
        boostEffect.setValueOverMax(true);
        reductionEffect.setFormula(boostEffect.getFormula().getNegative());
        boostEffect.setIrresistible(true);
        reductionEffect.setIrresistible(true);
    }

    @Override
    public void apply(Ref ref) {
        unitDied((DC_UnitObj) ref.getSourceObj(), ref.getAnimationActive());
    }

    public void unitDied(DC_UnitObj unit, ActiveObj animationActive) {
        if (unit.getOwner() == Player.NEUTRAL)
            return;
        boostEffect.setAnimationActive(animationActive);
        reductionEffect.setAnimationActive(animationActive);
        for (DC_HeroObj u : unit.getGame().getUnits()) {
            if (u.getOwner() == Player.NEUTRAL)
                continue;
            // if (!u.isLiving()) continue;
            if (ParamAnalyzer.isMoraleIgnore(u))
                continue;
            if (u.checkPassive(STANDARD_PASSIVES.DISPASSIONATE)
                    || u.checkPassive(STANDARD_PASSIVES.COLD_BLOODED)
                    || u.checkPassive(STANDARD_PASSIVES.FEARLESS))

                continue;
            if (PositionMaster.getDistance(u, unit) > 1)
                if (u != unit.getRef().getObj(KEYS.KILLER))
                    continue;

            Ref ref = Ref.getSelfTargetingRefCopy(u);
            if (u.getOwner() == unit.getOwner()) {
                if (!u.checkImmunity(IMMUNITIES.MORALE_REDUCTION_KILL))
                    if (!u.checkImmunity(IMMUNITIES.MORALE_REDUCTION))
                        if (!u.checkImmunity(IMMUNITIES.MIND_AFFECTION))
                            reductionEffect.apply(ref);
            } else {
                if (!u.checkPassive(STANDARD_PASSIVES.DISPASSIONATE))
                    boostEffect.apply(ref);
            }
        }
        // Ref ref = Ref.getCopy(unit.getRef()); OLD
        // enact(reduction_conditions, reduction_effect, false, ref);
        // enact(boost_conditions, boost_effect, true, ref);

    }

    @Override
    public void initEffects() {
        effects = new Effects(boostEffect, reductionEffect);
    }

    @Override
    public void initConditions() {
        conditions = new Conditions(reduction_conditions, boost_conditions);

    }

    // new ConditionalEffect(new NotCondition(
    // new OrConditions(new PropCondition(G_PROPS.IMMUNITIES,
    // IMMUNITIES.MIND_AFFECTION), new PropCondition(
    // G_PROPS.IMMUNITIES, IMMUNITIES.MORALE_REDUCTION),
    // new PropCondition(G_PROPS.IMMUNITIES,
    // // IMMUNITIES.MORALE_REDUCTION_KILL))),
    // new ConditionalEffect(new NotCondition(
    // new OrConditions(new PropCondition(G_PROPS.STANDARD_PASSIVES,
    // STANDARD_PASSIVES.DISPASSIONATE))), )
    @Override
    public void initEventType() {
        event_type = STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED;

    }

}
