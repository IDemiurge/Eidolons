
package eidolons.libgdx.gui.panels.headquarters.tabs.tree;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.HeroClassMaster;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.bf.DynamicLayeredActor;
import eidolons.libgdx.bf.SpriteActor;
import eidolons.libgdx.bf.SpriteActor.SPRITE_ACTOR_ANIMATION;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.text.TextParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public abstract class HtNode extends DynamicLayeredActor {

    protected boolean sequentialDisabled;
    protected boolean editable;
    protected Unit hero;
    protected int tier;
    protected int slot;
    protected List<ObjType> available;

    public HtNode(int tier, String rootPath, String overlay, String underlay, int slot) {
        super(getRootPath(rootPath, tier), overlay, underlay);
        this.tier = tier;
        this.slot = slot;
        init();
    }

    @Override
    public void disable() {
        super.disable();
        sequentialDisabled= true;
    }

    protected boolean isImgOnTop() {
        return false;
    }

    protected String getSpritePathRoot() {
        return PathFinder.getHqPath() +
                "trees/sprites/";
    }


    private static String getRootPath(String rootPath, int tier) {
        return StringMaster.getAppendedImageFile(rootPath, " " + (tier + 1));
    }

    protected float getDefaultWidth() {
        return 64;
    }

    protected float getDefaultHeight() {
        return 64;
    }



    public void update(float delta) {
        updateListeners();
    }

    private void updateListeners() {
        clearListeners();
        Tooltip tooltip = getTooltip();
        if (tooltip != null)
            addListener(tooltip.getController());
        addListener(new SmartClickListener(this) {
            @Override
            protected void onDoubleClick(InputEvent event, float x, float y) {
                doubleClick();
            }

            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                click();
            }

            @Override
            protected void entered() {
                super.entered();
                mouseEntered();
//                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, getTooltip());
            }

            @Override
            protected void exited() {
                super.exited();
                mouseExited();
//                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
            }
        });
    }

    protected abstract List<ObjType> createAvailable() ;



    public float getPeriod() { // alt
        switch (status) {
            case HOVER:
            case NORMAL:
            case DISABLED:
            case ACTIVE:
                break;
        }
        return 2.5f;
    }

    protected Tooltip getTooltip() {
        Entity entity = getEntity();
        String text = getTextPrefix();
        if (entity != null && !HeroClassMaster.isDataAnOpenSlot(entity) ) {
            Ref ref = Eidolons.getMainHero().getRef().getCopy();
            ref.setID(KEYS.SKILL, entity.getId());
            text += "\n" + entity.getName();
            text += "\n" + entity.getProperty(G_PROPS.TOOLTIP);
            text += "\n" + TextParser.parse(entity.getDescription(),
                    ref, TextParser.TOOLTIP_PARSING_CODE, TextParser.INFO_PARSING_CODE);
        } else
        if (sequentialDisabled) {
            text += "\n(Disabled: fill the previous slot!)";
        }
        return new ValueTooltip(text);
    }

    protected abstract String getTextPrefix();

    protected Entity getEntity() {
        return null;
    }

    protected void click() {
//      TODO   HqMaster.filterContent(getAvailable());
        if (ListMaster.isNotEmpty(getAvailable())) {

            SlotSelectionRadialMenu.setActiveNode(this);
            GuiEventManager.trigger(getSelectionEvent(), getAvailable(), tier, slot);
        } else {
            FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.REQUIREMENT,
                    "Nothing available for this slot yet!",   Eidolons.getMainHero());
//            GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT,
//                    "Nothing available for this slot yet!");
        }
    }

    protected abstract EventType getSelectionEvent();

    protected abstract Collection<ObjType> getAvailable();

    protected void doubleClick() {
        click();

    }

    protected void mouseEntered() {
        setStatus(ACTOR_STATUS.HOVER);
    }

    protected void mouseExited() {
        setStatus(defaultStatus);
    }


    public void setHero(Unit hero) {
        this.hero = hero;
    }

    public Unit getHero() {
        if (hero == null) {
            return HqPanel.getActiveInstance().getSelectedHero().getEntity();
        }
        return hero;
    }
}
