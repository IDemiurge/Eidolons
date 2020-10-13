package eidolons.libgdx.gui.panels.headquarters.creation.selection.skillset;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqMasteryTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqNewMasteryPanel;

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
                datum = (PARAMS) DC_ContentValsManager.getBaseAttr(datum);
                main.system.auxiliary.log.LogMaster.log(1,datum+" before = " +getUserObject().getParam(datum));
                super.modify(datum);
                main.system.auxiliary.log.LogMaster.log(1,datum+"= " +getUserObject().getParam(datum));
                HeroCreationMaster.checkRevert();
            }
        }) ;
        add(masteryTable = new HqMasteryTable() {
            @Override
            protected void modify(PARAMS datum) {
                super.modify(datum);
                HeroCreationMaster.checkRevert();
            }
        });
        row();
        HqNewMasteryPanel newMastery;
        add(newMastery = new HqNewMasteryPanel());
        attributeTable.setEditable(true);
        masteryTable.setEditable(true);
    }

    @Override
    protected void update(float delta) {

    }

}
