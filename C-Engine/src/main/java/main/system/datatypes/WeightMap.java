package main.system.datatypes;

import main.data.XLinkedMap;
import main.system.auxiliary.StringMaster;

import java.util.Map;

public class WeightMap<E> extends XLinkedMap<E, Integer> {

    public WeightMap(String data) {

    }

    public WeightMap() {

    }

    @Override
    public String toString() {
        String string = "";
        for (E e : keySet()) {
            string += e + StringMaster.wrapInParenthesis(StringMaster.toStringForm(get(e)));
        }
        return super.toString();
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
