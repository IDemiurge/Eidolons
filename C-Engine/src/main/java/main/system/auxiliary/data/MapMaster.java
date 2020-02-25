package main.system.auxiliary.data;

import main.data.XLinkedMap;
import main.system.SortMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class MapMaster<E, T> {

    public static void addToIntegerMap(Map map, Object key, Integer n) {
        Integer i = (Integer) map.get(key);
        if (i == null) {
            map.put(key, n);
        } else {
            map.put(key, new Integer(i + n));
        }
    }

    public static void addToFloatMap(Map map, Object key, Float n) {
        Float i = (Float) map.get(key);
        if (i == null) {
            map.put(key, n);
        } else {
            map.put(key, new Float(i + n));
        }
    }

    public static void removeFromListMap(Map map, Object key, Object value) {
        Object o = map.get(key);
        if (o instanceof Collection) {
            ((Collection) o).remove(value);
        }
    }

    public static void addToListMap(Map map, Object key, Object value) {
        if (value == null) {
            return;
        }
        Object entry = map.get(key);
        if (entry instanceof Collection) {
            Collection collection = (Collection) entry;
            if (value instanceof Collection) {
                collection.addAll(((Collection) value));
            } else {
                collection.add(value);
            }
        } else {

            if (value instanceof Collection) {
                map.put(key, new ArrayList<>(((Collection) value)));
                return;
            }
            ArrayList<Object> list = new ArrayList<>();
            list.add(value);
            map.put(key, list);
        }

    }

    public static String getNetStringForMap(Map map) {
        StringBuilder s = new StringBuilder();

        for (Object e : map.keySet()) {
            s.append(e.toString()).append(StringMaster.wrapInParenthesis(map.get(e).toString())).append(StringMaster.SEPARATOR);
        }
        return s.toString();
    }

    public static boolean isNotEmpty(Map map) {
        if (map == null) {
            return false;
        }
        return !map.isEmpty();
    }

    public static Object get(Map map, int i) {
        if (map.keySet().size() <= i)
            return null;
        return map.keySet().toArray()[i];
    }

    public static void addToStringMap(Map<String, String> values, String name, String value
    ) {
        addToStringMap(values, name, value, ContainerUtils.getContainerSeparator());
    }

    public static void addToStringMap(Map<String, String> values, String name, String value
            , String separator) {
        String val = values.get(name);
        if (val == null) {
            values.put(name, value);
            return;
        }
        val += separator + value;
        values.put(name, val);
    }

    public static Object getKeyForValue_(Map itemMap, Object value) {
        for (Object o : itemMap.entrySet()) {
            Entry e = (Entry) o;
            if (value.equals(itemMap.get(e.getKey()))) {
                return e.getKey();
            }
        }
        return null;
    }

    public static Map<String, String> createStringMap(String data) {
        return createStringMap(false, data);
    }

    public static Map<String, String> createStringMap(boolean inverse, String data) {
        Map<String, String> map = new HashMap<>();
        for (String substring : ContainerUtils.openContainer(data)) {
            if (inverse)
                map.put(StringMaster.getWeightItem(substring, true), StringMaster.getWeightItem(substring, false));
            else
                map.put(StringMaster.getWeightItem(substring, false), StringMaster.getWeightItem(substring, true));
        }
        return map;
    }

    public HashMap<E, T> cloneHashMap(Map<E, T> map) {
        HashMap<E, T> clone = new HashMap<E, T>();
        clone.putAll(map);
        return clone;
    }

    public LinkedHashMap<E, T> cloneLinkedHashMap(Map<E, T> map) {
        LinkedHashMap<E, T> clone = new LinkedHashMap<E, T>();
        clone.putAll(map);
        return clone;
    }

    public E getKeyForValue(Map<E, T> itemMap, T value) {
        for (Entry<E, T> e : itemMap.entrySet()) {
            if (itemMap.get(e.getKey()).equals(value)) {
                return e.getKey();
            }
        }
        return null;
    }

    public List<T> joinMap(Map<E, List<T>> map) {
        List<T> list = new ArrayList<>();
        for (E e : map.keySet()) {
            list.addAll(map.get(e));
        }
        return list;
    }

    public Map<T, E> invertMap(Map<E, T> map) {
        XLinkedMap<T, E> newMap = new XLinkedMap<>();
        for (E key : map.keySet()) {
            newMap.put(map.get(key), key);
        }
        return newMap;
    }

    public Map<E, T> invertMapOrder(Map<E, T> map, boolean keysetOnly) {
        List<E> inv_list = new ArrayList<>();
        List<T> inv_val_list = new ArrayList<>();
        for (E key : map.keySet()) {
            inv_list.add(0, key);
            if (!keysetOnly) {
                inv_val_list.add(0, map.get(key));
            }
        }
        return constructMap(inv_list, (keysetOnly ? new ArrayList<>(map.values()) : inv_val_list));

    }

    public Map<E, T> constructMap(Collection<E> keys, List<T> values) {
        Map<E, T> map = new XLinkedMap<>();
        int i = 0;
        for (E key : keys) {
            if (values.size() <= i) {
                break;
            }
            map.put(key, values.get(i));
            i++;
        }
        return map;
    }

    public List<Map<E, T>> splitMap(Map<E, T> map, int size) {
        return splitList(false, size, map);
    }

    public List<Map<E, T>> splitList(boolean fillWithNulls, int size, Map<E, T> map) {
        if (map == null) {
            return new ArrayList<>();
        }
        List<Map<E, T>> lists = new ArrayList<>();
        for (Iterator<E> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
            Map<E, T> newMap = new XLinkedMap<>();
            for (int i = 0; i < size; i++) {
                if (!iterator.hasNext()) {
                    break;
                }
                E next = iterator.next();
                newMap.put(next, map.get(next));
            }
            // if (fillWithNulls)
            // fillWithNullElements(newList, size);
            if (!newMap.isEmpty()) {
                lists.add(newMap);
            }
        }
        return lists;
    }

    public Map<Integer, T> getIdMapFromNetString(String input, Class<T> c) {
        RandomWizard<T> wizard = new RandomWizard<>();
        wizard.constructWeightMap(input, c, null, true);
        return wizard.getInvertedMap();
    }

    public Map<E, T> crop(Map<E, T> map, int crop, boolean first) {
        List<E> removeList = new ArrayList<>();
        Set<E> set = (!first) ? new MapMaster<E, T>().invertMapOrder(map, false).keySet() : map
                .keySet();
        for (E o : set) {
            crop--;
            if (crop < 0) {
                break;
            }
            removeList.add(o);

        }
        for (E o : removeList) {
            map.remove(o);
        }

        return map;

    }

    public Map<E, Integer> getSortedMap(Map<E, Integer> map,
                                        Function<Object, Integer> function) {
        ArrayList<E> list = new ArrayList<>(map.keySet());
        SortMaster.sortByExpression(list, function);

        Map<E, Integer> sortedMap = new XLinkedMap<>();
        for (E sub : list) {
            sortedMap.put(sub, map.get(sub));
        }
        return sortedMap;
    }

}
