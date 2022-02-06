package eidolons.game.battlecraft.rules.parameters.threshold;

import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import eidolons.system.math.roll.Dice;
import main.content.enums.entity.EffectEnums;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.data.MapBuilder;

import java.util.Map;

import static main.content.enums.GenericEnums.DieType.d4;
import static main.content.enums.GenericEnums.DieType.d6;
import static main.content.enums.entity.EffectEnums.COUNTER.*;

/**
 Focus Growth
 At 50: 1d4 Adrenaline, 1d4 Energy, 1d4 Stillness
 At 75: 1d6 Adrenaline, 1d6 Energy, 1d6 Stillness
 At 100: 1 Killer/Ecstasy/Zen counter, randomly

 Each threshold can only trigger once after Focus Reset (can’t trigger if disabled)

 Focus loss
 At 20: 1d4 Haze
 At 10: 1d4 Haze, 1d4 Concussion

 Each threshold can only trigger once after Toughness Reset (can’t trigger if disabled)
 If Base Focus is below a Threshold, will start the round with a Focus Loss effect!

 */
public class FocusTRule extends ThresholdRule {
    public FocusTRule(Unit unit) {
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
                     10, 20
            };
    }

    @Override
    protected int[] getThresholdsUp() {
        return new int[]{
                100, 75, 50
        };
    }


    @Override
    protected Map<EffectEnums.COUNTER, Dice> getCountersMapForThreshold(int n) {
        switch (n){
            case 3:
            case 2:
            case 1:
                return  new MapBuilder<EffectEnums.COUNTER, Dice>().keys(Stillness, Adrenaline, Energy).
                        values(new Dice(1, d4), new Dice(1, d4)).build();
            case -1:
                return  new MapBuilder<EffectEnums.COUNTER, Dice>().keys(Haze).
                        values(new Dice(2, d4)).build();
            case -2:
                return  new MapBuilder<EffectEnums.COUNTER, Dice>().keys(Haze, Concussion).
                        values(new Dice(1, d6), new Dice(1, d6)).build();
        }
        return null;
    }
}
