package libgdx.gui.dungeon.panels.headquarters.tabs.tree.classes;

import com.badlogic.gdx.math.Vector2;
import eidolons.netherflame.eidolon.heromake.passives.HeroClassMaster;
import libgdx.gui.dungeon.panels.headquarters.tabs.tree.SlotSelectionRadialMenu;
import main.content.enums.entity.HeroEnums;
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
    protected HeroEnums.HERO_OPERATION getOperation() {
        return HeroEnums.HERO_OPERATION.NEW_CLASS;
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
