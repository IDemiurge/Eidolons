package framework.sim;

import eidolons.content.DC_Formulas;
import framework.TestMathUtils;
import framework.TestUtils;
import main.content.enums.GenericEnums;

public class AtkAccuracySim extends MathSim<Integer> {

    @Override
    public Integer evaluate() {
        // TestUtils.setChaosLevel (getVarInt("Chaos Level"));
        int base = DC_Formulas.calculateAccuracyRating(getVarInt("defense"), getVarInt("attack"));
        int dieResult = TestMathUtils.dieRoll(min_max_average, getDice(), getDiceType());

        return base + dieResult;

    }

    @Override
    protected GenericEnums.DieType getDiceType() {
        return GenericEnums.DieType.d20;
    }

    @Override
    protected Integer getDice() {
        return 1;
    }
}
