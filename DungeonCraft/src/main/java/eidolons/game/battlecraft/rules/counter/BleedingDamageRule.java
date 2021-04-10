package eidolons.game.battlecraft.rules.counter;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.battlecraft.rules.counter.timed.TimedRule;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.MetaEnums;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;

public class BleedingDamageRule extends DamageCounterRule  implements TimedRule {

    private static final String PERC_PER_COUNTER = "1";

    public BleedingDamageRule(DC_Game game) {
        super(game);
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Bleeding;
    }


    @Override
    public DAMAGE_TYPE getDamageType() {
        return GenericEnums.DAMAGE_TYPE.PURE;
    }

    @Override
    public String getDamagePerCounterFormula() {
        return "" + StringMaster.getValueRef(KEYS.TARGET, PARAMS.ENDURANCE) + "*"
         + PERC_PER_COUNTER + "/100";
    }

    @Override
    public int getMaxNumberOfCounters(BattleFieldObject unit) {
        return 50;
    }

    @Override
    public boolean isEnduranceOnly() {
        return true;
    }

    @Override
    public String getBuffName() {
        return MetaEnums.STD_BUFF_NAME.Bleeding.toString();
    }

    @Override
    public STATUS getStatus() {
        return UnitEnums.STATUS.BLEEDING;
    }

    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        return unit.getIntParam(PARAMS.FORTITUDE);
    }

    @Override
    protected Effect getEffect() {
        return null;
    }

    @Override
    public String getSound() {
        return null;
    }


}
