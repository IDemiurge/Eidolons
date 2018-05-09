package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import eidolons.libgdx.gui.panels.headquarters.tabs.tree.nodes.HtNode;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.texture.Images;
import main.content.ContentValsManager;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.parameters.PARAMETER;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 5/6/2018.
 */
public class MasteryRankSlot extends HtNode {

    private MASTERY data;
    private PARAMETER param;

    public MasteryRankSlot(
                           int tier ) {
        super( tier, Images.EMPTY_RANK_SLOT);
    }

    @Override
    public void update(float delta) {
        if (param != null)
            setRootPath(ImageManager.getValueIconPath(param));
        super.update(delta);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (MASTERY) userObject;
        if (data!=null )
            param = ContentValsManager.getPARAM(data.name());
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
    protected Tooltip getTooltip() {
        return null;
    }

    @Override
    protected void click() {

    }

    @Override
    protected void doubleClick() {

    }
}
