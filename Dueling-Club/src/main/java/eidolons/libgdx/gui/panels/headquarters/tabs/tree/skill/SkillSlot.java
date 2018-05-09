package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.nodes.HtNode;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.texture.Images;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ListMaster;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public class SkillSlot extends HtNode {

    private Triple<DC_FeatObj, MASTERY, MASTERY> data;
    private List<ObjType> available;

    public SkillSlot(int tier) {
        super(tier, Images.EMPTY_SKILL_SLOT);
    }

    public void update(float delta) {
        if (data == null) {
            disable();
        } else {
            enable();
            if (data.getLeft() != null) {
                GdxImageMaster.round(data.getLeft().getImagePath() , true);
                setRootPath(GdxImageMaster.getRoundedPath(data.getLeft().getImagePath()));
            } else {
                if (data.getMiddle()!=null )
                    if (data.getRight()!=null )
                available = SkillMaster.getAvailableSkills(hero, tier, data.getMiddle(), data.getRight());
                //set image to N or X
            }
        }
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (Triple<DC_FeatObj, MASTERY, MASTERY>) userObject;
//        tooltip = new SkillTooltip(data);
    }

    @Override
    protected Tooltip getTooltip() {
        return null;
    }

    @Override
    protected void click() {
        if (ListMaster.isNotEmpty(available)) {
            GuiEventManager.trigger(GuiEventType.SHOW_SKILL_CHOICE, available);
        } else {
        }
    }

    @Override
    protected void doubleClick() {
        if (ListMaster.isNotEmpty(available)) {
            GuiEventManager.trigger(GuiEventType.SHOW_SKILL_CHOICE, available);
        } else {
        }

    }
}
