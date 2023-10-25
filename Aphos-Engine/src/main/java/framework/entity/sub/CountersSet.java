package framework.entity.sub;

import elements.exec.EntityRef;
import elements.stats.Counter;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;
import logic.rules.combat.CounterRule;
import logic.rules.combat.WardRule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 10/25/2023
 * <p>
 * managing counters - reduction, mutual suppression,
 */
public class CountersSet {
    private FieldEntity source;
    private Map<Counter, Integer> map = new HashMap<>();

    public CountersSet(FieldEntity source) {
        this.source = source;
    }

    public void tryAdd(int amount, Counter counter, EntityRef ref) {
        if (source instanceof Unit unit)
            if (WardRule.checkWardVsCounter(counter, unit, ref))
                return;
        Integer prev = get(counter);
        // if (prev == 0)
        //     fresh = true;
        map.put(counter, prev + amount);
        // combat().event(EVENT, ref, getEventArgs(amount, counter, ref));
    }

    public void remove(int amount, Counter counter, EntityRef ref) {

    }

    public void multiply(float mod, Counter counter, EntityRef ref) {
        int toAdd = Math.round(get(counter) * mod);
        tryAdd(toAdd, counter, ref);
        // MapMaster
    }

    public Integer get(String key) {
        Counter counter = getCounter(key);
        if (counter == null)
            return null;
        return get(counter);
    }

    public Counter getCounter(String key) {
        return CounterRule.dictionary.get(key);
    }

    public Integer get(Counter counter) {
        if (!map.containsKey(counter)) {
            return 0;
        }
        return map.get(counter);
    }

    public void roundEnds() {
        CounterRule.roundEnd(map, source);
    }

    public void apply() {
        for (Counter counter : map.keySet()) {
            Integer amount = map.get(counter);
            CounterRule.apply(source, counter, amount);
        }
        //counters' effect
    }

}
