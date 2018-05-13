package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HtNode;
import eidolons.libgdx.texture.Images;
import main.content.ContentValsManager;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 5/6/2018.
 */
public class MasteryRankSlot extends HtNode {

    private MASTERY data;
    private PARAMETER param;

    public MasteryRankSlot(
     int tier) {
        super(tier, Images.SMALL_TIER, Images.DIAMOND_OVERLAY, Images.DIAMOND_UNDERLAY);
    }

    @Override
    public void update(float delta) {
        if (param != null)
            setRootPath(ImageManager.getValueIconPath(param));
        else
            resetToOriginal();
        super.update(delta);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (MASTERY) userObject;
        if (data != null)
            param = ContentValsManager.getPARAM(data.name());
        else param = null;
    }

    @Override
    protected float getDefaultWidth() {
        return 50;
    }

    @Override
    protected float getDefaultHeight() {
        return 50;
    }

    @Override
    protected String getTextPrefix() {
        if (data == null)
            return "Mastery Slot, Rank " + StringMaster.getRoman(tier);
        return StringMaster.getWellFormattedString(data.name()) + " Rank " + StringMaster.getRoman(tier);
    }


    @Override
    protected void click() {

    }

    @Override
    protected void doubleClick() {

    }
}
