package main.gui.components.controls;

import main.swing.generic.components.G_CompHolder;
import main.utilities.sorting.SortBox;

import java.awt.*;

public class SpecialPanel extends G_CompHolder {

    private SortBox sortBox;
    private Checkbox autoSaveSwitcher;

    public SpecialPanel() {
        super();
    }

    public void initComp() {
        this.sortBox = new SortBox();

        this.autoSaveSwitcher = new Checkbox();

//		comp.added(sortBox);
    }
}
