package eidolons.libgdx.gui.panels.headquarters.creation.misc;

import eidolons.libgdx.gui.panels.headquarters.creation.general.SelectionImageTable;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.GuiEventType;

/**
 * Created by JustMe on 6/25/2018.
 */
public class HcPortraitPanel extends SelectionImageTable {
    public HcPortraitPanel( ) {
        super(4, 24);
    }

    @Override
    protected GuiEventType getEvent() {
        return GuiEventType.HC_PORTRAIT_CHOSEN;
    }

    @Override
    protected PROPERTY getProperty() {
        return G_PROPS.IMAGE;
    }
}
