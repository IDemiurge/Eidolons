package main.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.*;
import main.content.Constants;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;

public class PoisonRule extends DamageCounterRule {

    private static final String DAMAGE_PER_COUNTER = "1";
    private static final String HALLUNICATE = STD_BUFF_NAMES.Hallucinogetic_Poison.getName();
    private static final String HALLUNICATE_FOCUS_PER_COUNTER = "(-0.2)";
    private static final String WEAKEN = STD_BUFF_NAMES.Weakening_Poison.getName();
    private static final String WEAKEN_STAMINA_PER_COUNTER = Constants.WEAKEN_STAMINA_PER_COUNTER;
    private static final String PARALYZING = STD_BUFF_NAMES.Paralyzing_Poison.getName();
    private static final String PARALYZING_INITIATIVE_PER_COUNTER = "(-2.5)";
    private static final Integer REDUCTION_FACTOR = 5;

    public PoisonRule(DC_Game game) {
        super(game);
    }

    public boolean checkApplies(DC_HeroObj unit) {
        return unit.isLiving();
    }

    @Override
    protected Effect getSpecialRoundEffects() {
        Effects specialRoundEffects = new Effects();
        if (unit.getBuff(HALLUNICATE) != null) {
            specialRoundEffects.add(new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_CONST,
                    getNumberOfCounters(unit) + " * " + HALLUNICATE_FOCUS_PER_COUNTER));
        }
        if (unit.getBuff(WEAKEN) != null) {
            specialRoundEffects.add(new ModifyValueEffect(PARAMS.C_STAMINA, MOD.MODIFY_BY_CONST,
                    getNumberOfCounters(unit) + " * " + WEAKEN_STAMINA_PER_COUNTER));
        }
        if (unit.getBuff(PARALYZING) != null) {
            specialRoundEffects.add(new ModifyValueEffect(PARAMS.C_INITIATIVE_BONUS,
                    MOD.MODIFY_BY_CONST, getNumberOfCounters(unit) + " * "
                    + PARALYZING_INITIATIVE_PER_COUNTER));
        }
        return specialRoundEffects;
    }

    @Override
    public DAMAGE_TYPE getDamageType() {
        return DAMAGE_TYPE.POISON;
    }

    @Override
    public String getDamagePerCounterFormula() {
        return DAMAGE_PER_COUNTER;
    }

    @Override
    public boolean isEnduranceOnly() {
        return true;
    }

    @Override
    public String getCounterName() {
        return STD_COUNTERS.Poison_Counter.toString();
    }

    @Override
    public String getBuffName() {
        if (unit.checkPassive(STANDARD_PASSIVES.WEAKENING_POISON)) {
            return STD_BUFF_NAMES.Weakening_Poison.toString();
        }
        if (unit.checkPassive(STANDARD_PASSIVES.HALLUCINOGETIC_POISON)) {
            return STD_BUFF_NAMES.Hallucinogetic_Poison.toString();
        }
        return STD_BUFF_NAMES.Poison.toString();
    }

    @Override
    public STATUS getStatus() {
        return STATUS.POISONED;
    }

    @Override
    protected Effect getEffect() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
        return 1 + unit.getIntParam(PARAMS.FORTITUDE) * unit.getIntParam(PARAMS.POISON_RESISTANCE)
                / 100;
    }

    @Override
    public String getImage() {
        return "mini\\spell\\Death\\Poison.jpg";
    }

}
