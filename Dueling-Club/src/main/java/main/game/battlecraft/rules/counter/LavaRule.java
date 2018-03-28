package main.game.battlecraft.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.ability.effects.continuous.CustomTargetEffect;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 4/22/2017.
 */
public class LavaRule extends DamageCounterRule {

    private static final int REDUCTION = -1;
    private static final String DAMAGE_PER_COUNTER = "5";
    private static final String DURABILITY_PER_COUNTER = "(-0.5)";
    private static final String WEIGHT_PER_COUNTER = "1";
    private static final String RESISTANCES = "Acid|Lightning|Cold";
    private static final String RESIST_PER_COUNTER = "1";

    public LavaRule(DC_Game game) {
        super(game);
    }


    protected Effect getSpecialRoundEffects() {
        Effects effects = new Effects(new CustomTargetEffect(
         new FixedTargeting(KEYS.ARMOR), new ModifyValueEffect(
         PARAMS.C_DURABILITY, MOD.MODIFY_BY_CONST,
         getCounterRef() + "*" + DURABILITY_PER_COUNTER)));

        return effects;

        // immolation???
    }

    @Override
    protected Effect getEffect() {
        return new Effects(new ModifyValueEffect(PARAMS.WEIGHT,
         MOD.MODIFY_BY_PERCENT, getCounterRef() + "*"
         + WEIGHT_PER_COUNTER),
         new ModifyValueEffect(RESISTANCES
          , MOD.MODIFY_BY_CONST,
          getCounterRef() + "*" + RESIST_PER_COUNTER)
        );
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Lava;
    }

    @Override
    public DAMAGE_TYPE getDamageType() {
        return GenericEnums.DAMAGE_TYPE.FIRE;
    }

    @Override
    public String getDamagePerCounterFormula() {
        return DAMAGE_PER_COUNTER;
    }

    @Override
    public boolean isEnduranceOnly() {
        return false;
    }


    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        return REDUCTION;
    }

    @Override
    public String getBuffName() {
        return null;
    }

    @Override
    public STATUS getStatus() {
        return null;
    }
}
