
package eidolons.libgdx.gui.panels.headquarters.tabs.tree;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.HeroClassMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.DynamicLayeredActor;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.VerticalValueContainer;
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
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.text.TextParser;

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
        sequentialDisabled = true;
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

    protected abstract List<ObjType> createAvailable();


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
        String name = getTextPrefix();
        String text;
        if (entity != null && !HeroClassMaster.isDataAnOpenSlot(entity)) {
            name = entity.getName();
            text = getTooltipText();
        } else {
            text = getSlotTooltip();

            if (sequentialDisabled)
                text += "\n(Disabled: fill the previous slot!)";
        }
        ValueContainer container = new VerticalValueContainer(name, text);
        container.setNameAlignment(Align.center);
        container.setNameStyle(StyleHolder.getHqLabelStyle(19));
        return new ValueTooltip(container);
    }

    protected String getTooltipText() {
        Entity entity = getEntity();
        String text = "";
        Ref ref = getHero().getRef().getCopy();
        ref.setID(KEYS.SKILL, entity.getId());
        ref.setID(KEYS.INFO, entity.getId());

        text += entity.getProperty(G_PROPS.TOOLTIP);
        text += "\n" + TextParser.parse(entity.getDescription(ref),
                ref, TextParser.VARIABLE_PARSING_CODE, TextParser.TOOLTIP_PARSING_CODE, TextParser.INFO_PARSING_CODE);

        if (!entity.getProperty(G_PROPS.LORE).isEmpty()) {
            text += "\n" + "\n" + entity.getProperty(G_PROPS.LORE);
        }
        text+= "\n" +getSpecialInfo();
        return text;
    }

    protected String getSpecialInfo() {
        return "";
    }

    protected abstract String getSlotTooltip();

    protected abstract String getTextPrefix();

    protected Entity getEntity() {
        return null;
    }

    protected void click() {
//      TODO   HqMaster.filterContent(isAvailable());
        if (ListMaster.isNotEmpty(getAvailable())) {

            SlotSelectionRadialMenu.setActiveNode(this);
            GuiEventManager.triggerWithParams(getSelectionEvent(), getAvailable(), tier, slot);
        } else {
            EUtils.showInfoText("Nothing available for this slot yet!");
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
            hero = HqPanel.getActiveInstance().getSelectedHero().getEntity();
        }
        return hero;
    }
}
