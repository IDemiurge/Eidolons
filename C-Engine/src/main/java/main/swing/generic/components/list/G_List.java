package main.swing.generic.components.list;

import main.swing.generic.components.panels.G_InfoPanel;
import main.swing.generic.components.panels.G_ListPanel;
import main.swing.generic.misc.BORDER_CHECKER;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

/**
 * from Enum, List, Array, Text file
 * <portrait>
 * renderer? listener?
 *
 * @author JustMe
 */
public class G_List<E> extends JList<E> implements ListCellRenderer<E>, MouseListener {
    protected static final long serialVersionUID = 1L;
    // ListSelectionListener ???
    protected Collection<E> data;
    protected int obj_size = 0;
    protected G_InfoPanel ip;
    protected String emptyIcon;
    protected BORDER_CHECKER borderChecker;
    protected int wrap;
    G_ListPanel<E> panel;
    private DefaultListModel<E> listModel;
    private boolean initialized;

    public G_List(Collection<E> data) {
        listModel = new DefaultListModel<>();
        setModel(listModel);
        setCellRenderer(this);
        setData(data);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        initialized = true;
    }

    @Override
    protected void paintBorder(Graphics g) {
        return;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public void addItem(E item) {
        listModel.addElement(item);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Point getItemPosition(E e) {
        int i = new ListMaster<E>().getIndex(getData(), e);
        int x = isVertical() ? getObj_size() * i / getWrap() : getObj_size() * i % getWrap();
        int y = isVertical() ? getObj_size() * i % (getWrap()) : getObj_size() * i
         / (getVisibleRowCount());

        Point p = new Point(x, y);
        return p;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // e.consume();
        // for (Rectangle rect : mouseMap.keySet()) {
        // if (rect.contains(e.getPoint())) {
        // itemClicked(mouseMap.get(rect), e);
        // }
        // }

    }

    public E locationToItem(Point location) {
        return new ListMaster<E>().getItem(getData(), locationToIndex(location));
    }

    protected boolean isVertical() {
        return getLayoutOrientation() == JList.VERTICAL_WRAP
         || getLayoutOrientation() == JList.VERTICAL;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        ListItem<E> item = new ListItem<>(value, isSelected, cellHasFocus, getObj_size());
        item.setBorderChecker(borderChecker);
        if (getEmptyIcon() != null) {
            item.setEmptyIcon(getEmptyIcon());
            item.refresh();
        }
        return item;
    }

    public int getWrap() {
        if (wrap == 0) {
            return 1;
        }
        // if (wrap != getVisibleRowCount())
        // wrap = isVertical() ? getData().size() / getVisibleRowCount()
        // : getVisibleRowCount();
        // }
        return wrap;
    }

    public void setWrap(int wrap) {
        this.wrap = wrap;
    }

    public G_ListPanel<E> getPanel() {
        return panel;
    }

    public void setPanel(G_ListPanel<E> panel) {
        this.panel = panel;
    }

    @Override
    public String toString() {
        return "List Component: " + data.toString();
    }

    public int getObj_size() {
        if (obj_size == 0) {
            return GuiManager.getSmallObjSize(); // TODO [QUICK FIX]
        }
        return obj_size;
    }

    public void setObj_size(int obj_size) {
        this.obj_size = obj_size;
    }

    public G_InfoPanel getIp() {
        return ip;
    }

    public void setIp(G_InfoPanel ip) {
        this.ip = ip;
    }

    public String getEmptyIcon() {
        return emptyIcon;
    }

    public void setEmptyIcon(String emptyIcon) {
        this.emptyIcon = emptyIcon;
    }

    public void setBorderChecker(BORDER_CHECKER borderChecker) {
        this.borderChecker = borderChecker;
    }

    public Collection<E> getData() {
        return data;
    }

    public void setData(final Collection<E> data) {
        // setBackground(ColorManager.getCurrentColor());

        if (data == null) {
            LogMaster.log(0, "NULL DATA!!! ");
            return;
        }
        if (this.data != null) {
            if (this.data.equals(data)) { // TODO
                return;
            }
        }
        listModel.removeAllElements();

        for (E element : data) {
            listModel.addElement(element);
        }

        this.data = data;
        revalidate();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
