package main.level_editor.gui.palette;


import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.gui.panels.TabbedPanel;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;

import java.util.Arrays;
import java.util.Collection;

public class PaletteHolder extends TabbedPanel {

    public static Collection<? extends OBJ_TYPE> tabTypes=
            Arrays.asList(DC_TYPE.BF_OBJ, DC_TYPE.UNITS);

    public PaletteHolder(){
        super();
        addTab(new UpperPalette(DC_TYPE.BF_OBJ) , "Objects");
        addTab(new UpperPalette(DC_TYPE.UNITS) , "Units");
//        addTab(new ObjectPalette(), "Vfx");
//        addTab(new ObjectPalette(), "Templates");
//        addTab(new ObjectPalette(), "Groups");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
