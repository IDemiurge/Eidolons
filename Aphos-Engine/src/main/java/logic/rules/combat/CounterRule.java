package logic.rules.combat;

import content.LinkedStringMap;
import elements.stats.Counter;
import elements.stats.UnitParam;
import framework.data.DataManager;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;
import system.consts.StatUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 10/25/2023
 */
public class CounterRule {
    public static final Map<String, Counter> dictionary = new LinkedStringMap<>();
    private static final Map<Counter, Map<UnitParam, Integer>> modMaps = new HashMap<>();

    static {
        for (Counter counter : Counter.values()) {
            dictionary.put(counter.name(), counter);
            String str = counter.mods();
            if (!str.contains(";"))
                continue;
            Map<String, Object> mods = DataManager.stringArrayToMap(str.split(";"));
            Map<UnitParam, Integer> map = new HashMap<>();
            mods.forEach((s, i) -> map.put(StatUtils.getParam(s), Integer.valueOf(i.toString())));
            modMaps.put(counter, map);
        }
    }


    public static void roundEnd(Map<Counter, Integer> map, FieldEntity entity) {
        // ModificationResult result;
        //special procedures for Stun and other counters? "Absorption" counters!
        //transform into enegry into saved?
        for (Counter counter : map.keySet()) {
            Integer value = map.get(counter);

            enact(counter, value, entity);


            Integer modification = getModification(counter, value, entity);
            if (modification != 0) {
                if (modification < 0)
                    if (value <= -modification) {
                        map.remove(counter);
                        continue;
                    }
                map.put(counter, value + modification);
            }
        }
    }

    private static void enact(Counter counter, Integer value, FieldEntity entity) {
        //what kind of periodic damage? Wards don't work against it?
    }

    private static Integer getModification(Counter counter, Integer value, FieldEntity entity) {
        return 0;
    }

    public static void apply(FieldEntity entity, Counter counter, Integer amount) {
        Map<UnitParam, Integer> modMap = modMaps.get(counter);
        if (modMap == null)
            return;
        for (UnitParam param : modMap.keySet()) {
            Integer base = modMap.get(param);
            entity.modifyValue(param, base * amount);
            //TODO 10->1 rule?
        }
    }


}
