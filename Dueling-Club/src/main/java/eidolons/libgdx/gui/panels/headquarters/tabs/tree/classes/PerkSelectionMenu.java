package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.SlotSelectionRadialMenu;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/9/2018.
 */
public class PerkSelectionMenu extends SlotSelectionRadialMenu {

    protected EventType getEvent() {
        return   GuiEventType.SHOW_PERK_CHOICE;
    }

    @Override
    protected HQ_OPERATION getOperation() {
        return HQ_OPERATION.NEW_PERK;
    }

    @Override
    protected String getReqReason(ObjType type) {
        return  dataSource.getEntity() .getGame().getRequirementsManager()
         .check(dataSource.getEntity() , type);
    }
    protected boolean isFree() {
        return true;
    }
}
