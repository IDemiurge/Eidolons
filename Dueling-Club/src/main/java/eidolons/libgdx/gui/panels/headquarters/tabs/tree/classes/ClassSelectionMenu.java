package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.game.module.herocreator.logic.HeroClassMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.SlotSelectionRadialMenu;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/9/2018.
 */
public class ClassSelectionMenu extends SlotSelectionRadialMenu {
    protected EventType getEvent() {
        return   GuiEventType.SHOW_CLASS_CHOICE;
    }

    @Override
    protected HERO_OPERATION getOperation() {
        return HERO_OPERATION.NEW_CLASS;
    }
    @Override
    protected String getImagePath(ObjType type) {
        return HeroClassMaster.getImgPath(type);
    }
    @Override
    protected String getReqReason(ObjType type) {
        return  dataSource.getEntity() .getGame().getRequirementsManager()
         .check(dataSource.getEntity() , type);
    }
}
