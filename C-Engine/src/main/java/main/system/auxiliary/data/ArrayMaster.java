package main.system.auxiliary.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ArrayMaster<T> {

    public static boolean isNotEmpty(Object[] template) {
        if (template == null) {
            return false;
        }
        return template.length != 0;
    }

    public static int[] getIntArrayBetween(int i, int i2) {
        int size = i2 - i;
        int[] result = new int[size];
        int a = 0;
        for (int n = i; n < i2; n++) {
            result[a] = n;
            a++;
        }
        return result;
    }

    public int indexOf(T[] array, T item) {
        int i = 0;
        for (T item_ : array) {
            if (item_.equals(item)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public List<List<T>> get2dList(T[][] array) {
        List<List<T>> list = new LinkedList<>();
        for (T[] t : array) {
            list.add(Arrays.asList(t));
        }
        return list;
    }

    public Collection<Collection<T>> get2dListFrom3dArray(T[][][] array) {
        Collection<Collection<T>> list = new LinkedList<>();
        for (T[][] t : array) {
            for (T[] t1 : t) {
                list.add(Arrays.asList(t1));
            }
        }
        return list;
    }

    public boolean contains(T[] damage_mods, T unblockable) {
        for (T d: damage_mods){
            if (unblockable == null) {
                if (d == null) {
                    return true;
                }
            }
            if (unblockable.equals(d)) {
                return true;
            }
        }
        return false;
    }
//java generics are broken shit
//    public T[] addToArray(T[] strings, T dataString) {
//        List<T> list =    new LinkedList<>( Arrays.asList(strings));
//        list.add(dataString);
//        return (T[]) list.toArray();
//    }
}
