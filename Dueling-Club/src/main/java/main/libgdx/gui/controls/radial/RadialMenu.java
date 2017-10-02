package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.entity.Entity;
import main.entity.obj.DC_Obj;
import main.libgdx.anims.ActorMaster;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.audio.SoundController;
import main.system.audio.SoundController.SOUND_EVENT;

import java.util.Arrays;
import java.util.List;

import static main.system.GuiEventType.CREATE_RADIAL_MENU;
import static main.system.GuiEventType.UPDATE_GUI;

public class RadialMenu extends Group {
    private RadialValueContainer currentNode;

    private RadialValueContainer closeButton;
    private int radius;

    public RadialMenu() {
        final TextureRegion t = TextureCache.getOrCreateR(getEmptyNodePath());
        closeButton = new RadialValueContainer(new TextureRegion(t), this::close);
        closeButton.setX(-20);

        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer("Close", "")));
        closeButton.addListener(tooltip.getController());

        addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return event.getTarget() == RadialMenu.this || super.mouseMoved(event, x, y);
            }
        });

        GuiEventManager.bind(UPDATE_GUI, obj -> {
            RadialManager.clearCache();
        });

        GuiEventManager.bind(CREATE_RADIAL_MENU, obj -> {
            DC_Obj dc_obj = (DC_Obj) obj.get();
            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                init(DebugRadialManager.getDebugNodes(dc_obj));
            } else {
                init(RadialManager.getOrCreateRadialMenu(dc_obj));
            }
        });
    }

    public void close() {
        if (isAnimated()) {
            ActorMaster.addFadeOutAction(this, getAnimationDuration());
            currentNode.getChildNodes().forEach(child -> {
                ActorMaster.addMoveToAction(child, currentNode.getX(), currentNode.getY(), 0.5f);
            });
            ActorMaster.addRotateByAction(closeButton, 90);
            ActorMaster.addHideAfter(this);
        } else
            setVisible(false);
        SoundController.playCustomEventSound(SOUND_EVENT.RADIAL_CLOSED);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }


    public void init(List<RadialValueContainer> nodes) {
        currentNode = closeButton;
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x, v2.y);
        closeButton.setChildNodes(nodes);

        setParents(closeButton, null);
        if (closeButton.getChildren().size < 1) {
            return;
        }
        if (closeButton.getChildNodes().size() < 1) {
            return;
        }
        setCurrentNode(closeButton);
    }

    private void setParents(RadialValueContainer el, RadialValueContainer parent) {
        el.setParent(parent);
        el.getChildNodes().forEach(inn -> setParents(inn, el));
    }

    private void setCurrentNode(RadialValueContainer node) {
        clearChildren();
        addActor(node);
        currentNode = node;
        updateCallbacks();
        currentNode.getChildNodes().forEach(this::addActor);
        open();
    }

    private void open() {
        currentNode.setChildVisible(true);
        setVisible(true);
        updatePosition();
        setColor(new Color(1, 1, 1, 0));
//  TODO fade out the old nodes       ActorMaster.addChained
//         (this, ActorMaster.addFadeOutAction(this, getAnimationDuration()/2),
        ActorMaster.addFadeInAction(this, getAnimationDuration()

        );
        ActorMaster.addRotateByAction(closeButton, -90);
    }

    private void updatePosition() {
        int step = 360 / currentNode.getChildNodes().size();
        int pos;

        double coefficient = currentNode.getChildNodes().size() > 6 ? 2 : 1.5;

        if (currentNode.getChildNodes().size() > 10) {
            coefficient = 2.5;
        }
        boolean makeSecondRing = false;
        if (currentNode.getChildNodes().size() > 15) {
            makeSecondRing = true;
            coefficient = 3.5;
        }

        radius = (int) (72 * coefficient);
        final List<RadialValueContainer> childs = currentNode.getChildNodes();
        for (int i = 0; i < childs.size(); i++) {
            final RadialValueContainer valueContainer = childs.get(i);
            int r = radius;
            if (makeSecondRing && i % 2 == 0) {
                r = (int) (72 * (coefficient - 1));
            }
            pos = i * step;
            int y = (int) (r * Math.sin(Math.toRadians(pos + 90)));
            int x = (int) (r * Math.cos(Math.toRadians(pos + 90)));
            Vector2 v = new Vector2(x + currentNode.getX(), y + currentNode.getY());
            if (isAnimated()) {
                valueContainer.setPosition(currentNode.getX(), currentNode.getY());
                ActorMaster.addMoveToAction(valueContainer, v.x, v.y, 0.5f);
            } else
                valueContainer.setPosition(v.x, v.y);
        }
    }

    private float getAnimationDuration() {
        return 0.5f;
    }

    private String getEmptyNodePath() {
        return "UI\\components\\2017\\radial\\empty.png";
//        return "UI\\components\\2017\\radial\\empty dark.png";
//        return "UI\\components\\2017\\radial\\empty dark.png";
    }

    private boolean isAnimated() {
        return true;
    }

/*    final List<RadialValueContainer> childs = currentNode.getChildNodes();
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

    private void updateCallbacks() {
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
}
