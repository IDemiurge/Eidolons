package main.swing.listeners;

import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.listeners.ListChooserSortOptionListener.SORT_TEMPLATE;

public class ListChooserSortOptionListener implements OptionListener<SORT_TEMPLATE> {
    @Override
    public void optionSelected(SORT_TEMPLATE e) {
        ListChooser.sortData(e);

    }

    public enum SORT_TEMPLATE {
        BY_ID, ALPHABETIC, GROUP, SUBGROUP,
    }

}
