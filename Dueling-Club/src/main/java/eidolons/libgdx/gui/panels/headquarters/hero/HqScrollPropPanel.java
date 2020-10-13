package eidolons.libgdx.gui.panels.headquarters.hero;

import eidolons.content.DC_ContentValsManager;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import main.content.VALUE;

/**
 * Created by JustMe on 11/15/2018.
 */
public class HqScrollPropPanel extends HqElement {

    public HqScrollPropPanel() {
        this(500, 230);
    }

    public HqScrollPropPanel(float w, float h) {
        setSize(w, h);
        HqVerticalValueTable valueTable = new StatsListPanel(getValuesOne());
        valueTable.setSize(w, h);
        ScrollPanel scroll;
        add(scroll = new ScrollPanel() {
            @Override
            public int getDefaultOffsetY() {
                return 0;
            }

            @Override
            public Integer getScrollAmount() {
                return super.getScrollAmount();
            }

            @Override
            protected float getUpperLimit() {
                return -1;
            }

            @Override
            protected float getLowerLimit() {
                return -1;
            }

        });
        scroll.addElement(valueTable);
        scroll.pad(1, 10, 1, 10);
        scroll.fill();
    }

    @Override
    protected void update(float delta) {

    }

    private VALUE[] getValuesOne() {
        return DC_ContentValsManager.getDisplayedHeroProperties();
    }
}
