package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.SlotSelectionRadialMenu;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/9/2018.
 */
public class PerkSelectionMenu extends SlotSelectionRadialMenu {

    protected Vector2 getBackgroundPosition() {
        return super.getBackgroundPosition();
    }
    @Override
    protected int getIconSize() {
        return 50;
    }
    protected EventType getEvent() {
        return   GuiEventType.SHOW_PERK_CHOICE;
    }

    @Override
    protected HERO_OPERATION getOperation() {
        return HERO_OPERATION.NEW_PERK;
    }

    @Override
    protected String getImagePath(ObjType type) {
        return type.getImagePath();
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
