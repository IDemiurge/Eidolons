package eidolons.game.battlecraft.rules.counter.negative;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.battlecraft.rules.counter.generic.timed.TimedRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.MetaEnums;
import main.system.math.Formula;

/*
 * 
 Poison damage equal to x% of endurance? 
 stamina reduction
 Retain disease on cells and corpses and spread it 
 * 
 * 
 */
public class DiseaseRule extends DamageCounterRule  implements TimedRule {

    private static final String ENDURANCE_DAMAGE_PERC = "0.5";
    private static final int REDUCTION = 0;
    private static final int INCREASE = 0;
    private static final String ATB_PER_COUNTER = "(-3)";

    public DiseaseRule(DC_Game game) {
        super(game);
    }

    @Override
    public boolean check(BattleFieldObject unit) {
        // TODO Auto-generated method stub
        return super.check(unit);
    }

    @Override
    public DAMAGE_TYPE getDamageType() {
        return GenericEnums.DAMAGE_TYPE.POISON;
    }

    @Override
    protected Effect getEffect() {
        // STD_BOOLS.CONTAGIOUS
        return null;
    }

    @Override
    protected Effect getSpecialRoundEffects() {
        // spread effects could go here! :)
        ModifyValueEffect modifyValueEffect = new ModifyValueEffect(PARAMS.C_ATB,
         MOD.MODIFY_BY_CONST, getCounterRef() + "*" + ATB_PER_COUNTER);
        modifyValueEffect.setMin_max_formula(new Formula("0"));
        return new Effects(modifyValueEffect);
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


    // @Override
    // public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
    // if (checkVirulent(unit))
    // return INCREASE;
    // return REDUCTION;
    // }
    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        int n = 1 + unit.getIntParam(PARAMS.FORTITUDE) * unit.getIntParam(PARAMS.POISON_RESISTANCE)
         / 100;
        if (checkVirulent(unit)) {
            n -= 10;
        }
        return n;
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Disease;
    }

    private boolean checkVirulent(BattleFieldObject unit) {
        return unit.checkStatus(UnitEnums.STATUS.VIRULENT);
    }

    @Override
    public String getBuffName() {
        return MetaEnums.STD_BUFF_NAME.Contaminated.getName();
    }

    @Override
    public STATUS getStatus() {
        return UnitEnums.STATUS.CONTAMINATED;
    }


}
