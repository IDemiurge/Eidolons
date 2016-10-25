package main.client.cc.gui.neo.choice.utility;

import main.client.dc.Launcher;
import main.swing.listeners.ListChooserSortOptionListener.SORT_TEMPLATE;
import main.swing.listeners.OptionListener;

public class SortOptionListener implements OptionListener<SORT_TEMPLATE> {

    @Override
    public void optionSelected(SORT_TEMPLATE e) {
        Launcher.getMainManager().getSequence().getView().setSorterOption(e.toString());
    }

}
