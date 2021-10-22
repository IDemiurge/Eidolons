package eidolons.game.battlecraft.rules.counter.dmg;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.battlecraft.rules.counter.generic.timed.TimedRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.consts.Constants;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.MetaEnums;

public class PoisonRule extends DamageCounterRule implements TimedRule{

    private static final String DAMAGE_PER_COUNTER = "1";
    private static final String HALLUNICATE = MetaEnums.STD_BUFF_NAME.Hallucinogetic_Poison.getName();
    private static final String HALLUNICATE_FOCUS_PER_COUNTER = "(-0.2)";
    private static final String WEAKEN = MetaEnums.STD_BUFF_NAME.Weakening_Poison.getName();
    private static final String WEAKEN_TOUGHNESS_PER_COUNTER = Constants.WEAKEN_STAMINA_PER_COUNTER;
    private static final String PARALYZING = MetaEnums.STD_BUFF_NAME.Paralyzing_Poison.getName();
    private static final String PARALYZING_INITIATIVE_PER_COUNTER = "(-2.5)";
    private static final Integer REDUCTION_FACTOR = 5;

    public PoisonRule(DC_Game game) {
        super(game);
    }

    public boolean checkApplies(BattleFieldObject unit) {
        if ((unit instanceof Unit)) {
            if (!((Unit) unit).isLiving()) {
                return false;
            }
        }
        return super.checkApplies(unit);
    }

    @Override
    protected Effect getSpecialRoundEffects() {
        Effects specialRoundEffects = new Effects();
        if (object.getBuff(HALLUNICATE) != null) {
            specialRoundEffects.add(new ModifyValueEffect(PARAMS.C_FOCUS, MOD.MODIFY_BY_CONST,
             getNumberOfCounters(object) + " * " + HALLUNICATE_FOCUS_PER_COUNTER));
        }
        if (object.getBuff(WEAKEN) != null) {
            specialRoundEffects.add(new ModifyValueEffect(PARAMS.C_TOUGHNESS, MOD.MODIFY_BY_CONST,
             getNumberOfCounters(object) + " * " + WEAKEN_TOUGHNESS_PER_COUNTER));
        }
        //TODO paralyzing poisan?!
        return specialRoundEffects;
    }

    @Override
    public DAMAGE_TYPE getDamageType() {
        return GenericEnums.DAMAGE_TYPE.POISON;
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
    public COUNTER getCounter() {
        return COUNTER.Poison;
    }

    @Override
    public String getBuffName() {
        if (object.checkPassive(UnitEnums.STANDARD_PASSIVES.WEAKENING_POISON)) {
            return MetaEnums.STD_BUFF_NAME.Weakening_Poison.toString();
        }
        if (object.checkPassive(UnitEnums.STANDARD_PASSIVES.HALLUCINOGETIC_POISON)) {
            return MetaEnums.STD_BUFF_NAME.Hallucinogetic_Poison.toString();
        }
        return MetaEnums.STD_BUFF_NAME.Poison.toString();
    }

    @Override
    public STATUS getStatus() {
        return UnitEnums.STATUS.POISONED;
    }

    @Override
    protected Effect getEffect() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        return 1 + unit.getIntParam(PARAMS.MIGHT) * unit.getIntParam(PARAMS.POISON_RESISTANCE)
         / 100;
    }
 
}
