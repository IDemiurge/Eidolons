package libgdx.gui.dungeon.panels.dc.unitinfo.tooltips;

import eidolons.entity.item.WeaponItem;

public class WeaponTooltip extends SlotItemTooltip {
    public WeaponTooltip(WeaponItem weapon) {
        setUserObject(new SlotItemToolTipDataSource(weapon));
    }

}
