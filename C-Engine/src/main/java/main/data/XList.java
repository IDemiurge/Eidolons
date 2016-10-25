package main.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class XList<E> extends LinkedList<E> {
    public XList() {
        super();
    }

    public XList(E... e) {
        super(Arrays.asList(e));
    }

    public void addAllCast(Collection<?> list) {
        for (Object e : list) {
            add((E) e);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null)
            return false;
        return super.removeAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null)
            return false;
        return super.addAll(c);
    }

    public void addAllUnique(Collection<E> list) {
        for (E e : list)
            if (!contains(e))
                add(e);
    }

}
