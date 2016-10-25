package main.system.auxiliary.secondary;

import java.util.Comparator;

public class DefaultComparator<T> implements Comparator<T> {

    @Override
    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }

}
