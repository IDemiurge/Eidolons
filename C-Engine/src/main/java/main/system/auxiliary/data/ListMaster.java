package main.system.auxiliary.data;

import com.badlogic.gdx.utils.Array;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.*;

public class ListMaster<E> {
    private Class<E> clazz;

    public ListMaster() {

    }

    public ListMaster(Class<E> e) {
        this.clazz = e;
    }

    public static List<String> toList(String string, boolean ENUM) {
        List<String> list = new ArrayList<>();
        for (String item : string.split(StringMaster.getSeparator())) {
            if ((!item.isEmpty() && ENUM) || DataManager.isTypeName(item)) {
                list.add(item);
            }
        }
        return list;
    }

    public static void fill(Collection list, Object coordinates, int size) {
        for (int i = 0; i < size; i++) {
            list.add(coordinates);
        }
    }

    public <E> List<List<E>> generatePerm(List<E> original) {
        if (original.isEmpty()) {
            List<List<E>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        E firstElement = original.remove(0);
        List<List<E>> returnValue = new ArrayList<>();
        List<List<E>> permutations = generatePerm(original);
        for (List<E> smallerPermutated : permutations) {
            for (int index = 0; index <= smallerPermutated.size(); index++) {
                List<E> temp = new ArrayList<>(smallerPermutated);
                temp.add(index, firstElement);
                returnValue.add(temp);
            }
        }
        return returnValue;
    }

    public List<E> asList(E... values) {
        List<E> list = new ArrayList<>();
        for (E v : values) {
            if (v != null) {
                list.add(v);
            }
        }
        return list;
    }

    public static List<Object> toList(Object... values) {
        List<Object> list = new ArrayList<>();
        for (Object v : values) {
            if (v != null) {
                list.add(v);
            }
        }
        return list;
    }

    public static List<String> toStringList(Collection list) {
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
        List<String> list = new ArrayList<>();
        if (values == null) {
            return list;
        }
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
                        string = ContainerUtils.constructContainer(toStringList(objects));
                    } else {
                        string = v.toString();
                    }
                }
                if (wellFormatted) {
                    string = StringMaster.format(string);
                }
                list.add(string);
            }
        }
        return list;
    }

    public static boolean isNotEmpty(Array a) {
        if (a == null) {
            return false;
        }
        return a.size != 0;
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

    public static Collection<String> fillWithEmptyStrings(Collection<String> list, int size) {
        if (list.size() < size) {
            for (int i = list.size(); i < size; i++) {
                list.add("");
            }
        }
        return list;
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
        List<String> list = new ArrayList<>();
        for (Entity entity : objList) {
            list.add(entity.getName());
        }
        return list;
    }

    public static List<Entity> getEntityList(List members) {
        List<Entity> list = new ArrayList<>();
        for (Object o : members) {
            if (o instanceof Entity) {
                Entity entity = (Entity) o;
                list.add(entity);
            }
        }
        return list;
    }

    public static List<Integer> getIntegerList(int i1) {
        List<Integer> list = new ArrayList<>();
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
        List<Integer> indexList = new ArrayList<>(Arrays.asList(indices));
        for (Object e : new ArrayList<>(list)) {
            if (!indexList.contains(index)) {
                list.remove(e);
            }
            index++;
        }
        return list;
    }

    public static List removeIndices(List list, Integer... indices) {
        int index = 0;
        List<Integer> indexList = new ArrayList<>(Arrays.asList(indices));
        for (Object e : new ArrayList<>(list)) {
            if (indexList.contains(index)) {
                list.remove(e);
            }
            index++;
        }
        return list;
    }

    public static List<ObjAtCoordinate> toObjAtCoordinate(List<? extends Obj> units) {

        List<ObjAtCoordinate> list = new ArrayList<>();
        for (Obj e : units) {
            list.add(new ObjAtCoordinate(e.getType(), e.getCoordinates()));
        }
        return list;
    }


    public static void removeNullElements(List<?> list) {
        List<Object> elements = new ArrayList<>(list);
        for (Object o : elements) {
            if (o == null) {
                list.remove(o);
            }
        }
    }

    public static void invert(List list) {
        List inv_list = new ArrayList<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            inv_list.add(list.get(i));
        }
        list.clear();
        list.addAll(inv_list);
    }

    public Set<E> toSet(E... values) {
        Set<E> set = new LinkedHashSet<>();
        for (E v : values) {
            if (v != null) {
                set.add(v);
            }
        }
        return set;
    }

    public List<E> toList_(E... values) {
        List<E> list = new ArrayList<>();
        for (E v : values) {
            if (v != null) {
                list.add(v);
            }
        }
        return list;
    }

    public List<E> toList(String string) {
        List<E> list = new ArrayList<>();
        for (String item : string.split(StringMaster.getSeparator())) {
            E e = new EnumMaster<E>().retrieveEnumConst(clazz, item);
            if (e != null) {
                list.add(e);
            }
        }
        return list;
    }

    public List<ObjType> convertToTypeList(Collection<E> data) {
        List<ObjType> list = new ArrayList<>();
        for (E item : data) {
            if (item instanceof Obj) {
                list.add(((Obj) item).getType());
            }
        }
        return list;
    }

    public List<E> openSubLists(List<List<E>> nestedList) {
        List<E> list = new ArrayList<>();
        if (nestedList == null) {
            return list;
        }
        for (List<E> sublist : nestedList) {
            list.addAll(sublist);
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
            return new ArrayList<>();
        }
        // if (f return new ArrayList<>(fillWithNullElements(new
        // ArrayList<E>(), size));
        List<List<E>> lists = new ArrayList<>();
        if (size <= 0) {
            lists.add(new ArrayList<>(list));
            return lists;
        }
        for (Iterator<E> iterator = list.iterator(); iterator.hasNext(); ) {
            List<E> newList = new ArrayList<>();
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

        List<E> clone = new ArrayList<>();

        clone.addAll(list);

        return clone;
    }

    public boolean contains(E[] array, E e, boolean strict) {
        return contains(getList(array), e, strict);
    }

    private boolean contains(List<E> list, E e, boolean strict) {
        return getIndexString(list, e, strict) != -1;
    }

    public List<E> getList(E... e) {
        return new ArrayList<>(Arrays.asList(e));
    }

    public List<E> invertList(List<E> list) {
        List<E> inv_list = new ArrayList<>();

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
        List<E> result = new ArrayList<>();
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
        list = new ArrayList<>(list);
        for (int n = 0; n < list.size() - i; n++) {
            Collections.swap(list, n, n + i);
        }
        return list;
        // List<E> result = new ArrayList<>();
        //
        // for (E generic : new ArrayList<>(list)) {
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

    public static <E> List<E> merge(List<E>... lists) {
        List<E> result = new ArrayList<>();
        for (List<E> sub : lists) {
            result.addAll(sub);
        }
        return result;

    }
    public List<E> mergeShuffle(List<E>... lists) {
        List<E> result = new ArrayList<>();
        for (List<E> sub : lists) {
            result.addAll(sub);
        }
        Collections.shuffle(result);
        return result;

    }

    public List<E> toObjList(List<E> tasks, List<ObjType> list) {
        List<E> filtered = new ArrayList<>();
        for (ObjType sub : list) {
            //				if (framework.task.getType() == generic) {
            filtered.addAll(tasks);
            break;
        }
        //		}
        return filtered;
    }

    public List<E> getCommonElements(List<E> l, List<E> l2) {
        List<E> result = new ArrayList<>();

        for (E sub : l) {
            if (l2.contains(sub)) {
                result.add(sub);
            }
        }
        return result;
    }

    public void removeDuplicates(List<E> list) {
        List<E> cleaned = new ArrayList<>(new LinkedHashSet<>(list));
        list.clear();
        list.addAll(cleaned);

    }

    public List<E> getRemovedSequentialDuplicates(List<E> list, boolean onlyPairs) {
        list = new ArrayList<>(list);
        List<E> result = new ArrayList<>();
        int i = 0;

        for (E sub : new ArrayList<>(list)) {
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
        list = new ArrayList<>(list);
        List<E> result = new ArrayList<>();
        int i = 0;

        for (E sub : new ArrayList<>(list)) {
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
        return new ArrayList<>(new LinkedHashSet<>(list));
    }

    public List<E> removeNulls(List<E> list) {
        List<E> elements = new ArrayList<>(list);
        for (Object o : elements) {
            if (o == null) {
                list.remove(o);
            }
        }
        return list;
    }

    public List<Obj> convertToObjList(List<Integer> list, Game game) {
        List<Obj> objList = new ArrayList<>();
        for (Integer id : list) {
            Obj obj = game.getObjectById(id);
            if (obj != null) {
                objList.add(obj);
            }
        }
        return objList;
    }

    public List<Integer> convertToIdList(List<Obj> objList) {
        List<Integer> list = new ArrayList<>();
        for (Obj obj : objList) {
            list.add(obj.getId());
        }
        return list;
    }

}
