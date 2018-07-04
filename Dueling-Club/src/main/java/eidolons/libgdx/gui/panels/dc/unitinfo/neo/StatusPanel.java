package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import main.content.values.properties.G_PROPS;

/**
 * Created by JustMe on 6/30/2018.
 */
public class StatusPanel extends HqVerticalValueTable{
    public StatusPanel() {
        super(G_PROPS.MODE, G_PROPS.STATUS);
    }
}
