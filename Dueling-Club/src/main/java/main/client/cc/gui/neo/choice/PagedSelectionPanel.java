package main.client.cc.gui.neo.choice;

import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_ListPanel;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public class PagedSelectionPanel<E> extends G_PagePanel<E> implements ListSelectionListener {

    public static final boolean VERTICAL = false;
    public static final int VERSION = 3;

    private ChoiceView<E> view;
    private ListCellRenderer<? super E> customRenderer;

    public PagedSelectionPanel(ChoiceView<E> panel, int pageSize, int itemSize, int columnsCount,
                               boolean vertical, int version) {
        super(pageSize, vertical, version);
        this.view = panel;
        this.itemSize = itemSize;
        this.wrap = columnsCount;
    }

    public PagedSelectionPanel(ChoiceView<E> panel, int pageSize, int itemSize, int columnsCount,
                               boolean vertical) {
        this(panel, pageSize, itemSize, columnsCount, vertical, VERSION);
    }

    public PagedSelectionPanel(ChoiceView<E> panel, int pageSize, int itemSize, int columnsCount) {
        this(panel, pageSize, itemSize, columnsCount, VERTICAL, VERSION);
    }

    @Override
    protected int getArrowOffsetY() {
        // TODO Auto-generated method stub
        return super.getArrowOffsetY();
    }

    @Override
    protected int getArrowOffsetX() {
        return super.getArrowOffsetX();
    }

    @Override
    protected int getArrowOffsetX2() {
        // TODO Auto-generated method stub
        return super.getArrowOffsetX2();
    }

    @Override
    protected int getArrowOffsetY2() {
        // TODO Auto-generated method stub
        return super.getArrowOffsetY2();
    }

    @Override
    protected G_Component createPageComponent(List<E> list) {
        SelectionPage<E> page = new SelectionPage<>(list, getWrap());

        page.getList().addListSelectionListener(this);
        page.getList().setObj_size(getItemSize());
        if (getCustomRenderer() != null) {
            page.getList().setCellRenderer(getCustomRenderer());
        }
        return page;
    }

    @Override
    protected List<List<E>> getPageData() {
        return splitList(data);
    }

    @Override
    protected boolean isAddControlsAlways() {
        return false;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        E i = ((SelectionPage<E>) getCurrentComponent()).getList().getSelectedValue();
        view.itemSelected(i);
    }

    public ListCellRenderer<? super E> getCustomRenderer() {
        return customRenderer;
    }

    public void setCustomRenderer(ListCellRenderer<? super E> customRenderer) {
        this.customRenderer = customRenderer;
    }

    public class SelectionPage<E> extends G_ListPanel<E> {

        public SelectionPage(List<E> list, int wrap) {
            super(list);
            this.list.setEmptyIcon(getEmptyIcon(itemSize));
            initialized = true;
            minItems = pageSize;
            this.wrap = wrap;
            super.init();
        }

        @Override
        protected boolean isCustom() {
            return false;
        }

        @Override
        public void setInts() {
            if (!isInitialized()) {
                return;
            }
            layoutOrientation = (vertical) ? JList.HORIZONTAL_WRAP : JList.VERTICAL_WRAP;
            this.rowsVisible = pageSize / getWrap();
        }

        @Override
        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public int getObj_size() {
            return getItemSize();
        }

        @Override
        public int getColumns() {
            return !vertical ? pageSize / rowsVisible : getWrap();
        }

        private String getEmptyIcon(int itemSize) {
            if (itemSize == GuiManager.getSmallObjSize()) {
                return ImageManager.getAltEmptyListIcon();
            }
            if (itemSize == GuiManager.getSmallObjSize()) {

            }
            return null;
        }

    }

}
