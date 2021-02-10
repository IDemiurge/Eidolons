package libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.content.PROPS;
import eidolons.entity.obj.attach.HeroClass;
import eidolons.game.module.herocreator.logic.HeroClassMaster;
import libgdx.gui.panels.headquarters.tabs.tree.HtNode;
import libgdx.texture.Images;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;

import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public class ClassSlot extends HtNode {
    private List<ObjType> available;
    private HeroClass data;

    public ClassSlot(int tier, int slot) {
        super(tier, Images.TIER, Images.CIRCLE_OVERLAY, Images.CIRCLE_UNDERLAY, slot);
    }

    @Override
    protected String getSpecialInfo() {
        return
                HeroClassMaster.getPerkInfo(getEntity()) + "\n" +
                        HeroClassMaster.getNextClassInfo(getEntity());
    }

    @Override
    protected String getSlotTooltip() {
//        Unlocked Class Trees:
//        //check any is valid -> highlight
//        available.size()

        String text = "Fill this slot with a Class Rank that you qualify for from an unlocked Class Tree";
        String sfx = ContainerUtils.construct("\n", getHero().getProperty(PROPS.FIRST_CLASS),
                getHero().getProperty(PROPS.SECOND_CLASS), getHero().getProperty(PROPS.THIRD_CLASS));
        return text + "\n" + sfx;
    }

    @Override
    protected void init() {
        setSize(getDefaultWidth(), getDefaultHeight());
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (HeroClass) userObject;
    }

    @Override
    protected EventType getSelectionEvent() {
        return GuiEventType.SHOW_CLASS_CHOICE;
    }

    @Override
    public List<ObjType> getAvailable() {
        return available;
    }

    @Override
    protected String getTextPrefix() {
        return "Tier " + NumberUtils.getRoman(tier + 1) + " Class";
    }

    @Override
    protected Entity getEntity() {
        if (data != null)
            return data;
        return null;
    }

    public void update(float delta) {
        if (data != null) {
            image.setZIndex(999);
            enable();
            setRootPath(
                    HeroClassMaster.getImgPath(data)
            );
//            GdxImageMaster.round(data.getImagePath(), true);
//            setRootPath(GdxImageMaster.getRoundedPath(data.getImagePath()));
            if (HeroClassMaster.isDataAnOpenSlot(data)) {
                available = createAvailable();
            }
        } else {
            resetToOriginal();
            available = null;
            disable();
            //TODO block(); if no reqs
        }

        super.update(delta);
    }

    @Override
    protected List<ObjType> createAvailable() {
        return HeroClassMaster.getClassesToChooseFrom(getHero(),
                tier);
    }


}
