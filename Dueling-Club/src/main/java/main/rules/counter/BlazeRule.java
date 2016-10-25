package main.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.continuous.CustomTargetEffect;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.CONTENT_CONSTS.STD_BUFF_NAMES;
import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.content.PARAMS;
import main.elements.targeting.FixedTargeting;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;

public class BlazeRule extends DamageCounterRule {

    private static final Integer THRESHOLD = 10;
    private static final int REDUCTION = 2;
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
        return DAMAGE_TYPE.FIRE;
    }

    @Override
    public String getDamagePerCounterFormula() {
        return DAMAGE_PER_COUNTER;
    }

    @Override
    public boolean isEnduranceOnly() {
        if (getNumberOfCounters(unit) <= THRESHOLD)
            return true;
        return false;
    }

    @Override
    protected String getClashingCounter() {
        return STD_COUNTERS.Freeze_Counter.getName();
    }

    @Override
    public String getCounterName() {
        return STD_COUNTERS.Blaze_Counter.getName();

    }

    @Override
    public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
        if (checkAblaze(unit))
            return REDUCTION;
        return INCREASE;
    }

    private boolean checkAblaze(DC_HeroObj unit) {
        return getNumberOfCounters(unit) <= THRESHOLD;
    }

    @Override
    public String getBuffName() { // different while burning slow?
        return STD_BUFF_NAMES.Ablaze.getName();
    }

    @Override
    public STATUS getStatus() {
        if (checkAblaze(unit))
            return STATUS.ABLAZE; // panic? spreading?
        return null;
    }

    @Override
    public String getImage() {
        return "mini\\spell\\Life\\Elemental\\screen_33.jpg";
    }

}
