package libgdx.gui.panels.headquarters.tabs.tree.skill;

import com.badlogic.gdx.math.Vector2;
import eidolons.netherflame.eidolon.heromake.passives.SkillMaster;
import libgdx.gui.panels.headquarters.tabs.tree.SlotSelectionRadialMenu;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/8/2018.
 */
public class SkillSelectionMenu extends SlotSelectionRadialMenu {

    protected Vector2 getBackgroundPosition() {
        return parentToLocalCoordinates(localToStageCoordinates(
                new Vector2(getX() + 20, getY() - 11)));
    }
    @Override
    protected int getIconSize() {
        return 64;
    }
    protected EventType getEvent() {
        return   GuiEventType.SHOW_SKILL_CHOICE;
    }

    @Override
    protected HeroDataModel.HERO_OPERATION getOperation() {
        return HeroDataModel.HERO_OPERATION.NEW_SKILL;
    }

    @Override
    protected String getImagePath(ObjType type) {
        return (type).getImagePath();
    }

    @Override
    protected String getReqReason(ObjType type) {
        return  SkillMaster.getReqReasonForSkill(dataSource.getEntity(), type);
    }

}
