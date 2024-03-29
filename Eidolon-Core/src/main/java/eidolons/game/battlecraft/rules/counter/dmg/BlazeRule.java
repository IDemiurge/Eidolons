package eidolons.game.battlecraft.rules.counter.dmg;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.battlecraft.rules.counter.generic.timed.TimedRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.continuous.CustomTargetEffect;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.EffectEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.EffectEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.MetaEnums;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;

public class BlazeRule extends DamageCounterRule implements TimedRule{

    private static final Integer THRESHOLD = 25;
    private static final int REDUCTION =  3;
    private static final int INCREASE =  1;
    private static final String DAMAGE_PER_COUNTER = "1";
    private static final String DURABILITY_PER_COUNTER = "(-0.25)";

    public BlazeRule(DC_Game game) {
        super(game);
    }

    protected Effect getSpecialRoundEffects() {

        return new Effects(new CustomTargetEffect(
         new FixedTargeting(KEYS.ARMOR), new ModifyValueEffect(
         PARAMS.C_DURABILITY, MOD.MODIFY_BY_CONST,
         getCounterRef() + "*" + DURABILITY_PER_COUNTER)));

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
    public COUNTER getCounter() {
        return EffectEnums.COUNTER.Blaze;
    }

    @Override
    public boolean isEnduranceOnly() {
//        if (getNumberOfCounters(unit) <= THRESHOLD) {
//            return true;
//        }
        return false;
    }


    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        if (unit.isIndestructible())
            return getNumberOfCounters(unit);
        if (checkAblaze(unit)) {
            return INCREASE;
        }
        return REDUCTION;
    }

    private boolean checkAblaze(BattleFieldObject unit) {
        return getNumberOfCounters(unit) >= THRESHOLD;
    }

    @Override
    public String getBuffName() { // different while burning slow?
        return MetaEnums.STD_BUFF_NAME.Ablaze.getName();
    }

    @Override
    public STATUS getStatus() {
        if (checkAblaze(object)) {
            return UnitEnums.STATUS.ABLAZE; // panic? spreading?
        }
        return null;
    }

}
