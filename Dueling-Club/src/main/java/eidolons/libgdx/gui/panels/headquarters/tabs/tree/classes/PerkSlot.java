package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.entity.obj.attach.HeroClass;
import eidolons.entity.obj.attach.Perk;
import eidolons.game.module.herocreator.logic.PerkMaster;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HtNode;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.SlotSelectionRadialMenu;
import eidolons.libgdx.texture.Images;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.ListMaster;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public class PerkSlot extends HtNode {

    private Triple<Perk, HeroClass, HeroClass> data;
    private List<ObjType> available;

    public PerkSlot(int tier,int slot) {
        super(tier, Images.SMALL_TIER, Images.DIAMOND_OVERLAY, Images.DIAMOND_UNDERLAY, slot);
    }


    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (Triple<Perk, HeroClass, HeroClass>) userObject;
    }

    @Override
    protected void click() {
        if (ListMaster.isNotEmpty(available)) {
            SlotSelectionRadialMenu.setActiveNode(this);
            GuiEventManager.trigger(GuiEventType.SHOW_PERK_CHOICE, available);
        } else {
        }
    }

    @Override
    protected EventType getSelectionEvent() {
        return GuiEventType.SHOW_PERK_CHOICE;
    }

    @Override
    public List<ObjType> getAvailable() {
        return available;
    }

    @Override
    protected void doubleClick() {
        if (ListMaster.isNotEmpty(available)) {
            GuiEventManager.trigger(GuiEventType.SHOW_PERK_CHOICE, available);
        } else {
        }
    }

    @Override
    protected String getTextPrefix() {
        return "Tier " + NumberUtils.getRoman(tier) + " Perk";
    }

    @Override
    protected Entity getEntity() {
        if (data != null)
            return data.getLeft();
        return null;
    }
    public void update(float delta) {
        if (data == null)
            disable();
        else if (data.getLeft() != null) {
            enable();
            GdxImageMaster.round(data.getLeft().getImagePath(), true);
            setRootPath(GdxImageMaster.getRoundedPath(data.getLeft().getImagePath()));
        } else {
            resetToOriginal();
            if (data.getMiddle() != null)
            if (data.getRight() != null) {
            HeroClass c1 = data.getMiddle();
            HeroClass c2 = data.getRight();
            available = PerkMaster.getAvailablePerks(hero,
             tier, c1, c2);
            if (available.isEmpty())
                disable();
        }
        }
        super.update(delta);

    }
}
