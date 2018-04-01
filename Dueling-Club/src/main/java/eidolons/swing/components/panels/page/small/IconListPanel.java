package eidolons.swing.components.panels.page.small;

import main.swing.generic.components.list.G_List;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IconListPanel<E> extends G_ListPanel<E> implements ListCellRenderer<E> {
    private int size;
    private int items;

    // custom controls?
    public IconListPanel(List<E> list, int size, int items) {
        super(list);
        this.size = size;
        this.items = items;
        initialized = true;
        initList();
        getList().setCellRenderer(this);
        setInts();
        refresh();
        panelSize = new Dimension(192, 32);
        // mouse listener?
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    public boolean isBordered() {
        return false;
    }

    @Override
    protected void resetData() {

    }

    @Override
    public int getObj_size() {
        return size;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setInts() {
        sizeInfo = "w " + items * size + ", h " + size;
        minItems = items;
        layoutOrientation = JList.HORIZONTAL_WRAP;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (value instanceof SmallItem) {
            return (Component) value;
        }
        if (value == null) {

            return new JLabel(ImageManager.getEmptyIcon(32));
            // ImageManager.getSizedVersion(ImageManager
            // .getEmptyIcon(), new Dimension(32, 32)));
        }

        return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index,
         isSelected, cellHasFocus);
    }

    @Override
    protected G_List<E> createList() {
        return new G_List<>(data);
    }
}
