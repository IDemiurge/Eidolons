package main.level_editor.gui.palette;

import eidolons.libgdx.gui.panels.TabbedPanel;

public class PaletteHolder extends TabbedPanel {

    public void init(){
        addTab(new ObjectPalette(), "Objects");
//        addTab(new ObjectPalette(), "Units");
//        addTab(new ObjectPalette(), "Vfx");
//        addTab(new ObjectPalette(), "Templates");
//        addTab(new ObjectPalette(), "Groups");
    }
}
