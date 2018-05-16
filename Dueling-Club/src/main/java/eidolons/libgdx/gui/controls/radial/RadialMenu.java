package eidolons.libgdx.gui.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.AnimMaster;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.stage.Closable;
import eidolons.libgdx.stage.StageWithClosable;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.audio.SoundController;
import eidolons.system.audio.SoundController.SOUND_EVENT;
import main.entity.Entity;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.math.MathMaster;

import java.util.Arrays;
import java.util.List;

import static main.system.GuiEventType.*;

public class RadialMenu extends Group implements Closable {
    protected RadialValueContainer currentNode;

    protected RadialValueContainer closeButton;
    protected int radius;
    protected boolean ready = true;

    public RadialMenu() {
        final TextureRegion t = TextureCache.getOrCreateR(getEmptyNodePath());
        closeButton = new RadialValueContainer(new TextureRegion(t), this::close);
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer("Close", "")));
        closeButton.addListener(tooltip.getController());

        bindEvents();
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
        GuiEventManager.bind(CREATE_RADIAL_MENU, obj -> {
            triggered(obj);

        });
    }

    protected void triggered(EventCallbackParam obj) {
        if (!(obj.get() instanceof DC_Obj))
            return;
        if ((obj.get() instanceof DC_WeaponObj))
            return;
        DC_Obj dc_obj = (DC_Obj) obj.get();
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            init(DebugRadialManager.getDebugNodes(dc_obj));
        } else {
            init(RadialManager.getOrCreateRadialMenu(dc_obj));
        }
    }

    public void close() {
        if (currentNode == null)
            return;
        ready = false;
        if (isAnimated()) {
            currentNode.getChildNodes().forEach(child -> {
                ActorMaster.addMoveToAction(child, currentNode.getX(), currentNode.getY(), getAnimationDuration());
            });
            ActorMaster.addFadeOutAction(this, getAnimationDuration());
            ActorMaster.addHideAfter(this);
        } else
            setVisible(false);
        SoundController.playCustomEventSound(SOUND_EVENT.RADIAL_CLOSED);
    }

    @Override
    public void act(float delta) {
        //TODO fix
        if (getColor().a == 0.0f)
            setVisible(false);
        else {
            setVisible(true);
        }
        super.act(delta);
    }


    public void init(List<RadialValueContainer> nodes) {
        currentNode = closeButton;
        Vector2 v2 = getInitialPosition();
        setPosition(v2.x + getOffsetX(), v2.y);

        closeButton.setChildNodes(nodes);

        setParents(closeButton, null);

        nodes.forEach(node-> node.setCustomRadialMenu(this));

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

    protected void setParents(RadialValueContainer el, RadialValueContainer parent) {
        el.setParent(parent);
        el.getChildNodes().forEach(inn -> setParents(inn, el));
    }

    protected void setCurrentNode(RadialValueContainer node) {
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
        ActorMaster.addFadeInAction(this, getAnimationDuration());
        ActorMaster.addAfter(this, new Action() {
            @Override
            public boolean act(float delta) {
                ready = true;
                return true;
            }
        });
        ActorMaster.addRotateByAction(closeButton, -90);
    }

    protected void adjustPosition() {
        float w = getWidth();
        float h = getHeight();

        float x=MathMaster.minMax(getX(),
         w/2, GdxMaster.getWidth()-w );
        float y=MathMaster.minMax(getY(),
         h/2, GdxMaster.getHeight()-h );
        ActorMaster.addMoveToAction(this, x, y, 1);

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
                 getChildNodes().size() / (Math.PI+1)), getMinCoef(), getMaxCoef());
        boolean makeSecondRing = isMakeSecondRing(currentNode.getChildNodes().size());
        if (makeSecondRing ) {
            coefficient = 3.5;
        }
        int xMax=0;
        int xMin=0;
        int yMax=0;
        int yMin=0;

        radius = (int) (72 * coefficient);
        final List<RadialValueContainer> children = currentNode.getChildNodes();
        for (int i = 0; i < children.size(); i++) {
            final RadialValueContainer valueContainer = children.get(i);
            int r = radius;
            if (makeSecondRing && i % 2 == 0) {
                r = (int) (72 * (coefficient - 1));
            }
            int pos = initial + i * step;
            if (isClockwise()) {
                pos = initial - i * step;
            }
            int y = (int) (r * Math.sin(Math.toRadians(pos )));
            int x = (int) (r * Math.cos(Math.toRadians(pos )));

            if (y<yMin)
                yMin=y;
            if (x<xMin)
                xMin=x;
            if (x>xMax)
                xMax=x;
            if (y>yMax)
                yMax=y;

            Vector2 v = new Vector2(x + currentNode.getX(), y + currentNode.getY());



            if (isAnimated()) {
                valueContainer.setPosition(currentNode.getX(), currentNode.getY());
                ActorMaster.addMoveToAction(valueContainer, v.x, v.y, 0.5f);
            } else
                valueContainer.setPosition(v.x, v.y);
        }
        setSize(xMax-xMin,yMax-yMin);
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
        return 0.2f * AnimMaster.getInstance().getAnimationSpeedFactor();
    }

    protected String getEmptyNodePath() {
        return "UI\\components\\dc\\radial\\empty.png";
//        return "UI\\components\\dc\\radial\\empty dark.png";
//        return "UI\\components\\dc\\radial\\empty dark.png";
    }

    protected boolean isAnimated() {
        return true;
    }

/*
    final int length = childs.size() * (step+1);
        for (int i = 90, c = 0; i <= length; i += step, c++) {
        final RadialValueContainer valueContainer = childs.get(c);
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
        for (final RadialValueContainer child : currentNode.getChildNodes()) {
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
