package main.level_editor.gui.palette;

import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;

public class UpperPalette extends TablePanelX {
    public UpperPalette(DC_TYPE TYPE) {
        super(1200, 400);
//        defaults().
        PaletteTypesTable palette = new PaletteTypesTable(1);
        add(new PaletteTabs(palette, TYPE).getTable()). height(134).expandX().fillX().row();
        add(palette).expandX().fillX(). height(256).row();
    }
}
