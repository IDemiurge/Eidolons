package main.rules.counter;

import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.system.MetaEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.system.auxiliary.StringMaster;

public class BleedingCounterRule extends DamageCounterRule {

    private static final String PERC_PER_COUNTER = "1";

    public BleedingCounterRule(DC_Game game) {
        super(game);
    }

    @Override
    public String getCounterName() {
        return UnitEnums.STD_COUNTERS.Bleeding_Counter.toString();
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
    public int getMaxNumberOfCounters(Unit unit) {
        return 50;
    }

    @Override
    public boolean isEnduranceOnly() {
        return true;
    }

    @Override
    public String getBuffName() {
        return MetaEnums.STD_BUFF_NAMES.Bleeding.toString();
    }

    @Override
    public STATUS getStatus() {
        return UnitEnums.STATUS.BLEEDING;
    }

    @Override
    public int getCounterNumberReductionPerTurn(Unit unit) {
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

    @Override
    public String getImage() {
        return "mini\\spell\\Death\\Ability_CriticalStrike.jpg";
    }

}
