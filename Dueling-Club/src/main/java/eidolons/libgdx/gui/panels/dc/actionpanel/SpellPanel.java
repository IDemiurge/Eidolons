package eidolons.libgdx.gui.panels.dc.actionpanel;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.SpellDataSource;
import eidolons.libgdx.texture.Images;

import java.util.List;

public class SpellPanel extends BaseSlotPanel {
    public SpellPanel() {
        super(0);
    }

    protected int getPageSize() {
        return 5;
    }

    public SpellPanel(int imageSize) {
        super(imageSize);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final SpellDataSource source = (SpellDataSource) getUserObject();
        if (source == null) {
            return;
        }
        final List<ValueContainer> sources = source.getSpells();
        initContainer(sources, Images.EMPTY_SPELL);
    }
}
