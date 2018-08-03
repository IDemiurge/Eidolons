package main.system.datatypes;

import main.data.XLinkedMap;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.Map;

public class WeightMap<E> extends XLinkedMap<E, Integer> {

    private  Class<E> clazz;

    public WeightMap(String data, Class<E> clazz) {
        super(new RandomWizard<E>().constructWeightMap(data, clazz));
        this.clazz = clazz;
    }

    public WeightMap() {

    }

    public WeightMap<E> chain(Object key, Integer value) {
        super.put((E) key.toString(), value);
        return  this;
    }
        public WeightMap<E> putChain(E key, Integer value) {
          super.put(key, value);
          return  this;
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

    public WeightMap(Map<E, Integer> map) {
        putAll(map);
    }

    public E getRandomByWeight() {
       return  new RandomWizard<E>().getObjectByWeight(toString(), clazz);
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
