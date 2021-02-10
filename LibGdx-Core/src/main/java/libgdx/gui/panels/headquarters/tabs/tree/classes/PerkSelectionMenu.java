package libgdx.gui.panels.headquarters.tabs.tree.classes;

import com.badlogic.gdx.math.Vector2;
import libgdx.gui.panels.headquarters.tabs.tree.SlotSelectionRadialMenu;
import libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/9/2018.
 */
public class PerkSelectionMenu extends SlotSelectionRadialMenu {

    protected Vector2 getBackgroundPosition() {
        if (closeButton != null)
            return  //parentToLocalCoordinates
                    (  localToStageCoordinates(
                            new Vector2(closeButton.getX()+20, closeButton.getY() )));
        return parentToLocalCoordinates(  localToStageCoordinates(
                new Vector2(getX()+20, getY())));
    }
    @Override
    protected int getIconSize() {
        return 50;
    }
    protected EventType getEvent() {
        return   GuiEventType.SHOW_PERK_CHOICE;
    }

    @Override
    protected HeroDataModel.HERO_OPERATION getOperation() {
        return HeroDataModel.HERO_OPERATION.NEW_PERK;
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
