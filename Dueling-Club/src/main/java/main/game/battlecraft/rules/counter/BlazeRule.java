package main.game.battlecraft.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.continuous.CustomTargetEffect;
import main.ability.effects.common.ModifyValueEffect;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.MetaEnums;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;

public class BlazeRule extends DamageCounterRule {

    private static final Integer THRESHOLD = 10;
    private static final int REDUCTION = -2;
    private static final int INCREASE = 2;
    private static final String DAMAGE_PER_COUNTER = "3";
    private static final String DURABILITY_PER_COUNTER = "(-0.25)";

    public BlazeRule(DC_Game game) {
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
        return null;
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
//        if (getNumberOfCounters(unit) <= THRESHOLD) {
//            return true;
//        }
        return false;
    }


    @Override
    public String getCounterName() {
        return COUNTER.Blaze.getName();

    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
        if (checkAblaze(unit)) {
            return INCREASE;
        }
        return REDUCTION;
    }

    private boolean checkAblaze(Unit unit) {
        return getNumberOfCounters(unit) >= THRESHOLD;
    }

    @Override
    public String getBuffName() { // different while burning slow?
        return MetaEnums.STD_BUFF_NAMES.Ablaze.getName();
    }

    @Override
    public STATUS getStatus() {
        if (checkAblaze(unit)) {
            return UnitEnums.STATUS.ABLAZE; // panic? spreading?
        }
        return null;
    }

}
