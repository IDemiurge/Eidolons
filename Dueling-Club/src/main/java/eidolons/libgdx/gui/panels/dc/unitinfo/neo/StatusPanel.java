package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import main.content.values.properties.G_PROPS;

/**
 * Created by JustMe on 6/30/2018.
 */
public class StatusPanel extends HqVerticalValueTable{
    public StatusPanel() {
        super(G_PROPS.MODE, G_PROPS.STATUS);

    }
    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
    }

    @Override
    protected int getDefaultAlign() {
        return Align.left;
    }
}
