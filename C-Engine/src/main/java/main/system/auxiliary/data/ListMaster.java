package main.system.auxiliary.data;

import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import org.apache.poi.ss.formula.functions.T;

import java.util.*;

public class ListMaster<E> {
    private Class<E> clazz;

    public ListMaster() {

    }

    public ListMaster(Class<E> e) {
        this.clazz = e;
    }

    public static List<String> toList(String string, boolean ENUM) {
        List<String> list = new LinkedList<>();
        for (String item : string.split(StringMaster.getSeparator())) {
            if ((!item.isEmpty() && ENUM) || DataManager.isTypeName(item)) {
                list.add(item);
            }
        }
        return list;
    }

    public   List<E> toList_(E... values) {
        List<E> list = new LinkedList<>();
        for (E v : values) {
            if (v != null) {
                list.add(v);
            }
        }
        return list;
    }
        public static List<Object> toList(Object... values) {
        List<Object> list = new LinkedList<>();
        for (Object v : values) {
            if (v != null) {
                list.add(v);
            }
        }
        return list;
    }

    public static List<String> toStringList(List list) {
        return toStringList(list.toArray());
    }

    public static List<String> toStringList(Object... values) {
        return toStringList(null, values);
    }

    public static List<String> toStringList(Boolean preferEntityNameOrIfNameKnown, Object... values) {
        return toStringList(false, preferEntityNameOrIfNameKnown, values);
    }

    public static List<String> toStringList(boolean wellFormatted,
                                            Boolean preferEntityNameOrIfNameKnown, Object... values) {
        List<String> list = new LinkedList<>();
        for (Object v : values) {
            if (v != null) {
                String string;
                if (v instanceof Entity && preferEntityNameOrIfNameKnown != null) {
                    Entity entity = (Entity) v;
                    string = preferEntityNameOrIfNameKnown ? entity.getName() : entity
                            .getNameIfKnown();
                } else {
                    if (v instanceof Object[]) {
                        Object[] objects = (Object[]) v;
                        string = StringMaster.constructContainer(toStringList(objects));
                    } else {
                        string = v.toString();
                    }
                }
                if (wellFormatted) {
                    string = StringMaster.getWellFormattedString(string);
                }
                list.add(string);
            }
        }
        return list;
    }

    public static boolean isNotEmpty(Collection list) {
        if (list == null) {
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }
        for (Object o : list) {
            if (o instanceof Collection) {
                if (isNotEmpty((Collection) o)) {
                    return true;
                }
            } else if (o != null) {
                return true;
            }
        }
        return false;
    }

    public static int getIndexString(Collection<?> list, String item, boolean strict) {
        // if (list.contains(item))
        // return list.indexOf(item);
        int i = 0;
        for (Object str : list) {
            if (StringMaster.compareByChar(str.toString(), item, true)) {
                return i;
            }
            i++;
        }
        i = 0;
        for (Object str : list) {
            if (StringMaster.compare(str.toString(), item, true)) {
                return i;
            }
            i++;
        }
        if (strict) {
            return -1;
        }
        i = 0;
        for (Object str : list) {
            if (StringMaster.compare(str.toString(), item, false)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static boolean contains(List<String> list, String item, boolean strict) {
        return getIndexString(list, item, strict) != -1;
    }

    public static Collection<?> fillWithNullElements(Collection<?> list, int size) {
        if (list.size() < size) {
            for (int i = list.size(); i < size; i++) {
                list.add(null);
            }
        }
        return list;
    }

    public static List<String> toNameList(Collection<? extends Entity> objList) {
        List<String> list = new LinkedList<>();
        for (Entity entity : objList) {
            list.add(entity.getName());
        }
        return list;
    }

    public static List<Entity> getEntityList(List members) {
        List<Entity> list = new LinkedList<>();
        for (Object o : members) {
            if (o instanceof Entity) {
                Entity entity = (Entity) o;
                list.add(entity);
            }
        }
        return list;
    }

    public static List<Integer> getIntegerList(int i1) {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < i1; i++) {
            list.add(i);
        }
        return list;
    }

    public static void cropLast(List<String> lines, int i) {
        int size = lines.size();
        if (size <= i) {
            return;
        }
        for (int index = i + size; i > 0; i--) {
            if (lines.size() <= index) {
                return;
            }
            lines.remove(index);
        }

    }

    public static Object getListItem(List list, int index) {
        if (list.size() <= index) {
            return null;
        }
        return list.get(index);
    }

    public static List removeIndicesAllExcept(List list, Integer... indices) {
        int index = 0;
        List<Integer> indexList = new LinkedList<>(Arrays.asList(indices));
        for (Object e : new LinkedList<>(list)) {
            if (!indexList.contains(index)) {
                list.remove(e);
            }
            index++;
        }
        return list;
    }

    public static List removeIndices(List list, Integer... indices) {
        int index = 0;
        List<Integer> indexList = new LinkedList<>(Arrays.asList(indices));
        for (Object e : new LinkedList<>(list)) {
            if (indexList.contains(index)) {
                list.remove(e);
            }
            index++;
        }
        return list;
    }

    public static List<ObjAtCoordinate> toObjAtCoordinate(List<? extends Obj> units) {

        List<ObjAtCoordinate> list = new LinkedList<>();
        for (Obj e : units) {
            list.add(new ObjAtCoordinate(e.getType(), e.getCoordinates()));
        }
        return list;
    }

    public static void removeNullElements(List<?> list) {
        List<Object> elements = new LinkedList<>(list);
        for (Object o : elements) {
            if (o == null) {
                list.remove(o);
            }
        }
    }

    public static void invert(List list) {
        List inv_list = new LinkedList<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            inv_list.add(list.get(i));
        }
        list.clear();
        list.addAll(inv_list);
    }

    public List<E> toList(String string) {
        List<E> list = new LinkedList<>();
        for (String item : string.split(StringMaster.getSeparator())) {
            E e = new EnumMaster<E>().retrieveEnumConst(clazz, item);
            if (e != null) {
                list.add(e);
            }
        }
        return list;
    }

    public List<ObjType> convertToTypeList(Collection<E> data) {
        List<ObjType> list = new LinkedList<>();
        for (E item : data) {
            if (item instanceof Obj) {
                list.add(((Obj) item).getType());
            }
        }
        return list;
    }

    public List<E> openSubLists(List<List<E>> nestedList) {
        List<E> list = new LinkedList<>();
        if (nestedList == null) {
            return list;
        }
        for (List<E> sublist : nestedList) {
            for (E e : sublist) {
                list.add(e);
            }
        }
        return list;
    }

    public boolean compareNested(List<List<E>> list, List<List<E>> list2) {
        if (!isNotEmpty(list) && !isNotEmpty(list2)) {
            return true;
        }
        if (!isNotEmpty(list)) {
            return false;
        }
        if (!isNotEmpty(list2)) {
            return false;
        }

        if (list == list2) {
            return true;
        }
        if (list.equals(list2)) {
            return true;
        }
        for (List<E> sublist : list) {
            for (List<E> sublist2 : list2) {
                if (!compare(sublist, sublist2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean compare(List<E> list, List<E> list2) {
        if (!isNotEmpty(list) && !isNotEmpty(list2)) {
            return true;
        }
        if (!isNotEmpty(list)) {
            return false;
        }
        if (!isNotEmpty(list2)) {
            return false;
        }

        if (list == list2) {
            return true;
        }
        if (list.equals(list2)) {
            return true;
        }
        for (E e : list) {
            for (E e2 : list2) {
                if (e == null && e2 == null) {
                    return true;
                }
                if (e == null) {
                    return false;
                }
                if (e2 == null) {
                    return false;
                }
                if (!e.equals(e2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getIndexString(Collection<E> list, E item, boolean strict) {
        return getIndexString(list, item.toString(), strict);
    }

    public int getIndex(Collection<E> list, E item) {
        int i = 0;
        for (E e : list) {
            if (e.equals(item)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public E findType(String typeName, List<E> types) {
        List<String> list = DataManager.toStringList((List<? extends Entity>) types);
        int i = getIndexString(list, typeName, true);
        if (i == -1) {
            i = getIndexString(list, typeName, false);
        }
        if (i == -1) {
            return null;
        }
        return types.get(i);
    }

    public List<List<E>> splitList(int size, Collection<E> list) {
        return splitList(false, size, list);
    }

    public List<List<E>> splitList(boolean fillWithNulls, int size, Collection<E> list) {
        if (list == null) {
            return new LinkedList<>();
        }
        // if (f return new LinkedList<>(fillWithNullElements(new
        // LinkedList<E>(), size));
        List<List<E>> lists = new LinkedList<>();
        if (size <= 0) {
            lists.add(new LinkedList<>(list));
            return lists;
        }
        for (Iterator<E> iterator = list.iterator(); iterator.hasNext(); ) {
            List<E> newList = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                if (!iterator.hasNext()) {
                    break;
                }
                newList.add(iterator.next());
            }
            if (fillWithNulls) {
                fillWithNullElements(newList, size);
            }
            if (!newList.isEmpty()) {
                lists.add(newList);
            }
        }
        return lists;
    }

    public List<E> cloneList(List<E> list) {

        List<E> clone = new LinkedList<>();

        for (E e : list) {
            clone.add(e);
        }

        return clone;
    }

    public boolean contains(E[] array, E e, boolean strict) {
        return contains(getList(array), e, strict);
    }

    private boolean contains(List<E> list, E e, boolean strict) {
        return getIndexString(list, e, strict) != -1;
    }

    public List<E> getList(E... e) {
        return new LinkedList<>(Arrays.asList(e));
    }

    public List<E> invertList(List<E> list) {
        List<E> inv_list = new LinkedList<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            inv_list.add(list.get(i));
        }

        // list.clear();
        // for (E item : inv_list)
        // list.add(item);

        return inv_list;
    }

    public List<E> getSmallest(List<E>... lists) {
        List<E> smallest = null;
        int min_size = Integer.MAX_VALUE;
        for (List<E> e : lists) {
            if (e == null) {
                continue;
            }
            if (e.size() < min_size) {
                min_size = e.size();
                smallest = e;
            }
        }

        return smallest;
    }

    public E getItem(Collection<E> list, int index) {
        if (list.size() <= index) {
            return null;
        }
        int i = 0;
        for (E e : list) {
            if (index == i) {
                return e;
            }
            i++;
        }
        return null;
    }

    public Map<E, String> getStringMap(Map<E, ?> map) {
        Map<E, String> stringMap = new XLinkedMap<>();
        for (E e : map.keySet()) {
            stringMap.put(e, map.get(e).toString());
        }
        return stringMap;
    }

    public Map<E, String> getBonusMap(Map<E, Integer> map) {
        Map<E, String> stringMap = new XLinkedMap<>();
        for (E e : map.keySet()) {
            stringMap.put(e, StringMaster.getBonusString(map.get(e)));
        }
        return stringMap;
    }

    public Map<E, String> getModsMap(Map<E, Integer> map) {
        Map<E, String> stringMap = new XLinkedMap<>();
        for (E e : map.keySet()) {
            stringMap.put(e, StringMaster.getModifierString(map.get(e)));
        }
        return stringMap;
    }

    public List<E> join(boolean duplicates, List<E> list, List<E> list2) {
        List<E> result = new LinkedList<>();
        addAll(list, result, duplicates);
        addAll(list2, result, duplicates);
        return result;
    }

    private void addAll(List<E> list, List<E> result, boolean duplicates) {
        if (list != null) {
            for (E e : list) {
                if (!duplicates) {
                    if (result.contains(e)) {
                        continue;
                    }
                }
                result.add(e);
            }
        }
    }

    public List<E> interleave(List<E> list, int i) {
        list = new LinkedList<>(list);
        for (int n = 0; n < list.size() - i; n++) {
            Collections.swap(list, n, n + i);
        }
        return list;
        // List<E> result = new LinkedList<>();
        //
        // for (E generic : new LinkedList<>(list)) {
        //
        //
        // // n++;
        // // if (n>=i){
        // // n=0;
        // // result.add(generic);
        // // }
        // element = list.get(index);
        // if (index + 1 % (i * 2) == 0 && index + 1 >= i * 2) {
        // result.set(index, element);
        // }
        // }
        // return result;
        // // 2->1; 4->3;6->5;...
        // // 4->2; 6->4;8->6;...
    }

    public List<E> mergeShuffle(List<E>... lists) {
        List<E> result = new LinkedList<>();
        for (List<E> sub : lists) {
            result.addAll(sub);
        }
        Collections.shuffle(result);
        return result;

    }

    public List<E> toObjList(List<E> tasks, List<ObjType> list) {
        List<E> filtered = new LinkedList<>();
        for (ObjType sub : list) {
            for (E task : tasks)
//				if (task.getType() == generic) {
            {
                filtered.add(task);
            }
            break;
        }
//		}
        return filtered;
    }

    public List<E> getCommonElements(List<E> l, List<E> l2) {
        List<E> result = new LinkedList<>();

        for (E sub : l) {
            if (l2.contains(sub)) {
                result.add(sub);
            }
        }
        return result;
    }

    public void removeDuplicates(List<E> list) {
        LinkedList<E> linkedList = new LinkedList<>(new LinkedHashSet<>(list));
        list.clear();
        list.addAll(linkedList);

    }

    public List<E> getRemovedSequentialDuplicates(List<E> list, boolean onlyPairs) {
        list = new LinkedList<>(list);
        List<E> result = new LinkedList<>();
        int i = 0;

        for (E sub : new LinkedList<>(list)) {
            if (list.get(i - 1).equals(sub)) {
                if (!onlyPairs || i != 0) {
                    list.remove(i);
                    i = 0;
                    continue;
                }
            }
            i++;

        }
        return result;

    }

    public List<E> getDifferingElements(List<E> list, List<E> list2) {
        list = new LinkedList<>(list);
        List<E> result = new LinkedList<>();
        int i = 0;

        for (E sub : new LinkedList<>(list)) {
            if (list2.size() <= i) {
                result.add(sub);
                break;
            }
            if (!list2.get(i).equals(sub)) {
                result.add(sub);
            } else {
                i++;
            }

        }

        return result;
    }

    public List<E> getRemovedDuplicates(List<E> list) {
        return new LinkedList<>(new LinkedHashSet<>(list));
    }

    public List<E> removeNulls(List<E> list) {
        List<E> elements = new LinkedList<>(list);
        for (Object o : elements) {
            if (o == null) {
                list.remove(o);
            }
        }
        return list;
    }

    public List<Obj> convertToObjList(List<Integer> list, Game game) {
        List<Obj> objList = new LinkedList<>();
        for (Integer id : list) {
            Obj obj = game.getObjectById(id);
            if (obj != null) {
                objList.add(obj);
            }
        }
        return objList;
    }

    public List<Integer> convertToIdList(List<Obj> objList) {
        List<Integer> list = new LinkedList<>();
        for (Obj obj : objList) {
            list.add(obj.getId());
        }
        return list;
    }

}
