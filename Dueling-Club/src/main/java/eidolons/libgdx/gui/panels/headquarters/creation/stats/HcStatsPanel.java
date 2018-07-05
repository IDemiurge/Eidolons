package eidolons.libgdx.gui.panels.headquarters.creation.stats;

import eidolons.content.PARAMS;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqMasteryTable;

/**
 * Created by JustMe on 7/4/2018.
 */
public class HcStatsPanel extends HqElement {

    HqAttributeTable attributeTable;
    HqMasteryTable masteryTable;

    public HcStatsPanel() {
        add(attributeTable = new HqAttributeTable() {
            @Override
            protected void modify(PARAMS datum) {
                super.modify(datum);
                HeroCreationMaster.checkRevert();
            }
        });
    }

    @Override
    protected void update(float delta) {

    }
}
