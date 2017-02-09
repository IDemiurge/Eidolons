package main.system.auxiliary;

import main.data.XLinkedMap;

import java.util.Map;

public class WeightMap<E> extends XLinkedMap<E, Integer> {

    public WeightMap(String data) {

    }

    public WeightMap() {

    }

    public WeightMap(Map<E, Integer> map) {
        putAll(map);
    }

    public E getGreatest() {
        E greatest = null;
        Integer greatestN = 0;
        for (E key : keySet()) {
            Integer integer = get(key);
            if (integer != null) {
                if (integer > greatestN) {
                    greatest = key;
                    greatestN = integer;
                }
            }
        }
        return greatest;
    }
}
