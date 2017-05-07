package main.swing.generic.components.panels;

import main.content.OBJ_TYPE;
import main.entity.obj.Obj;
import main.game.core.game.Game;
import main.game.core.state.GameState;
import main.game.bf.SwingBattleField;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.list.CustomList;
import main.swing.generic.components.list.G_List;
import main.swing.generic.services.listener.ObjListMouseListener;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class G_ListPanel<E> extends G_Panel {

    public String sizeInfo = "";
    protected int wrap;
    protected int rowsVisible = 1;
    protected int minItems = 0;
    protected G_List<E> list;
    protected Collection<E> data;
    protected GameState state;
    protected Obj obj;
    protected SwingBattleField bf;
    protected int hpolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
    protected int vpolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
    protected int layoutOrientation = JList.HORIZONTAL_WRAP;
    protected OBJ_TYPE obj_type;
    protected int selection_mode = ListSelectionModel.SINGLE_SELECTION;
    protected int obj_size = 0;
    protected boolean vertical;
    private boolean permanent;
    private MouseListener customMouseListener;

    // private BorderChecker borderChecker;
    // private BORDER getBorder(ObjType value) {
    // // TODO preCheck hero
    // if (borderChecker == null)
    // return DEFAULT_BORDER;
    // return borderChecker.getBorder(value);
    // }
    // public BorderChecker getBorderChecker() {
    // return borderChecker;
    // }
    //
    // public void setBorderChecker(BorderChecker borderChecker) {
    // this.borderChecker = borderChecker;
    // }
    // abstract protected E getEmptyItem();

    public G_ListPanel(List<E> list) {
        this(list, GuiManager.getSmallObjSize(), null);
        permanent = true;

    }

    public G_ListPanel() {
        this(Game.game.getState());
    }

    public G_ListPanel(List<E> list, int obj_size, GameState state) {
        super();
        data = list;
        if (data == null) {
            data = getEmptyData();
        }
        this.state = state;
        this.obj_size = obj_size;
        init();

        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        if (!CoreEngine.isLevelEditor()) {
            setIgnoreRepaint(true);
        }
        // LookAndFeel.installBorder(this, null);
    }

    public G_ListPanel(GameState state, int obj_size) {
        this(null, obj_size, state);
    }

    public G_ListPanel(GameState state) {
        this(state, 0);

    }

    @Override
    protected void paintBorder(Graphics g) {
        return;
    }

    @Override
    public void paint(Graphics g) {
        if (isBordered()) {
            super.paint(g);
        } else {
            list.paint(g);
        }
    }

    public boolean isBordered() {
        return false;
    }

    protected void init() {
        setInts();
        initList();
        if (isInitialized()) {
            refresh();
        }
    }

    public void initSize() {
        if (!isAutoSizingOn()) {
            return;
        }
        vertical = layoutOrientation != JList.HORIZONTAL_WRAP;
        setPanelSize(new Dimension(getPanelWidth(), getPanelHeight()));
    }

    public int getPanelWidth() {
        return getColumns() * obj_size;
    }

    public int getColumns() {
        return minItems / rowsVisible;
    }

    public int getPanelHeight() {
        return rowsVisible * obj_size;
    }

    public int getRowsVisible() {
        return rowsVisible;
    }

    public void setRowsVisible(int rowsVisible) {
        this.rowsVisible = rowsVisible;
    }

    public int getMinItems() {
        return minItems;
    }

    public void setMinItems(int minItems) {
        this.minItems = minItems;
    }

    public boolean isVertical() {
        return vertical;
    }

    protected void initList() {
        if (isInitialized()) {
            initSize();
        }
        setList(createList());

        if (!isCustom()) {
            getList().addMouseListener(getMouseListener());
        }
        if (getObj_size() != 0) {
            getList().setObj_size(getObj_size());
        }

        getList().setSelectionMode(selection_mode);

        getList().setLayoutOrientation(layoutOrientation);

        if (getRowsVisible() != 0) {
            getList().setVisibleRowCount(rowsVisible);
            getList().setWrap(getWrap());
        }

        getList().setPanel(this);

        list.setBorder(null);
    }

    protected G_List<E> createList() {
        if (isCustom()) {
            return new CustomList<>(data);
        }
        return new G_List<>(data);
    }

    protected boolean isCustom() {
        return true;
    }

    // public int getPageSize() {
    // return minItems;
    // }

    public int getWrap() {
        if (wrap == 0) {
            wrap = isVertical() ? rowsVisible : minItems / rowsVisible;
        }
        return wrap;
    }

    public void setWrap(int wrap) {
        this.wrap = wrap;
    }

    public MouseListener getMouseListener() {
        if (customMouseListener != null) {
            return customMouseListener;
        }
        return new ObjListMouseListener<>(getList());
    }

    protected boolean isScrollable() {
        return true;
    }

    public Collection<E> getEmptyData() {
        Collection<E> emptyData = new LinkedList<>();
        int n = minItems;
        for (int i = 0; i < n; i++) {
            emptyData.add(null);
        }
        return emptyData;
    }

    @Override
    public void refresh() {
        // for (m m : getMouseListeners()) {
        // if (list.getMouseListeners())
        // list.addMouseListener(m);
        // }
        if (isInitialized()) {
            if (getPanelSize() == null) {
                initSize();
            }
        }
        removeAll();
        // if (isInitialized() && !permanent)
        resetData();
        add(list, "pos 0 0" + ", " + sizeInfo);
        list.clearSelection(); // TODO
        revalidate();
        repaint();
    }

    protected void resetData() {
        // get selected obj\

        try {
            data = getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data == null) {
            data = getEmptyData();
        } else {
            if (data.isEmpty()) {
                data = getEmptyData();
            } else if (data.size() < minItems) {
                for (int i = data.size(); i < minItems; i++) {
                    data.add(null);

                }
            }
        }
        if (getList() != null) {
            getList().setData(data);
        }

        // if (obj.getOwner().isMe())
        // data = getData(obj);
        // else {
        // data = getEmptyData();
        // }
    }

    public abstract void setInts();

    public Collection<E> getData() {
        return data;
    }

    public Obj getObj() {
        return obj;
    }

    public void setObj(Obj obj) {
        this.obj = obj;
        if (obj != null) {
            this.obj_type = obj.getOBJ_TYPE_ENUM();
        }
        // dataChanged();
    }

    public void dataChanged() {
        refresh();
    }

    public int getObj_size() {
        return obj_size;
    }

    public void setObj_size(int obj_size) {
        this.obj_size = obj_size;
    }

    public G_List<E> getList() {
        return list;
    }

    public void setList(G_List<E> list) {
        this.list = list;
    }

    public void setCustomMouseListener(MouseListener customMouseListener) {
        this.customMouseListener = customMouseListener;
    }
}
