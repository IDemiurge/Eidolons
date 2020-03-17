package main.level_editor.gui.palette;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;
import main.level_editor.gui.palette.tree.PaletteTree;


public class UpperPalette extends TablePanelX {
    public UpperPalette(DC_TYPE TYPE) {
        if (isTreeMode()){
//            setSize(400, 900);
            if (isScrollMode()) {
                ScrollPane scr = new ScrollPane(new PaletteTree(TYPE));
                add(scr).expandX().fillX();
            } else {
                add(new PaletteTree(TYPE));
            }
            return;
        }
        setSize(1200, 400);
        PaletteTypesTable palette = new PaletteTypesTable(0);
        add(new PaletteTabs(palette, TYPE).getTable()). height(64).expandX().fillX().row();
        add(palette).expandX().fillX(). maxHeight(400-64).row();
    }

    private boolean isScrollMode() {
        return false;
    }

    private boolean isTreeMode() {
        return true;
    }
}
