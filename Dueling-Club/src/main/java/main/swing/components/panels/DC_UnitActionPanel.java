package main.swing.components.panels;

import main.content.CONTENT_CONSTS.ACTION_TYPE;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.DC_UnitObj;
import main.game.MicroGameState;
import main.rules.DC_ActionManager;
import main.swing.components.obj.ActionListItem;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * buttons - defend
 *
 * @author Regulus
 */
public class DC_UnitActionPanel extends G_ListPanel<DC_UnitAction> implements
        ListCellRenderer<DC_UnitAction> {

    public static final int SPECIAL_W = 5;
    private static final int UNIT_ACTION_PANEL_MIN_ITEMS = 5;
    private boolean rdy = false;
    private ACTION_DISPLAY_GROUP group;
    private int panelWidth;

    public DC_UnitActionPanel(List<DC_UnitAction> list) {
        super(list);
        panelWidth = SPECIAL_W;
        rdy = true;
        setInts();
        initList();
        getList().setCellRenderer(this);
        getList().setEmptyIcon(ImageManager.getAltEmptyListIcon());
        refresh();
    }

    public DC_UnitActionPanel(ACTION_DISPLAY_GROUP group, MicroGameState state) {
        super(state);

        this.group = group;
        panelWidth = group.getPanelWidth();
        bf = state.getGame().getBattleField();
        rdy = true;
        setInts();
        initList();
        getList().setCellRenderer(this);
        getList().setEmptyIcon(ImageManager.getAltEmptyListIcon());
        refresh();
    }

    @Override
    protected boolean isScrollable() {
        return false;
    }

    @Override
    public boolean isInitialized() {
        return rdy;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DC_UnitAction> list,
                                                  DC_UnitAction value, int index, boolean isSelected, boolean cellHasFocus) {
        ActionListItem item = new ActionListItem(value, isSelected, cellHasFocus);
        if (value == null) {
            item.setEmptyIcon(ImageManager.getAltEmptyListIcon());
            item.refresh();
        }
        return item;
    }

    @Override
    public void refresh() {
        // this.obj = state.getGame().getManager().getActiveObj();
        super.refresh();
    }

    @Override
    protected void resetData() {
        super.resetData();
    }

    @Override
    public List<DC_UnitAction> getData() {
        // if (obj == null)
        // return getEmptyData();
        //
        // LinkedList<DC_UnitAction> list = new LinkedList<DC_UnitAction>();
        // List<DC_UnitAction> actions = getObj().getActionMap().getOrCreate(type);
        // if (actions != null)
        // list.addAll(actions);
        // return list;
        return new LinkedList<>(data);
    }

    @Override
    public DC_UnitObj getObj() {
        return (DC_UnitObj) super.getObj();
    }

    @Override
    public void setInts() {
        if (!rdy) {
            return;
        }
        sizeInfo = "w " + panelWidth * GuiManager.getSmallObjSize() + ", h "
                + GuiManager.getSmallObjSize();
        hpolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        vpolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
        rowsVisible = 1;
        minItems = UNIT_ACTION_PANEL_MIN_ITEMS;
        layoutOrientation = JList.HORIZONTAL_WRAP;
    }

    public enum ACTION_DISPLAY_GROUP {
        STD_MODES(DC_ActionManager.STD_ACTION_N, ACTION_TYPE.MODE, false),
        STD_ACTIONS(DC_ActionManager.STD_ACTION_N, ACTION_TYPE.STANDARD, false),
        ADDITIONAL_MOVES(DC_ActionManager.STD_ACTION_N, ACTION_TYPE.ADDITIONAL_MOVE, false),
        SPEC_ACTIONS(SPECIAL_W, ACTION_TYPE.SPECIAL_ACTION, true),
        SPEC_ATTACKS(SPECIAL_W, ACTION_TYPE.SPECIAL_ATTACK, true),
        SPEC_MOVES(SPECIAL_W, ACTION_TYPE.SPECIAL_MOVE, true),

        // USABLE_ITEMS?
        ;
        private ACTION_TYPE type;

        private int panelWidth;

        private boolean scrollable;

        ACTION_DISPLAY_GROUP(int panelWidth, ACTION_TYPE type, boolean scrollable) {
            this.setType(type);
            this.panelWidth = panelWidth;
            this.scrollable = scrollable;

        }

        public static ACTION_DISPLAY_GROUP getDisplayGroup(ACTION_TYPE type) {
            for (ACTION_DISPLAY_GROUP group : values()) {
                if (type == group.getType()) {
                    return group;
                }
            }
            return null;

        }

        public int getPanelWidth() {
            return panelWidth;
        }

        public ACTION_TYPE getType() {
            return type;
        }

        public void setType(ACTION_TYPE type) {
            this.type = type;
        }

        public boolean isScrollable() {
            return scrollable;
        }
    }

}
