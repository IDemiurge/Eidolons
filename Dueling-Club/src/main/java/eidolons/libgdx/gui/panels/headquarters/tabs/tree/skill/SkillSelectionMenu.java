package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.SlotSelectionRadialMenu;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/8/2018.
 */
public class SkillSelectionMenu extends SlotSelectionRadialMenu {

    protected EventType getEvent() {
        return   GuiEventType.SHOW_SKILL_CHOICE;
    }

    @Override
    protected HQ_OPERATION getOperation() {
        return HQ_OPERATION.NEW_SKILL;
    }

    @Override
    protected String getReqReason(ObjType type) {
        return  SkillMaster.getReqReasonForSkill(dataSource.getEntity(), type);
    }

}