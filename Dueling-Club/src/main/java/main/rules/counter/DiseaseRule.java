package main.rules.counter;

import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.CONTENT_CONSTS.STD_BUFF_NAMES;
import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.system.math.Formula;

/*
 * 
 Poison damage equal to x% of endurance? 
 stamina reduction
 Retain disease on cells and corpses and spread it 
 * 
 * 
 */
public class DiseaseRule extends DamageCounterRule {

    private static final String ENDURANCE_DAMAGE_PERC = "0.5";
    private static final int REDUCTION = 0;
    private static final int INCREASE = 0;
    private static final String STAMINA_PER_COUNTER = "(-0.2)";

    public DiseaseRule(DC_Game game) {
        super(game);
    }

    @Override
    public boolean check(DC_HeroObj unit) {
        // TODO Auto-generated method stub
        return super.check(unit);
    }

    @Override
    public DAMAGE_TYPE getDamageType() {
        return DAMAGE_TYPE.POISON;
    }

    @Override
    protected Effect getEffect() {
        // STD_BOOLS.CONTAGIOUS
        return null;
    }

    @Override
    protected Effect getSpecialRoundEffects() {
        // spread effects could go here! :)
        ModifyValueEffect modifyValueEffect = new ModifyValueEffect(PARAMS.C_STAMINA,
                MOD.MODIFY_BY_CONST, getCounterRef() + "*" + STAMINA_PER_COUNTER);
        modifyValueEffect.setMin_max_formula(new Formula("0"));
        Effects effects = new Effects(modifyValueEffect);
        return effects;
    }

    @Override
    public String getDamagePerCounterFormula() {
        return "{target_c_endurance}*" + ENDURANCE_DAMAGE_PERC + "/100";
    }

    @Override
    protected boolean isUseBuffCache() {
        return false;
    }

    @Override
    public boolean isEnduranceOnly() {
        return true;
    }

    @Override
    public String getCounterName() {
        return STD_COUNTERS.Disease_Counter.getName();
    }

    // @Override
    // public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
    // if (checkVirulent(unit))
    // return INCREASE;
    // return REDUCTION;
    // }
    @Override
    public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
        int n = 1 + unit.getIntParam(PARAMS.FORTITUDE) * unit.getIntParam(PARAMS.POISON_RESISTANCE)
                / 100;
        if (checkVirulent(unit))
            n -= 10;
        return n;
    }

    private boolean checkVirulent(DC_HeroObj unit) {
        return unit.checkStatus(STATUS.VIRULENT);
    }

    @Override
    public String getBuffName() {
        return STD_BUFF_NAMES.Contaminated.getName();
    }

    @Override
    public STATUS getStatus() {
        return STATUS.CONTAMINATED;
    }

    @Override
    public String getImage() {
        return "mini\\spell\\Death\\Contagion.jpg";
    }

}
