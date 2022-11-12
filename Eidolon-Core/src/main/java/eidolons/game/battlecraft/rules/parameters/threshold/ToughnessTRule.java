package eidolons.game.battlecraft.rules.parameters.threshold;

import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import eidolons.system.math.roll.Dice;
import main.content.enums.entity.EffectEnums;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.data.MapBuilder;

import java.util.Map;

import static main.content.enums.GenericEnums.DieType.*;
import static main.content.enums.entity.EffectEnums.COUNTER.*;

/**
 * Toughness loss
 * At 50%: 1d4 Pain, 1d4 Fatigue
 * At 30%: 1d4 Pain, 1d4 Bleeding
 * At 10%: 1d4 Adrenaline, 1d6 Bleeding
 */
public class ToughnessTRule extends ThresholdRule {
    public ToughnessTRule(Unit unit) {
        super(unit);
    }

    @Override
    protected boolean isRoundBased() {
        return true;
    }

        @Override
    protected PARAMETER getBaseParam() {
        return PARAMS.TOUGHNESS;
    }

    @Override
    protected int[] getThresholdsDown() {
            return new int[]{
                     10, 30, 50
            };
    }

    @Override
    protected int[] getThresholdsUp() {
        return new int[0];
    }


    @Override
    protected Map<EffectEnums.COUNTER, Dice> getCountersMapForThreshold(int n) {
        switch (n){
            case 0:
                return  new MapBuilder<EffectEnums.COUNTER, Dice>().keys(Pain, Fatigue).
                        values(new Dice(1, d4), new Dice(1, d4)).build();
            case 1:
                return  new MapBuilder<EffectEnums.COUNTER, Dice>().keys(Pain, Bleeding).
                        values(new Dice(1, d4), new Dice(1, d4)).build();
            case 2:
                return  new MapBuilder<EffectEnums.COUNTER, Dice>().keys(Adrenaline, Bleeding).
                        values(new Dice(1, d6), new Dice(1, d6)).build();
        }
        return null;
    }
}
