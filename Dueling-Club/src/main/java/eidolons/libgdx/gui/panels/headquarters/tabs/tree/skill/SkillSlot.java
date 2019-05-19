package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HtNode;
import eidolons.libgdx.texture.Images;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public class SkillSlot extends HtNode {

    private Triple<DC_FeatObj, MASTERY, MASTERY> data;
    private List<ObjType> available;

    public SkillSlot(int tier,int slot) {
        super(tier, Images.TIER, Images.CIRCLE_OVERLAY, Images.CIRCLE_UNDERLAY,slot );
    }

    public void update(float delta) {
        if (data == null) {
//            clearImage();
            disable();
        } else {
            enable();
            available = new ArrayList<>();
            if (data.getLeft() != null  )
            if (!data.getLeft().getName().equalsIgnoreCase(SkillMaster.DUMMY_SKILL)  )
            {
                GdxImageMaster.round(data.getLeft().getImagePath(), true);
                setRootPath(SkillMaster.getSkillImgPath(data.getLeft()));
            } else {

                resetToOriginal();
                if (data.getMiddle() != null)
                    if (data.getRight() != null)
                        available = SkillMaster.getAllSkills(getHero(), tier, data.getMiddle(), data.getRight());
                //set image to N or X
            }
        }
        super.update(delta);
    }




    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (Triple<DC_FeatObj, MASTERY, MASTERY>) userObject;
//        tooltip = new SkillTooltip(data);
    }


    @Override
    protected String getTextPrefix() {
        return "Tier " + NumberUtils.getRoman(tier+1) + " Skill";
    }

    @Override
    protected Entity getEntity() {
        if (data != null)
            return data.getLeft();
        return null;
    }

    @Override
    protected EventType getSelectionEvent() {
        return GuiEventType.SHOW_SKILL_CHOICE;
    }

    @Override
    public List<ObjType> getAvailable() {
        return available;
    }


}
