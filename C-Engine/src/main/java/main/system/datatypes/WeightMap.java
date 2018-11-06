package main.system.datatypes;

import main.content.enums.entity.OBJ_TYPE_ENUM;
import main.data.XLinkedMap;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.Map;

public class WeightMap<E> extends XLinkedMap<E, Integer> {

    private Class<E> clazz;

    public WeightMap(String data, Class<E> clazz) {
        super(new RandomWizard<E>().constructWeightMap(data, clazz));
        this.clazz = clazz;
    }

    public WeightMap() {

    }

    public WeightMap(Class<E> clazz) {
        this.clazz = clazz;
    }

    public WeightMap(Map<E, Integer> map) {
        putAll(map);
    }

    public WeightMap<E> chain(Object key, Integer value) {
        if (key.getClass() == clazz)
            return putChain((E) key, value);
        if (key instanceof OBJ_TYPE_ENUM) {
            return putChain((E)((OBJ_TYPE_ENUM) key).getName(), value);
        }
        return putChain((E) key.toString(), value);
    }

    public WeightMap<E> putChain(E key, Integer value) {
        super.put(key, value);
        return this;
    }

    public WeightMap<E> merge(WeightMap<E> map) {
        for (E e : map.keySet()) {
            MapMaster.addToIntegerMap(this, e, map.get(e));
        }
        return this;
    }
    @Override
    public String toString() {
        String string = "";
        for (E e : keySet()) {
            string += e + StringMaster.wrapInParenthesis(StringMaster.toStringForm(get(e)))
             + ContainerUtils.getContainerSeparator();
        }
        return string;
    }

    public E getRandomByWeight() {

        return new RandomWizard<E>().getObjectByWeight(this);
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
