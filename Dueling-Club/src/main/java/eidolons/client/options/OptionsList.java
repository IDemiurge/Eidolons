package eidolons.client.options;

import main.swing.generic.components.list.GenericList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class OptionsList<GAME_OPTION> extends GenericList<GAME_OPTION> {

    boolean SINGLE_SELECTION;

    public OptionsList(boolean single) {
        super();
        this.SINGLE_SELECTION = single;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GAME_OPTION> list, GAME_OPTION value, int index, boolean isSelected, boolean cellHasFocus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

}
