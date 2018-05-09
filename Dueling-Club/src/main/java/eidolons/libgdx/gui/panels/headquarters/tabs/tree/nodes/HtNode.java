package eidolons.libgdx.gui.panels.headquarters.tabs.tree.nodes;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.bf.DynamicLayeredActor;
import eidolons.libgdx.bf.SpriteActor;
import eidolons.libgdx.bf.SpriteActor.SPRITE_ACTOR_ANIMATION;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.Tooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;

/**
 * Created by JustMe on 5/6/2018.
 */
public abstract class HtNode extends DynamicLayeredActor {

    protected boolean editable;
    protected Unit hero;
    protected int tier;
    SpriteActor sprite = new SpriteActor();

    public HtNode(  int tier, String rootPath) {
        super(rootPath);
        this.tier = tier;
        setSize(getDefaultWidth(), getDefaultHeight() );
        addListener(new SmartClickListener(this) {
            @Override
            protected void onDoubleTouchDown(InputEvent event, float x, float y) {
                doubleClick();
            }

            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                click();
            }

            @Override
            protected void entered() {
                mouseEntered();
                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, getTooltip());
            }

            @Override
            protected void exited() {
                mouseExited();
                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
            }
        });
    }

    protected float getDefaultWidth() {
        return 64;
    }
    protected float getDefaultHeight() {
        return 64;
    }

    protected void playStateAnim() {
        boolean alt = RandomWizard.chance(getAltChance(status));
       sprite. play(getAnimForStatus(status, alt));

    }

    protected int getAltChance(ACTOR_STATUS status) {
        return 25;
    }

    protected SPRITE_ACTOR_ANIMATION getAnimForStatus(ACTOR_STATUS status, boolean alt) {
        switch (status) {
            case HOVER:
                //TODO on status changed?
                if (alt)
                    return SPRITE_ACTOR_ANIMATION.FLASH;
                return SPRITE_ACTOR_ANIMATION.SCUD_OVER;
            case NORMAL:
                if (alt)
                    return SPRITE_ACTOR_ANIMATION.SCUD_OVER;
                return SPRITE_ACTOR_ANIMATION.FADE_IN_OUT;
            case DISABLED:
                return SPRITE_ACTOR_ANIMATION.FADE_IN_OUT;
            case ACTIVE:
                if (alt)
                    return SPRITE_ACTOR_ANIMATION.FLASH;
                return SPRITE_ACTOR_ANIMATION.FADE_IN_OUT;
        }
        return null;
    }
    public void update(float delta) {
    }
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

    protected abstract Tooltip getTooltip();

    protected abstract void click();

    protected void mouseEntered() {

    }

    protected void mouseExited() {

    }

    protected abstract void doubleClick();

    public void setHero(Unit hero) {
        this.hero = hero;
    }
}
