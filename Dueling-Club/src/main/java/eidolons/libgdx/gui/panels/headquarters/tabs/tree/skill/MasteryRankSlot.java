package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HtNode;
import eidolons.libgdx.texture.Images;
import eidolons.system.text.DescriptionTooltips;
import main.content.ContentValsManager;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.parameters.PARAMETER;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public class MasteryRankSlot extends HtNode {

    private MASTERY data;
    private PARAMETER param;

    public MasteryRankSlot(
     int tier,int slot) {
        super(tier, Images.SMALL_TIER, Images.DIAMOND_OVERLAY, Images.DIAMOND_UNDERLAY,slot );
    }

    @Override
    protected String getSlotTooltip() {
        return DescriptionTooltips.MASTERY_SLOT;
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
    protected List<ObjType> createAvailable() {
        return null;
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
    protected void init() {
       setSize(getDefaultWidth(), getDefaultHeight());
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
            return "Mastery Slot, Rank " + NumberUtils.getRoman(tier+1);
        return StringMaster.format(data.name()) + " Rank " + NumberUtils.getRoman(tier+1);
    }
    @Override
    protected EventType getSelectionEvent() {
        return null ;
    }

    @Override
    public List<ObjType> getAvailable() {
        return null ;
    }

    @Override
    protected void click() {

    }

    @Override
    protected void doubleClick() {

    }
}
