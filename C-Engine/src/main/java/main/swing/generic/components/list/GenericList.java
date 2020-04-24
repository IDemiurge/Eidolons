package main.swing.generic.components.list;

import main.swing.generic.components.Refreshable;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.util.Vector;

public abstract class GenericList<E> extends JList<E> implements
 ListSelectionListener, ListCellRenderer<E>, Refreshable {
    protected Vector<E> list = new Vector<>();

    public GenericList() {
        setCellRenderer(this);
        // addListSelectionListener(this);
    }

    public Vector<E> getList() {
        return list;
    }

    public void setList(Vector<E> list) {
        this.list = list;
    }

    @Override
    public abstract void refresh();

    public void add(E e) {
        Vector<E> v = new Vector<>();
        v.addAll(list);
        v.add(e);

        setList(v);
        setListData(list);
    }

    public void remove(E e) {

        Vector<E> v = new Vector<>();
        v.addAll(list);
        v.remove(e);

        setList(v);
        setListData(list);
    }

}
