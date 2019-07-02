package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub.imbue;

import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.ArmorTooltip;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.SlotItemTooltip;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.WeaponTooltip;

public class ImbueItemInfo extends TablePanelX {

    public ImbueItemInfo() {

    }

    @Override
    public void updateAct(float delta) {

        DC_HeroItemObj item = (DC_HeroItemObj) getUserObject();

        if (item instanceof DC_WeaponObj) {
            SlotItemTooltip tooltip = new WeaponTooltip((DC_WeaponObj) item);
            add(tooltip);
        } else {
            if (item instanceof DC_ArmorObj) {
                SlotItemTooltip tooltip = new ArmorTooltip((DC_ArmorObj) item);
                add(tooltip);
            }
        }

        super.updateAct(delta);

    }
}
