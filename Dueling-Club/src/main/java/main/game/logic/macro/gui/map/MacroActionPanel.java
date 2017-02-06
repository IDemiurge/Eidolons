package main.game.logic.macro.gui.map;

import main.entity.obj.DC_UnitObj;
import main.game.logic.macro.entity.MacroAction;
import main.game.logic.macro.gui.map.MacroAP_Holder.MACRO_ACTION_GROUPS;
import main.swing.components.obj.ActionListItem;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;

public class MacroActionPanel extends G_ListPanel<MacroAction> implements
        ListCellRenderer<MacroAction> {

    private boolean rdy = false;
    private int panelWidth;
    private MACRO_ACTION_GROUPS group;

    public MacroActionPanel(MACRO_ACTION_GROUPS group, List<MacroAction> list) {
        super(list);
        this.group = group;
        panelWidth = list.size();
        rdy = true;
        setInts();
        initList();
        getList().setCellRenderer(this);
        getList().setEmptyIcon(ImageManager.getAltEmptyListIcon());
        getList().addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    getList().getSelectedValue().invokeRightClicked();
                } else {
                    getList().getSelectedValue().invokeClicked();
                }

            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {
            }

        });
        refresh();
    }

    protected boolean isScrollable() {
        return false;
    }

    public boolean isInitialized() {
        return rdy;
    }

    public Component getListCellRendererComponent(
            JList<? extends MacroAction> list, MacroAction value, int index,
            boolean isSelected, boolean cellHasFocus) {
        ActionListItem item = new ActionListItem(value, isSelected,
                cellHasFocus);
        if (value == null) {
            item.setEmptyIcon(ImageManager.getAltEmptyListIcon());
            item.refresh();
        } else if (group == MACRO_ACTION_GROUPS.MODE) {
            if (value.getMode() == value.getOwnerObj().getMacroMode()) {
                item.setSpecialBorder(BORDER.HIGHLIGHTED_GREEN);
            } else {
                item.setSpecialBorder(null);
            }
            item.refresh();
        }

        return item;
    }

    public void refresh() {
        // this.obj = state.getGame().getManager().getActiveObj();
        super.refresh();
    }

    protected void resetData() {
    }

    public Collection<MacroAction> getData() {
        // if (obj == null)
        // return getEmptyData();
        //
        // LinkedList<Entity> list = new LinkedList<Entity>();
        // List<Entity> actions = getObj().getActionMap().getOrCreate(type);
        // if (actions != null)
        // list.addAll(actions);
        // return list;
        return data;
    }

    public DC_UnitObj getObj() {
        return (DC_UnitObj) super.getObj();
    }

    public void setInts() {
        if (!rdy) {
            return;
        }
        sizeInfo = "w " + panelWidth * GuiManager.getSmallObjSize() + ", h "
                + GuiManager.getSmallObjSize();
        hpolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        vpolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
        rowsVisible = 1;
        minItems = data.size();
        layoutOrientation = JList.HORIZONTAL_WRAP;
    }

}
