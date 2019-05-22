package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import com.badlogic.gdx.math.Vector2;
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
        return GuiEventType.SHOW_CLASS_CHOICE;
    }

    protected Vector2 getBackgroundPosition() {
        if (closeButton != null)
            return
                    (  localToStageCoordinates(
                            new Vector2(closeButton.getX()+20 , closeButton.getY()  )));
        return parentToLocalCoordinates(  localToStageCoordinates(
                new Vector2(getX()+20, getY())));
    }

    @Override
    protected HERO_OPERATION getOperation() {
        return HERO_OPERATION.NEW_CLASS;
    }

    @Override
    protected int getIconSize() {
        return 96;
    }

    @Override
    protected String getImagePath(ObjType type) {
        return HeroClassMaster.getImgPathRadial(type);
    }

    @Override
    protected String getReqReason(ObjType type) {
        return dataSource.getEntity().getGame().getRequirementsManager()
                .check(dataSource.getEntity(), type);
    }
}
