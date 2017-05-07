package main.game.battlecraft.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.continuous.CustomTargetEffect;
import main.ability.effects.common.ModifyValueEffect;
import main.content.DC_ValueManager.VALUE_GROUP;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

public class CorrosionRule extends DC_CounterRule {
    private static final String DURABILITY_PER_COUNTER = "(-0.5)";
    private static final String RESIST_PER_COUNTER = "(-1)";
    private static final String ARMOR_MOD_PER_COUNTER = "(-2)";
    private static final String ARMOR_PER_COUNTER = "(-0.5)";

    // private static final String AFFECTED_RESISTANCES =
    // "Acid|Fire|Lightning|Cold";

    public CorrosionRule(DC_Game game) {
        super(game);
    }

    @Override
    public String getCounterName() {
        return COUNTER.Corrosion.getName();
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return 0;
    }

    @Override
    protected Effect getSpecialRoundEffects() {
        Effects effects = new Effects(new CustomTargetEffect(
                new FixedTargeting(KEYS.WEAPON), new ModifyValueEffect(
                PARAMS.C_DURABILITY, MOD.MODIFY_BY_CONST,
                getCounterRef() + "*" + DURABILITY_PER_COUNTER)),
                new CustomTargetEffect(new FixedTargeting(KEYS.ARMOR),
                        new ModifyValueEffect(PARAMS.C_DURABILITY,
                                MOD.MODIFY_BY_CONST, getCounterRef()
                                + "*" + DURABILITY_PER_COUNTER)));

        return effects;
    }

    @Override
    public String getBuffName() {
        return "Corroding";
    }

    @Override
    protected Effect getEffect() {
        return new Effects(new ModifyValueEffect(PARAMS.ARMOR,
                MOD.MODIFY_BY_PERCENT, getCounterRef() + "*"
                + ARMOR_MOD_PER_COUNTER),

                new ModifyValueEffect(PARAMS.ARMOR, MOD.MODIFY_BY_CONST,
                        getCounterRef() + "*" + ARMOR_PER_COUNTER),
                new ModifyValueEffect(VALUE_GROUP.ELEMENTAL_RESISTANCES
                        .toString(), MOD.MODIFY_BY_CONST,
                        getCounterRef() + "*" + RESIST_PER_COUNTER));
    }

    @Override
    public STATUS getStatus() {
        return null;
    }
}