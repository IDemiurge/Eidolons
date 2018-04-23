package main.system.datatypes;

import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@SuppressWarnings("serial")
public class DequeImpl<E> extends ConcurrentLinkedDeque<E>
// implements List<E>
{
    int size = 0;

    public DequeImpl(E... array) {
        this(new ArrayList<>(Arrays.asList(array)));
    }

    public DequeImpl(Collection<? extends E> asList) {
        super(asList == null ? new ArrayList<>() : asList);
    }

    public DequeImpl() {
    }

    public DequeImpl(Collection<E>... lists) {
        for (Collection<E> sub : lists)
            addAll(sub);
    }

    public int indexOf(E e) {
        int i = 0;
        for (E e1 : this) {
            if (e.equals(e1)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public DequeImpl<E> getRemoveAll(List<? extends E> list) {
        // deque = new DequeImpl<>(asList);
        for (E e : list) {
            remove(e);
        }
        return this;
    }

    public DequeImpl<E> getAddAllCast(Collection<?> list) {
        // deque = new DequeImpl<>(asList);
        for (Object o : list) {
            add((E) o);
        }
        return this;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (!ListMaster.isNotEmpty(c)) {
            return false;
        }
        boolean result = false;
        try {
            result = super.addAll(c);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

//        size = size();
        return result;
    }

    public DequeImpl<E> addAllCast(Collection<?> list) {
        for (Object item : list) {
            try {
                add((E) item);
            } catch (Exception e) {

            }
        }
        return this;
    }

    public boolean addCast(Object o) {
        return add((E) o);
    }

    @Override
    public boolean add(E e) {
        boolean add = super.add(e);
//        size = size();
        return add;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = super.remove(o);
//        size = size();
        return remove;
    }

    public E get(int index) {
        size = size();
        if (index>size)
            throw new IndexOutOfBoundsException(index+" index > size "+size);
        int i = 0;
        for (E e : this) {
            if (i == index) {
                return e;
            }
            i++;
        }

        return null;
    }

    public DequeImpl<E> addAllChained(Collection<E> e) {
        addAll(e);
        return this;
    }

    public DequeImpl<E> addChained(E e) {
        add(e);
        return this;
    }
    //
    // @Override
    // public boolean addAll(int index, Collection<? extends E> c) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // @Override
    // public E get(int index) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public E set(int index, E element) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public void add(int index, E element) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public E remove(int index) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public int indexOf(Object o) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // @Override
    // public int lastIndexOf(Object o) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    //
    // @Override
    // public ListIterator<E> listIterator() {
    // return null;
    // }
    //
    // @Override
    // public ListIterator<E> listIterator(int index) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public List<E> subList(int fromIndex, int toIndex) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //

}
