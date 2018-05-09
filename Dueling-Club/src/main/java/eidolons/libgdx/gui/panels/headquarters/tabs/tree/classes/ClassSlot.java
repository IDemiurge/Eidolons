package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.entity.obj.attach.HeroClass;
import eidolons.game.module.herocreator.logic.HeroClassMaster;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.nodes.HtNode;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.texture.Images;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public class ClassSlot extends HtNode {
    private List<ObjType> available;
    private HeroClass data;

    public ClassSlot(int tier) {
        super(tier, Images.EMPTY_CLASS_SLOT);
    }


    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (HeroClass) userObject;
    }

    @Override
    protected void click() {
        if (ListMaster.isNotEmpty(available)) {
            GuiEventManager.trigger(GuiEventType.SHOW_CLASS_CHOICE, available);
        } else {
        }
    }

    @Override
    protected void doubleClick() {
        if (ListMaster.isNotEmpty(available)) {
            GuiEventManager.trigger(GuiEventType.SHOW_CLASS_CHOICE, available);
        } else {
        }
    }

    public void update(float delta) {
        if (data != null) {
            enable();
            GdxImageMaster.round(data.getImagePath(), true);
            setRootPath(GdxImageMaster.getRoundedPath(data.getImagePath()));
        } else {
            available = HeroClassMaster.getAvailableClasses(hero,
             tier);
            if (available.isEmpty())
                disable();
        }

    }

    @Override
    protected Tooltip getTooltip() {
        return null;
    }
}
