package eidolons.game.battlecraft.rules.combat.mechanics;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.DC_UnitModel;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.ActiveObj;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.MultiEventType;
import main.system.math.PositionMaster;

public class MoraleKillingRule extends DC_RuleImpl {
    final String FORMULA = "{SOURCE_POWER}/4";
    // "{SOURCE_LEVEL}*{SOURCE_LEVEL}";
    private final ModifyValueEffect boostEffect = new ModifyValueEffect(PARAMS.C_ESSENCE,
     MOD.MODIFY_BY_CONST, FORMULA);
    private final Conditions reduction_conditions = new Conditions();

    private final ModifyValueEffect reductionEffect = new ModifyValueEffect(PARAMS.C_ESSENCE,
     MOD.MODIFY_BY_CONST, FORMULA);

    private final Conditions boost_conditions = new Conditions();

    public MoraleKillingRule(GenericGame game) {
        super(game);
        boostEffect.setValueOverMax(true);
        reductionEffect.setFormula(boostEffect.getFormula().getNegative());
        boostEffect.setIrresistible(true);
        reductionEffect.setIrresistible(true);
    }

    @Override
    public void apply(Ref ref) {
        unitDied((DC_UnitModel) ref.getSourceObj(), ref.getAnimationActive());
    }

    public void unitDied(DC_UnitModel unit, ActiveObj animationActive) {
        if (unit.getOwner() == Player.NEUTRAL) {
            return;
        }
        for (Unit u : unit.getGame().getUnits()) {
            if (u.getOwner() == Player.NEUTRAL) {
                continue;
            }
            // if (!u.isLiving()) continue;
            if (ParamAnalyzer.isMoraleIgnore(u)) {
                continue;
            }
            if (u.checkPassive(UnitEnums.STANDARD_PASSIVES.DISPASSIONATE)
             || u.checkPassive(UnitEnums.STANDARD_PASSIVES.COLD_BLOODED)
             || u.checkPassive(UnitEnums.STANDARD_PASSIVES.FEARLESS))

            {
                continue;
            }
            if (PositionMaster.getDistance(u, unit) > 1) {
                if (u != unit.getRef().getObj(KEYS.KILLER)) {
                    continue;
                }
            }

            Ref ref = Ref.getSelfTargetingRefCopy(u);
            if (u.getOwner() == unit.getOwner()) {
                if (!u.checkImmunity(UnitEnums.IMMUNITIES.MORALE_REDUCTION_KILL)) {
                    if (!u.checkImmunity(UnitEnums.IMMUNITIES.MORALE_REDUCTION)) {
                        if (!u.checkImmunity(UnitEnums.IMMUNITIES.MIND_AFFECTION)) {
                            reductionEffect.apply(ref);
                        }
                    }
                }
            } else {
                if (!u.checkPassive(UnitEnums.STANDARD_PASSIVES.DISPASSIONATE)) {
                    boostEffect.apply(ref);
                }
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
        event_type = new MultiEventType(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED,
                STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ANNIHILATED);

    }

}
