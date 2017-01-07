package main.swing.generic.components.list;

import main.data.XLinkedMap;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.ListMaster;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomList<E> extends G_List<E> {

    List<Component> comps;
    private Image displayImage;
    private int offsetX;
    private int offsetY;
    private Map<Rectangle, E> mouseMap;
    private MouseListener mouseListener;

    public CustomList(Collection<E> data) {
        super(data);
        setIgnoreRepaint(true);
        addMouseListener(this);
    }

    public Point getItemPosition(E e) {
        int i = new ListMaster<E>().getIndex(getData(), e);
        return getIndexPosition(i);
    }

    public Point getIndexPosition(int i) {
        int x = getObj_size() * getColumnIndex(i);
        int y = getObj_size() * getRowIndex(i);

        Point p = new Point(x, y);
        return p;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(displayImage, offsetX, offsetY, null);
    }

    private int getColumnIndex(int i) {
        if (getWrap() == 0)
            return 0;
        return isVertical() ? getColumnIndexVertical(i) : getColumnIndexHorizontal(i);
    }

    private int getRowIndex(int i) {
        if (getWrap() == 0)
            return 0;
        if (getVisibleRowCount() == 0)
            return 0;
        return isVertical() ? getRowIndexVertical(i) : getRowIndexHorizontal(i);
    }

    // VERTICAL
    private int getColumnIndexVertical(int i) {
        return i % getWrap();
    }

    private int getRowIndexVertical(int i) {
        return i / (getWrap());
    }

    public int getWidthVertical() {
        return getWrap() * getObj_size();
    }

    public int getHeightVertical() {
        return getData().size() / getWrap() * getObj_size();
    }

    // HORIZONTAL
    private int getColumnIndexHorizontal(int i) {
        return i % getWrap();
    }

    private int getRowIndexHorizontal(int i) {
        return i / (getWrap());
    }

    public int getWidthHorizontal() {
        return getWrap() * getObj_size();
    }

    public int getHeightHorizontal() {
        return getData().size() / getWrap() * getObj_size();
    }

    @Override
    public int getWidth() {
        return isVertical() ? getWidthVertical() : getWidthHorizontal();
        // return (getColumnIndex(getData().size() - 1) + 1) * getObj_size();
    }

    @Override
    public int getHeight() {
        return isVertical() ? getHeightVertical() : getHeightHorizontal();
        // return (getRowIndex(getData().size() - 1) + 1) * getObj_size();
    }

    @Override
    public int locationToIndex(Point location) {
        for (Rectangle rect : mouseMap.keySet()) {
            if (rect.contains(location)) {
                // return ListMaster.get
                return new LinkedList<E>(getData()).indexOf(mouseMap.get(rect));
            }
        }
        return super.locationToIndex(location);
    }

    @Override
    public E getSelectedValue() {
        if (getSelectedIndex() != -1)
            if (getData().size() > getSelectedIndex())
                return new LinkedList<E>(getData()).get(getSelectedIndex());
        return null;
    }

    public int getVisibleColumnCount() {
        int n = getData().size();
        if (n == 0)
            return 0;
        if (getVisibleRowCount() == 0)
            return 0;
        if (getWrap() == 0)
            return 0;
        return isVertical() ? getRowIndexHorizontal(n) : n / getVisibleRowCount();
    }

    public void setData(final Collection<E> data) {
        if (data == null) {
            main.system.auxiliary.LogMaster.log(0, "NULL DATA!!! ");
            return;
        }
        this.data = data;
        if (isInitialized())
            update();
    }

    private void updateDisplayImage() {
        displayImage = ImageManager.getNewBufferedImage(getWidth(), getHeight());// TODO
        int n = 0;
        for (E e : getData()) {
            Component comp = comps.get(n);
            int h = comp.getWidth();
            int w = comp.getHeight();
            Point p = getIndexPosition(n);
            n++;
            int x = p.x;
            int y = p.y;
            Image image = null;
            if (comp instanceof ListItem) {
                ListItem listItem = (ListItem) comp;
                listItem.refresh();
                if (listItem.getIcon() == null)
                    continue;
                image = listItem.getImage();
            } else if (comp instanceof GraphicComponent) {
                GraphicComponent graphicComponent = (GraphicComponent) comp;
                image = graphicComponent.getImg();
            } else {
                if (w == 0)
                    w = 64;
                if (h == 0)
                    h = 64;
                image = ImageManager.getNewBufferedImage(w, h);
                Graphics g = image.getGraphics();
                comp.paintAll(g);
                mouseMap.put(new Rectangle(x, y, w, h), e);
                displayImage.getGraphics().drawImage(image, x, y, null);
                continue;
            }

            w = image.getWidth(null);
            h = image.getHeight(null);
            mouseMap.put(new Rectangle(x, y, w, h), e);
            displayImage.getGraphics().drawImage(image, x, y, null);
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        ListItem<E> item = getListItem(value, isSelected, cellHasFocus);
        item.setBorderChecker(borderChecker);
        if (getEmptyIcon() != null) {
            item.setEmptyIcon(getEmptyIcon());
            item.refresh();
        }
        return item;
    }

    protected ListItem<E> getListItem(E value, boolean isSelected, boolean cellHasFocus) {
        return new ListItem<E>(true, value, isSelected, cellHasFocus, getObj_size());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        for (Rectangle rect : mouseMap.keySet()) {
            if (rect.contains(e.getPoint())) {
                itemClicked(mouseMap.get(rect), e);
            }
        }

    }

    private void itemClicked(E item, MouseEvent e) {
        getMouseListener().mouseClicked(e);
        update();
        repaint();
    }

    private MouseListener getMouseListener() {
        if (panel != null)
            return panel.getMouseListener();
        return mouseListener;
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public void update() {
        mouseMap = new XLinkedMap<>();
        comps = new LinkedList<>();
        int n = 0;
        for (E e : getData()) {
            Component comp = getCellRenderer().getListCellRendererComponent(this, e, n,
                    isSelectedIndex(n), false);
            comps.add(comp);
            n++;
        }
        updateDisplayImage();
    }

    public void removeMouseListeners() {

    }

}
