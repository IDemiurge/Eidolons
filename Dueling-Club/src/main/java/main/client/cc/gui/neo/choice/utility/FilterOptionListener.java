package main.client.cc.gui.neo.choice.utility;

import main.client.dc.Launcher;
import main.swing.listeners.OptionListener;

public class FilterOptionListener<E> implements OptionListener<E> {

    @Override
    public void optionSelected(E e) {
        // workspace, level cap,

        Launcher.getMainManager().getSequence().getView().setFilterOption(e.toString());

    }

}
