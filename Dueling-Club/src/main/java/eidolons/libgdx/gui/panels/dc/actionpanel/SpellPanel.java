package eidolons.libgdx.gui.panels.dc.actionpanel;

import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.SpellDataSource;

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

        final List<ActionValueContainer> sources = source.getSpells();
        initContainer(sources, "UI/EMPTY_LIST_ITEM.jpg");
    }
}
