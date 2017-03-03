package main.swing.components.panels;

import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.core.state.MicroGameState;
import main.game.logic.battle.turn.TurnManager;
import main.swing.components.PriorityListItem;
import main.swing.generic.components.panels.G_ListPanel;
import main.swing.generic.services.listener.ObjListMouseListener;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.math.MathMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Selection => info select on BF (or redirect click?) Render: display AP
 */
public class DC_PriorityListPanel extends G_ListPanel<Unit> implements
        ListCellRenderer<Unit> {

    public static final int PLP_MIN_ITEMS = MathMaster.getMinMax(GuiManager
            .getBF_CompDisplayedCellsY() * 2, 8, 12);
    private TurnManager manager;

    public DC_PriorityListPanel(MicroGameState state) {
        super(state);
        getList().setCellRenderer(this);
    }

    @Override
    public ObjListMouseListener<Unit> getMouseListener() {
        return new ObjListMouseListener<Unit>(getList()) {
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
    public void setInts() {
        sizeInfo = "h 5+" + PLP_MIN_ITEMS + "*" + GuiManager.getSmallObjSize() + "!";
        vpolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
        rowsVisible = PLP_MIN_ITEMS;
        minItems = PLP_MIN_ITEMS;
        hpolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        layoutOrientation = JList.VERTICAL;
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    protected void resetData() {

        getList().setData(getData());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Unit> getData() {
        LinkedList<Unit> newData = new LinkedList(getManager().getDisplayedUnitQueue());
        ListMaster.fillWithNullElements(newData, PLP_MIN_ITEMS);
        // newData.pop();
        return newData;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Unit> list,
                                                  Unit value, int index, boolean isSelected, boolean cellHasFocus) {
        /**
         * TODO emblem AP highlight? highlight when infoselected on bf
         */
        PriorityListItem priorityListItem = new PriorityListItem(value);
        priorityListItem.setEmptyIcon(ImageManager.getAltEmptyListIcon());
        return priorityListItem;
    }

    public TurnManager getManager() {
        if (manager == null) {
            manager = state.getGame().getTurnManager();
        }
        return manager;
    }

    public void setManager(TurnManager manager) {
        this.manager = manager;
    }

}
