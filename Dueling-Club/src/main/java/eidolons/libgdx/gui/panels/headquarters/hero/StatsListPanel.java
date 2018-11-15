package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GDX;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.AbstractValueContainer;
import main.content.VALUE;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/14/2018.
 */
public class StatsListPanel extends HqVerticalValueTable {
    public StatsListPanel(VALUE[] values) {
        super(false, values);
    }

    @Override
    protected void updateContainer(AbstractValueContainer sub, VALUE val) {
        super.updateContainer(sub, val);
        float w = GDX.width(100) + 100;
        if (sub.getActor().getWidth()>w){
            sub.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 15));
        }
        sub.setValueStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 19));
        sub.getActor().setSize(w, GDX.height(20)+20);
    }

    @Override
    protected int getDefaultAlign() {
        return Align.left;
    }

    @Override
    protected LabelStyle getLabelStyle() {
        return StyleHolder.getSizedLabelStyle(FONT.MAIN, 17);
    }
}
