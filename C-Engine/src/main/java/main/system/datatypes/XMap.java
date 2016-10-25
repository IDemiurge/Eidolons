package main.system.datatypes;

import java.util.HashMap;

public class XMap<E, T> extends HashMap<E, T> {

    // Map<E, List<T>> hiddenEntries;

    @Override
    public T get(Object key) {
        // if (key instanceof LinkedList){
        // LinkedList linkedList = (LinkedList) key;
        // hiddenEntries
        // }
        return super.get(key);
    }

}
