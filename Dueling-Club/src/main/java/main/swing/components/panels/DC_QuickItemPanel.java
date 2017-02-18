package main.swing.components.panels;

import main.entity.item.DC_QuickItemObj;
import main.game.core.state.MicroGameState;
import main.swing.generic.components.panels.G_ListPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Comparator;

public class DC_QuickItemPanel extends G_ListPanel<DC_QuickItemObj> implements
        ListCellRenderer<DC_QuickItemObj>, Comparator<DC_QuickItemObj> {

    public DC_QuickItemPanel(MicroGameState state) {
        super(state);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setInts() {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection<DC_QuickItemObj> getData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DC_QuickItemObj> list, DC_QuickItemObj value, int index, boolean isSelected, boolean cellHasFocus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int compare(DC_QuickItemObj o1, DC_QuickItemObj o2) {
        // TODO Auto-generated method stub
        return 0;
    }

}
