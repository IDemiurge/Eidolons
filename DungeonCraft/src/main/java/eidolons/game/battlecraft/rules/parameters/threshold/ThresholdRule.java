package eidolons.game.battlecraft.rules.parameters.threshold;

import eidolons.entity.unit.Unit;
import eidolons.system.math.roll.Dice;
import eidolons.system.math.roll.DiceMaster;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Essence loss At 20%: 1d4 Dismay, 1d4 Fear, 1d4 Haunted At 10%: 1d6 Dismay, 1d6 Fear, 1d6 Haunted
 * <p>
 * Each threshold can only trigger once per combat. If starts combat below threshold, effect applies immediately.
 * <p>
 * Endurance loss Endurance – Bleeding/Wounds/Dismay/Fear At 50%: 1d6 Bleeding, 1d6 Fear At 30%: 1d6 Dismay, 1d6
 * Bleeding, 1d6 Wounds At 10%: 2d4 Adrenaline, 2d6 Wounds
 * <p>
 * Each threshold can only trigger once per combat. If starts combat below threshold, effect applies immediately.
 * <p>
 * Toughness loss At 50%: 1d4 Pain, 1d4 Fatigue At 30%: 1d4 Pain, 1d4 Bleeding At 10%: 1d4 Adrenaline, 1d6 Bleeding
 * <p>
 * Each threshold can only trigger once after Toughness Reset (can’t trigger if disabled) Focus Growth At 50: 1d4
 * Adrenaline, 1d4 Energy, 1d4 Stillness At 75: 1d6 Adrenaline, 1d6 Energy, 1d6 Stillness At 100: 1 Killer/Ecstasy/Zen
 * counter, randomly
 * <p>
 * Each threshold can only trigger once after Focus Reset (can’t trigger if disabled)
 * <p>
 * Focus loss At 20: 1d4 Haze At 10: 1d4 Haze, 1d4 Concussion
 * <p>
 * Each threshold can only trigger once after Toughness Reset (can’t trigger if disabled)
 * <p>
 * If Base Focus is below a Threshold, will start the round with a Focus Loss effect!
 */
public abstract class ThresholdRule {
    /*
     * record threshold being passed via special property? Or just use Mark/... prop container? it is supposed to be
     * combat-scoped only why not create an instance per unit and keep it in normal vars?!
     */
    final List<Integer> thresholdsPassed = new ArrayList<>();
    final Unit unit;

    public ThresholdRule(Unit unit) {
        this.unit = unit;
    }

    protected abstract boolean isRoundBased();

    protected int getThreshold() {
        int perc = unit.getParamPercentage(getBaseParam());
        int n = -1;
        for (int i : getThresholdsDown()) {
            if (perc <= i) {
                return n;
            }
            n--;
        }
        n = 1;
        for (int i : getThresholdsUp()) {
            if (perc >= i) {
                return n;
            }
            n++;
        }
        return 0;
    }

    protected abstract PARAMETER getBaseParam();

    protected abstract int[] getThresholdsDown();

    protected abstract int[] getThresholdsUp();

    protected abstract Map<UnitEnums.COUNTER, Dice> getCountersMapForThreshold(int n);

    protected void update() {
        int threshold = getThreshold();
        if (threshold==0)
            return;
        if (thresholdsPassed.contains(threshold))
            return;
        applyThreshold(threshold);

    }

    //upon value reset!
    protected void resetThresholds() {
        thresholdsPassed.clear();
    }

    protected void applyThreshold(int n) {
        Map<UnitEnums.COUNTER, Dice> map = getCountersMapForThreshold(n);

        for (UnitEnums.COUNTER counter : map.keySet()) {
            Dice dice = map.get(counter);
            int amount = DiceMaster.roll(dice, unit, true);
            //TODO fix up counters FGS!
            // unit.addCounter(counter, amount);
        }
        thresholdsPassed.add(n);
    }


}
