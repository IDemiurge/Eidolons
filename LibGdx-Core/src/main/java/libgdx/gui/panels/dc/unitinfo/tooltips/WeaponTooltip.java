package libgdx.gui.panels.dc.unitinfo.tooltips;

import eidolons.entity.item.DC_WeaponObj;

public class WeaponTooltip extends SlotItemTooltip {
    public WeaponTooltip(DC_WeaponObj weapon) {
        setUserObject(new SlotItemToolTipDataSource(weapon));
    }

}
