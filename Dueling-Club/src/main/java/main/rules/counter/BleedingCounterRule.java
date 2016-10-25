package main.rules.counter;

import main.ability.effects.Effect;
import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.CONTENT_CONSTS.STD_BUFF_NAMES;
import main.content.CONTENT_CONSTS.STD_COUNTERS;
import main.content.PARAMS;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.system.auxiliary.StringMaster;

public class BleedingCounterRule extends DamageCounterRule {

    private static final String PERC_PER_COUNTER = "1";

    public BleedingCounterRule(DC_Game game) {
        super(game);
    }

    @Override
    public String getCounterName() {
        return STD_COUNTERS.Bleeding_Counter.toString();
    }

    @Override
    public DAMAGE_TYPE getDamageType() {
        return DAMAGE_TYPE.PURE;
    }

    @Override
    public String getDamagePerCounterFormula() {
        return "" + StringMaster.getValueRef(KEYS.TARGET, PARAMS.ENDURANCE) + "*"
                + PERC_PER_COUNTER + "/100";
    }

    @Override
    public int getMaxNumberOfCounters(DC_HeroObj unit) {
        return 50;
    }

    @Override
    public boolean isEnduranceOnly() {
        return true;
    }

    @Override
    public String getBuffName() {
        return STD_BUFF_NAMES.Bleeding.toString();
    }

    @Override
    public STATUS getStatus() {
        return STATUS.BLEEDING;
    }

    @Override
    public int getCounterNumberReductionPerTurn(DC_HeroObj unit) {
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
