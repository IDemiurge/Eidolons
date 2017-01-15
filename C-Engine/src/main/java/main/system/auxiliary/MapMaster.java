package main.system.auxiliary;

import main.data.XLinkedMap;

import java.util.*;
import java.util.Map.Entry;

public class MapMaster<E, T> {

    public static void addToIntegerMap(Map map, Object key, Integer n) {
        Integer i = (Integer) map.get(key);
        if (i == null)
            map.put(key, n);
        else
            map.put(key, new Integer(i + n));
    }

    public static void addAllToListMap(Map map, Map map1) {
        for (Object key : map1.keySet()) {
            addToListMap(map, key, map1.get(key));
        }

    }

    public static void addToListMap(Map map, Object key, Object value) {
        Object entry = map.get(key);
        if (entry instanceof Collection) {
            Collection collection = (Collection) entry;
            collection.add(value);
        } else {
            LinkedList<Object> list = new LinkedList<>();
            list.add(value);
            map.put(key, list);
        }

    }

    public static String getNetStringForMap(Map map) {
        String s = "";

        for (Object e : map.keySet()) {
            s += e.toString() + StringMaster.wrapInParenthesis(map.get(e).toString())
                    + StringMaster.SEPARATOR;
        }
        return s;
    }

    public static boolean isNotEmpty(Map map) {
        if (map == null)
            return false;
        return !map.isEmpty();
    }

    public E getKeyForValue(Map<E, T> itemMap, T value) {
        for (Entry<E, T> e : itemMap.entrySet()) {
            if (itemMap.get(e.getKey()).equals(value))
                return e.getKey();
        }
        return null;
    }

    public List<T> joinMap(Map<E, List<T>> map) {
        List<T> list = new LinkedList<T>();
        for (E e : map.keySet()) {
            list.addAll(map.get(e));
        }
        return list;
    }

    public Map<T, E> invertMap(Map<E, T> map) {
        XLinkedMap<T, E> newMap = new XLinkedMap<T, E>();
        for (E key : map.keySet()) {
            newMap.put(map.get(key), key);
        }
        return newMap;
    }

    public Map<E, T> invertMapOrder(Map<E, T> map, boolean keysetOnly) {
        List<E> inv_list = new LinkedList<>();
        List<T> inv_val_list = new LinkedList<>();
        for (E key : map.keySet()) {
            inv_list.add(0, key);
            if (!keysetOnly)
                inv_val_list.add(0, map.get(key));
        }
        return constructMap(inv_list, (keysetOnly ? new LinkedList<>(map.values()) : inv_val_list));

    }

    public Map<E, T> constructMap(Collection<E> keys, List<T> values) {
        Map<E, T> map = new XLinkedMap<>();
        int i = 0;
        for (E key : keys) {
            if (values.size() <= i)
                break;
            map.put(key, values.get(i));
            i++;
        }
        return map;
    }

    public List<Map<E, T>> splitMap(Map<E, T> map, int size) {
        return splitList(false, size, map);
    }

    public List<Map<E, T>> splitList(boolean fillWithNulls, int size, Map<E, T> map) {
        if (map == null)
            return new LinkedList<>();
        List<Map<E, T>> lists = new LinkedList<>();
        for (Iterator<E> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
            Map<E, T> newMap = new XLinkedMap<E, T>();
            for (int i = 0; i < size; i++) {
                if (!iterator.hasNext())
                    break;
                E next = iterator.next();
                newMap.put(next, map.get(next));
            }
            // if (fillWithNulls)
            // fillWithNullElements(newList, size);
            if (!newMap.isEmpty())
                lists.add(newMap);
        }
        return lists;
    }

    public Map<Integer, T> getIdMapFromNetString(String input, Class<T> c) {
        RandomWizard<T> wizard = new RandomWizard<T>();
        wizard.constructWeightMap(input, c, null, true);
        return wizard.getInvertedMap();
    }

    public Map<E, T> crop(Map<E, T> map, int crop, boolean first) {
        List<E> removeList = new LinkedList<>();
        Set<E> set = (!first) ? new MapMaster<E, T>().invertMapOrder(map, false).keySet() : map
                .keySet();
        for (E o : set) {
            crop--;
            if (crop < 0)
                break;
            removeList.add(o);

        }
        for (E o : removeList)
            map.remove(o);

        return map;

    }

    public static Object get(Map map, int i) {
    return map.keySet().toArray()[i];
    }
}
