package libgdx.gui.dungeon.panels.headquarters.tabs.tree.skill;

import eidolons.entity.unit.attach.DC_PassiveObj;
import eidolons.netherflame.eidolon.heromake.passives.SkillMaster;
import libgdx.GdxImageMaster;
import libgdx.gui.dungeon.panels.headquarters.tabs.tree.HtNode;
import eidolons.content.consts.Images;
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

    private Triple<DC_PassiveObj, MASTERY, MASTERY> data;

    public SkillSlot(int tier,int slot) {
        super(tier, Images.TIER, Images.CIRCLE_OVERLAY, Images.EMPTY_CLASS_SLOT,slot );
    }

    public void update(float delta) {
        if (data == null) {
            disable();
        } else {
            if (isImgOnTop())
                image.setZIndex(999);
            enable();
            available = new ArrayList<>();
            if (data.getLeft() != null  )
            if (SkillMaster.isDataAnOpenSlot(data)  )
            {
                resetToOriginal();
                if (data.getMiddle() != null)
                    if (data.getRight() != null)
                        available =createAvailable();
                //set image to N or X
            } else {
                GdxImageMaster.round(data.getLeft().getImagePath(), true, "");
                setRootPath(SkillMaster.getSkillImgPath(data.getLeft()));
                //check last?
                //or compare to prev data?
                // or just allow to play it only once
//                if (!animPlayed){
//                    playStateAnim();
//                    animPlayed= true;
//                }
            }
        }
        super.update(delta);
    }

    protected List<ObjType> createAvailable() {
      return   SkillMaster.getAllSkills(getHero(), tier, data.getMiddle(), data.getRight());
    }

    @Override
    protected String getSlotTooltip() {
        return "Fill the two Mastery Rank slots above to buy a skill for Experience points, chosen from the pool of the two related masteries. If the two are the same, some skills can be chosen twice.";
    }


    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (Triple<DC_PassiveObj, MASTERY, MASTERY>) userObject;
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
