package logic.v2.ai.sim;

import logic.v2.ai.generic.AiConsts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 1/21/2023
 */
public class SimParams {

    private final Map<String, String> namedArgs;
    private List<String> units;

    public SimParams(Map<String, String> namedArgs) {
        this.namedArgs = namedArgs;
        init();
    }

    private void init() {
        units = new ArrayList<>(Arrays.asList(namedArgs.get(AiConsts.p_units).split("|")));
    }


    public List<String> getUnits() {
        return units;
    }
}
