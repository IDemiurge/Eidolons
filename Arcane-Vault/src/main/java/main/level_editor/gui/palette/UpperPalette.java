package main.level_editor.gui.palette;

import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;

public class UpperPalette extends TablePanelX {
    public UpperPalette(DC_TYPE TYPE) {
        super(1200, 400);
//        defaults().
        PaletteTypesTable palette = new PaletteTypesTable(0);
        add(new PaletteTabs(palette, TYPE).getTable()). height(64).expandX().fillX().row();
        add(palette).expandX().fillX(). maxHeight(400-64).row();
    }
}
