package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.gui.panels.dc.actionpanel.EffectsPanel;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.hero.HqParamPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqTraitsPanel;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;

/**
 * Created by JustMe on 5/14/2018.
 *
 * requirements
 *
 * > easy to show in Menu
 * > Compact and resizable
 * > Readable and non-scary
 * > Use ninepatches properly and generated tiled panels
 *
 * Layout changes
 *
 * The core is the FullParamTable
 *
 *
 * which panels really have to change?
 * apart from the "look'n'feel"?
 *
 * Weapon
 * > Attack icons will be welcome
 * big tabs with sprites, max space for attacks
 *
 *
 * > Fx and abils - how will I make it for HQ ?
 * :: IN A ROW ! expandable in the future
 *
 */
public class UnitInfoPanelNew  extends HqElement{

    FullParamTable paramTable;
    HqAttributeTable attributeTable;
    HqTraitsPanel traitsPanel;
    EffectsPanel effectsPanel;

    HqParamPanel paramPanel;
    HqParamPanel dynamicParamPanel;

    public UnitInfoPanelNew() {
        attributeTable = new HqAttributeTable();
        attributeTable.setEditable(false);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);

    }

    @Override
    protected void update(float delta) {

    }
}
