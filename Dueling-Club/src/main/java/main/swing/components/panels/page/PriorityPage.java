package main.swing.components.panels.page;

import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.swing.components.PriorityListItem;
import main.swing.components.panels.DC_PriorityListPanel;
import main.swing.generic.components.list.G_List;
import main.swing.generic.components.panels.G_ListPanel;
import main.swing.generic.services.listener.ObjListMouseListener;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class PriorityPage extends G_ListPanel<DC_HeroObj> implements ListCellRenderer<DC_HeroObj> {

    public PriorityPage(List<DC_HeroObj> list) {
        super(list);
        getList().setCellRenderer(this);
        panelSize = new Dimension(GuiManager.getSmallObjSize(), DC_PriorityListPanel.PLP_MIN_ITEMS
                * GuiManager.getSmallObjSize());
        addMouseListener(getMouseListener());
    }

    @Override
    public void setInts() {
        sizeInfo = "h " + DC_PriorityListPanel.PLP_MIN_ITEMS + "*" + GuiManager.getSmallObjSize()
                + "!";
        vpolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
        rowsVisible = DC_PriorityListPanel.PLP_MIN_ITEMS;
        minItems = DC_PriorityListPanel.PLP_MIN_ITEMS;
        hpolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        layoutOrientation = JList.VERTICAL;

    }

    @Override
    protected G_List<DC_HeroObj> createList() {
        return new G_List<>(data);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public ObjListMouseListener<DC_HeroObj> getMouseListener() {
        return new ObjListMouseListener<DC_HeroObj>(getList()) {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (SwingUtilities.isRightMouseButton(e)) { // to avoid this if
                    // SELECTING
                    DC_Game.game.getBattleField().centerCameraOn((Obj) getSelected());
                }
            }

        };
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    protected void resetData() {
        getList().setData(getData());
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DC_HeroObj> list,
                                                  DC_HeroObj value, int index, boolean isSelected, boolean cellHasFocus) {
        /**
         * TODO emblem AP highlight? highlight when infoselected on bf
         */
        PriorityListItem priorityListItem = new PriorityListItem(value);
        priorityListItem.setEmptyIcon(ImageManager.getAltEmptyListIcon());
        priorityListItem.refresh();
        return priorityListItem;
    }

}
