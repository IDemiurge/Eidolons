package libgdx.gui.dungeon.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.DC_Obj;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.dungeon.tooltips.ToolTipManager;
import libgdx.gui.dungeon.tooltips.ValueTooltip;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.stage.Closable;
import libgdx.stage.StageWithClosable;
import eidolons.content.consts.Sprites;
import libgdx.assets.texture.TextureCache;
import eidolons.system.audio.SoundController;
import eidolons.system.audio.SoundController.SOUND_EVENT;
import main.content.enums.GenericEnums;
import main.entity.Entity;
import main.system.EventCallbackParam;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;
import main.system.math.MathMaster;

import java.util.Collections;
import java.util.List;

import static main.system.GuiEventType.RADIAL_MENU_CLOSE;
import static main.system.GuiEventType.UPDATE_GUI;

public class RadialMenu extends Group implements Closable {
    protected RadialContainer currentNode;
    SpriteAnimation background;

    protected RadialContainer closeButton;
    protected int radius;
    protected boolean ready = true;

    public RadialMenu() {
        final TextureRegion t = TextureCache.getOrCreateR(getCloseNodePath());
        closeButton = new RadialContainer(new TextureRegion(t), this::close);
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Collections.singletonList(new ValueContainer("Close", "")));
        closeButton.addListener(tooltip.getController());
        initBackground();
        bindEvents();
        getColor().a = 0;
        setVisible(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (background != null) {
            if (background != null)
                updateBackground(0);
            background.setScale(getColor().a);
            if (batch instanceof CustomSpriteBatch) {
                ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
            }
            try {
                background.draw(batch);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (batch instanceof CustomSpriteBatch) {
                ((CustomSpriteBatch) batch).resetBlending();
            }
        }
        super.draw(batch, parentAlpha);
    }

    protected void initBackground() {
        if (!Flags.isLiteLaunch()) {
            background = SpriteAnimationFactory.getSpriteAnimation(getBackgroundSpritePath(), false);
            background.setFrameDuration(0.05f);
        }
    }

    protected String getBackgroundSpritePath() {
        return Sprites.RADIAL;
    }

    protected void bindEvents() {
        addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return event.getTarget() == RadialMenu.this || super.mouseMoved(event, x, y);
            }
        });
        GuiEventManager.bind(UPDATE_GUI, obj -> {
            RadialManager.clearCache();
        });

        GuiEventManager.bind(RADIAL_MENU_CLOSE, obj -> {
            close();
        });
        GuiEventManager.bind(getOpenEvent(), obj -> {
            triggered(obj);
            GuiEventManager.trigger(GuiEventType.GRID_SET_VIEW, obj);
            GuiEventManager.trigger(GuiEventType.GRID_SCREEN);
        });
    }

    protected EventType getOpenEvent() {
        return GuiEventType.CREATE_RADIAL_MENU;
    }

    protected void triggered(EventCallbackParam obj) {
        if (!(obj.get() instanceof DC_Obj))
            return;
        if ((obj.get() instanceof WeaponItem))
            return;
        DC_Obj dc_obj = (DC_Obj) obj.get();
        init(RadialManager.getOrCreateRadialMenu(dc_obj));
    }

    public void close() {
        ToolTipManager.setPresetTooltipPos(null);
        if (currentNode == null)
            return;
        ready = false;
        if (!isVisible())
            return;
        if (isAnimated()) {
            currentNode.getChildNodes().forEach(child -> {
                ActionMasterGdx.addMoveToAction(child, currentNode.getX(), currentNode.getY(), getAnimationDuration());
            });
            ActionMasterGdx.addFadeOutAction(this, getAnimationDuration());
            ActionMasterGdx.addHideAfter(this);
        } else
            setVisible(false);
        SoundController.playCustomEventSound(SOUND_EVENT.RADIAL_CLOSED);
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }

    @Override
    public void act(float delta) {

        setVisible(getColor().a != 0.0f);
        super.act(delta);
    }

    protected Vector2 getBackgroundPosition() {
        if (closeButton != null)
            return //parentToLocalCoordinates
                    (localToStageCoordinates(
                            new Vector2(closeButton.getX() + 20, closeButton.getY())));
        return parentToLocalCoordinates(localToStageCoordinates(
                new Vector2(getX() + 20, getY())));
    }

    protected void updateBackground(float delta) {
        if (background == null) {
            return;
        }
        Vector2 pos = getBackgroundPosition();
        background.setOffsetX(pos.x);
        background.setOffsetY(pos.y);
    }

    protected float getBackgroundRotationPerSecond() {
        return 1;
    }


    public void init(List<RadialContainer> nodes) {
        currentNode = closeButton;
        Vector2 v2 = getInitialPosition();
        setPosition(v2.x + getOffsetX(), v2.y);

        closeButton.setChildNodes(nodes);

        setParents(closeButton, null);

        nodes.forEach(node -> node.setCustomRadialMenu(this));

        if (closeButton.getChildren().size < 1) {
            return;
        }
        if (closeButton.getChildNodes().size() < 1) {
            return;
        }
        setCurrentNode(closeButton);
    }

    protected Vector2 getInitialPosition() {
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        return getStage().screenToStageCoordinates(v2);
    }

    protected float getOffsetX() {
        return -20;
    }

    protected void setParents(RadialContainer el, RadialContainer parent) {
        el.setParent(parent);
        el.getChildNodes().forEach(inn -> setParents(inn, el));
    }

    protected void setCurrentNode(RadialContainer node) {
        clearChildren();
        addActor(node);
        currentNode = node;
        updateCallbacks();
        currentNode.getChildNodes().forEach(this::addActor);
        open();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    public void open() {
        if (getStage() instanceof StageWithClosable) {
            ((StageWithClosable) getStage()).setDisplayedClosable(this);
        }
        currentNode.setChildVisible(true);
        setVisible(true);
        updatePosition();
        setColor(new Color(1, 1, 1, 0));
        adjustPosition();
        //  TODO fade out the old nodes       ActorMaster.addChained
        //         (this, ActorMaster.addFadeOutAction(this, getAnimationDuration()/2),
        ActionMasterGdx.addFadeInAction(this, getAnimationDuration());
        ActionMasterGdx.addAfter(this, new Action() {
            @Override
            public boolean act(float delta) {
                ready = true;
                return true;
            }
        });
        ActionMasterGdx.addRotateByAction(closeButton, -90);

        updateBackground(0);
    }

    protected void adjustPosition() {
        float w = getWidth();
        float h = getHeight();

        float x = MathMaster.minMax(getX(),
                w / 2, GdxMaster.getWidth() - w);
        float y = MathMaster.minMax(getY(),
                h / 2, GdxMaster.getHeight() - h);
        ActionMasterGdx.addMoveToAction(this, x, y, 1);

    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (isVisible()) {
            Vector2 pos = parentToLocalCoordinates
                    (localToStageCoordinates(
                            new Vector2(getX(), getY())));

            ToolTipManager.setPresetTooltipPos(getTooltipPos(pos.x + getWidth() / 3 * 2, pos.y + getHeight() / 3 * 2));
        }
    }

    protected Vector2 getTooltipPos(float x, float y) {
        return new Vector2(x, y);
    }

    protected float getMinCoef() {
        return 1.4f;
    }

    protected float getMaxCoef() {
        return 3.25f;
    }

    protected void updatePosition() {
        int step = getSpectrumDegrees() / currentNode.getChildNodes().size();
        int initial = getStartDegree();

        //        double coefficient = currentNode.getChildNodes().size() > 6 ? 2 : 1.5;
        //        if (currentNode.getChildNodes().size() > 10) {
        //            coefficient = 2.5;
        //        }
        double coefficient = MathMaster.getMinMax((float) (currentNode.
                getChildNodes().size() / (Math.PI + 1)), getMinCoef(), getMaxCoef());
        boolean makeSecondRing = isMakeSecondRing(currentNode.getChildNodes().size());
        if (makeSecondRing) {
            coefficient = 3.5;
        }
        int xMax = 0;
        int xMin = 0;
        int yMax = 0;
        int yMin = 0;

        radius = (int) (getRadiusBase() * coefficient);
        final List<RadialContainer> children = currentNode.getChildNodes();
        for (int i = 0; i < children.size(); i++) {
            final RadialContainer valueContainer = children.get(i);
            int r = radius;
            if (makeSecondRing && i % 2 == 0) {
                r = (int) (72 * (coefficient - 1));
            }
            int pos = initial + i * step;
            if (isClockwise()) {
                pos = initial - i * step;
            }
            int y = (int) (r * Math.sin(Math.toRadians(pos)));
            int x = (int) (r * Math.cos(Math.toRadians(pos)));

            if (y < yMin)
                yMin = y;
            if (x < xMin)
                xMin = x;
            if (x > xMax)
                xMax = x;
            if (y > yMax)
                yMax = y;

            Vector2 v = new Vector2(x + currentNode.getX(), y + currentNode.getY());


            if (isAnimated()) {
                valueContainer.setPosition(currentNode.getX(), currentNode.getY());
                ActionMasterGdx.addMoveToAction(valueContainer, v.x, v.y, 0.5f);
            } else
                valueContainer.setPosition(v.x, v.y);
        }
        setSize(xMax - xMin, yMax - yMin);
    }

    protected double getRadiusBase() {
        return 72;
    }

    protected boolean isMakeSecondRing(int size) {
        return size > 15;
    }


    protected boolean isClockwise() {
        return false;
    }

    protected int getStartDegree() {
        return 90;
    }

    protected int getSpectrumDegrees() {
        return 360;
    }

    protected float getAnimationDuration() {
        return 0.65f;
    }

    protected String getCloseNodePath() {
        return "ui/components/dc/radial/empty.png";
    }

    protected boolean isAnimated() {
        return true;
    }

/*
    final int length = childs.size() * (step+1);
        for (int i = 90, c = 0; i <= length; i += step, c++) {
        final RadialValueContainer valueContainer = childs.getVar(c);
        int r = radius;
        if (makeSecondRing && c % 2 == 0) {
            r = (int) (72 * (coefficient - 1));
        }
        int y = (int) (r * Math.sin(Math.toRadians(180)));
        int x = (int) (r * Math.cos(Math.toRadians(180)));

        valueContainer.setPosition(x + currentNode.getX(), y + currentNode.getY());
    }*/

    protected void updateCallbacks() {
        if (currentNode.getParent() != null) {
            currentNode.bindAction(() -> setCurrentNode(currentNode.getParent()));
        }
        for (final RadialContainer child : currentNode.getChildNodes()) {
            if (child.getChildNodes().size() > 0) {
                child.bindAction(() -> setCurrentNode(child));
            } else {
                Runnable action = child.getClickAction();
                child.bindAction(() -> {
                    close();
                    action.run();
                });
            }
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null && currentNode != null) {
            Vector2 v = new Vector2(currentNode.getX(), currentNode.getY());
            Vector2 v2 = new Vector2(x, y);
            v = currentNode.localToParentCoordinates(v);
            final int cradius = radius + 32;
            Rectangle rect = new Rectangle(
                    v.x - cradius, v.y - cradius,
                    cradius * 2, cradius * 2);
            if (rect.contains(v2)) {
                actor = this;
            }
        }
        return actor;
    }

    public void hover(Entity entity) {
        //        for (Actor sub : getChildren()) {
        //        }
    }

    public void hoverOff(Entity entity) {
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
