package main.swing.generic.components.editors.lists;

import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.swing.components.TextComp;
import main.swing.generic.Decorator;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.list.G_List;
import main.swing.generic.services.listener.ObjListMouseListener;
import main.system.auxiliary.GuiManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GenericListChooser<E> implements ListSelectionListener {
    static List<LC_MODS> mods = new LinkedList<>();
    private static Map<String, String> map;
    private static Decorator panelDecorator;
    private static String tooltip;
    private static OBJ_TYPE staticTYPE;
    protected int columns = 2;
    protected List<E> listData;
    protected List<E> secondListData;
    protected G_List<E> list;
    protected G_List<E> secondList;
    protected SELECTION_MODE mode;
    protected boolean ENUM;
    protected G_Panel panel;
    protected ListControlPanel buttonPanel;
    protected OBJ_TYPE TYPE;
    protected Component parent;
    protected List<Object> varTypes;
    protected ListItemMouseListener<E> mouseListener;
    protected ListObjRenderer renderer;
    boolean movable;
    boolean horizontal;
    private Class<?> varClass;
    private int maxRowCount = 17;
    private int maxRowCountEnum = 29;
    private int maxColumnNumber = 6;
    private TextComp toolTipPanel;
    private Map<String, String> tooltipMap;
    private Decorator decorator;
    private TextComp toolTipMainPanel;

    public GenericListChooser() {

    }

    public GenericListChooser(Collection<E> objList, OBJ_TYPE TYPE) {
        mode = SELECTION_MODE.SINGLE;
        this.listData = new LinkedList<>(objList);
        this.TYPE = TYPE;
    }

    public static void addMod(LC_MODS mod) {
        mods.add(mod);

    }

    public static void setTooltipMapForComponent(Map<String, String> pool) {
        map = pool;
    }

    public static void setTooltip(String string) {
        tooltip = string;

    }

    public static void setDecorator(Decorator decorator) {
        panelDecorator = decorator;

    }

    public static OBJ_TYPE getStaticTYPE() {
        return staticTYPE;
    }

    public static void setStaticTYPE(OBJ_TYPE T) {
        staticTYPE = T;
    }

    public String getString() {
        E result = choose();
        if (result == null) {

            return null;
        }
        return result.toString();
    }

    public G_Panel getChoicePanel() {
        if (list == null)
            initList();
        if (panel == null)
            initPanel();
        return panel;
    }

    public E choose() {
        // if (list == null)
        initList();
        // if (panel == null)
        initPanel();

        int result = JOptionPane.showConfirmDialog(getParent(), panel);
        if (result != JOptionPane.YES_OPTION) {
            return null;
        }
        if (mode == SELECTION_MODE.SINGLE)
            return list.getSelectedValue();

        else {
            return getMultiValue();
        }
    }

    protected Collection<E> getSelectedItems(G_List<E> list) {
        return list.getSelectedValuesList();
    }

    protected void initPanel() {

        panel = new G_Panel();
        // if (list == null)
        initList();
        JScrollPane scroll = new JScrollPane(list);
        int height = getPanelHeight();
        int width = getPanelWidth();
        panel.setPanelSize(new Dimension(width, height));
        String listPanelHeight = height + "-40";

        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scroll, "id list1, pos 0 0, h " + listPanelHeight + "!");

        if (secondList != null) {
            JScrollPane scroll2 = new JScrollPane(secondList);
            scroll2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            panel.add(scroll2, "id list2, pos list1.x2+50 0, h " + listPanelHeight + "!");

            // panel.add(buttonPanel, "id bp, pos 0 " + listPanelHeight);
            panel.add(buttonPanel, "id bp, pos 0 list2.y2");
        } else {
            // list.addMouseListener(listener);
        }

        if (tooltipMap == null) {
            tooltipMap = map;
            map = null;
        }
        toolTipPanel = new TextComp() {
            @Override
            protected Color getColor() {
                return Color.black;
            }
        };
        panel.add(toolTipPanel, "id tp, pos ip.x @max_y");
        toolTipMainPanel = new TextComp(tooltip) {
            @Override
            protected Color getColor() {
                return Color.black;
            }
        };
        panel.add(toolTipMainPanel, "id tmp, pos @max_x+(max_x*(-3)) 0");
        if (decorator == null) {
            decorator = panelDecorator;
            panelDecorator = null;
        }
        if (decorator != null)
            decorator.addComponents(panel);
    }

    protected int getPanelWidth() {
        return GuiManager.getScreenWidthInt() / 3;
    }

    protected int getPanelHeight() {
        return 2 * GuiManager.getScreenHeightInt() / 3 + 65;
    }

    protected void itemSelected(String value) {
        if (tooltipMap != null) {
            toolTipPanel.setText(tooltipMap.get(value));
            toolTipPanel.refresh();
            panel.refresh();
        }
    }

    protected void initButtonPanel() {
        this.buttonPanel = new ListControlPanel(this);
        buttonPanel.setVarTypes(getVarTypes());
        buttonPanel.setVarClass(getVarClass());
    }

    public Class<?> getVarClass() {
        return varClass;
    }

    public void setVarClass(Class<?> varClass) {
        this.varClass = varClass;
    }

    protected void initList() {

        list = new G_List<E>(listData);
        list.addMouseListener(new ObjListMouseListener<E>(getList()));
        list.setSelectionMode(mode.getMode());
        list.addListSelectionListener(this);
        renderer = new ListObjRenderer(TYPE);
        renderer.setMods(mods);
        list.setCellRenderer(renderer);
        int rowCount = getMaxRowCount();
        while (listData.size() / rowCount >= getMaxColumnNumber()) {
            rowCount++;
        }

        list.setVisibleRowCount(rowCount);
        list.setLayoutOrientation((ENUM) ? JList.VERTICAL_WRAP : JList.HORIZONTAL_WRAP);

        // list.addKeyListener(new ListKeyListener(false, list));

        if (mode != SELECTION_MODE.SINGLE) {

            secondList = new G_List<E>(secondListData);
            secondList.addMouseListener(new ObjListMouseListener<E>(getSecondList()));
            secondList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            secondList.setCellRenderer(renderer);
            secondList.setVisibleRowCount(listData.size() / getColumns());
            secondList.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        }
        // if (buttonPanel == null)
        initButtonPanel();
        setMouseListener(new ListItemMouseListener<E>(TYPE, buttonPanel, mode, list));
        list.addMouseListener(getMouseListener());
        if (secondList != null) {
            secondList.addMouseListener(getMouseListener());
            getMouseListener().setSecondList(secondList);
        }

    }

    public G_List<E> getList() {
        return list;
    }

    public void setList(G_List<E> list) {
        this.list = list;
    }

    public G_List<E> getSecondList() {
        return secondList;
    }

    public void setSecondList(G_List<E> secondList) {
        this.secondList = secondList;
    }

    public List<E> getListData() {
        return listData;
    }

    public void setListData(List<E> listData) {
        this.listData = listData;
    }

    public List<E> getSecondListData() {
        return secondListData;
    }

    public void setSecondListData(List<E> secondListData) {
        this.secondListData = secondListData;
    }

    public SELECTION_MODE getMode() {
        return mode;
    }

    public void setMode(SELECTION_MODE mode) {
        this.mode = mode;
    }

    public boolean isENUM() {
        return ENUM;
    }

    public void setENUM(boolean eNUM) {
        ENUM = eNUM;
    }

    public OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public void setTYPE(OBJ_TYPE tYPE) {
        TYPE = tYPE;
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public List<Object> getVarTypes() {
        return varTypes;
    }

    public void setVarTypes(List<Object> varTypes) {
        this.varTypes = varTypes;
    }

    protected E getMultiValue() {
        return null;
    }

    public synchronized boolean isMovable() {
        return movable;
    }

    public synchronized void setMovable(boolean movable) {
        this.movable = movable;
    }

    public synchronized boolean isHorizontal() {
        return horizontal;
    }

    public synchronized void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public ListItemMouseListener<E> getMouseListener() {
        return mouseListener;
    }

    public void setMouseListener(ListItemMouseListener<E> mouseListener) {
        this.mouseListener = mouseListener;
    }

    public int getMaxRowCount() {
        return (ENUM) ? maxRowCountEnum : (TYPE != null) ? getMaxRowCountType(TYPE) : maxRowCount;
    }

    public void setMaxRowCount(int maxRowCount) {
        this.maxRowCount = maxRowCount;
    }

    private int getMaxRowCountType(OBJ_TYPE TYPE) {
        if (!(TYPE instanceof OBJ_TYPES))
            return Integer.MAX_VALUE;
        for (OBJ_TYPES t : OBJ_TYPES.values()) {
            if (TYPE.equals(t)) {
                switch (t) {
                    case SPELLS:
                    case SKILLS:
                    case CLASSES:
                        return maxRowCount;
                }
            }
        }
        // if( (TYPE instanceof MACRO_OBJ_TYPES))
        // return Integer.MAX_VALUE;

        return 9;
    }

    public int getMaxColumnNumber() {
        return maxColumnNumber;
    }

    public void setMaxColumnNumber(int maxColumnNumber) {
        this.maxColumnNumber = maxColumnNumber;
    }

    public Map<String, String> getTooltipMap() {
        return tooltipMap;
    }

    public void setTooltipMap(Map<String, String> tooltipMap) {
        this.tooltipMap = tooltipMap;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == list) {
            E selectedValue = list.getSelectedValue();
            if (selectedValue != list)
                itemSelected(selectedValue.toString());
        }

    }

    public void setMaxRowCountEnum(int maxRowCountEnum) {
        this.maxRowCountEnum = maxRowCountEnum;
    }

    public enum LC_MODS {
        TEXT_DISPLAYED

    }

}
