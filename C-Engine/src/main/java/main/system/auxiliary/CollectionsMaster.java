package main.system.auxiliary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CollectionsMaster {

    public static <T> Collection<T> getSortedCollection(Collection<T> c) {
        return c;

    }

    public static <V, K> Map<V, K> getInvertedMap(Map<K, V> map) {

        Map<V, K> inv = new HashMap<V, K>();

        for (Entry<K, V> entry : map.entrySet()) {
            inv.put(entry.getValue(), entry.getKey());
        }

        return inv;
    }

    public static <E> E getLast(Collection<E> collection) {
        E item = null;
        for (Iterator<E> iterator = collection.iterator(); iterator.hasNext(); ) {
            item = iterator.next();
        }
        return item;
    }

}
