package apps;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/1/2023
 */
public class Consts {
    public static final String[] UNITS = {""};
    public static Map<String, JOption.PickType> attackFactors;

    static {
        attackFactors = new LinkedHashMap<>();
        // attackFactors.put("", JOption.PickType.Counters)
        // attackFactors.put("", JOption.PickType.Wounds)
        // attackFactors.put("", JOption.PickType.Omen_Grade)
    }
}
