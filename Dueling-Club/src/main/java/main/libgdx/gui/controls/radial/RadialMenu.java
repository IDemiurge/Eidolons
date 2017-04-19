package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.texture.TextureCache;

import java.util.Arrays;
import java.util.List;

public class RadialMenu extends Group {
    private RadialValueContainer currentNode;

    private RadialValueContainer closeButton;
    private int radius;

    public RadialMenu() {
        final TextureRegion t = TextureCache.getOrCreateR(getEmptyNodePath());
//        new Texture(RadialMenu.class.getResource(
//         /data/marble_green.png).getPath());
        closeButton = new RadialValueContainer(new TextureRegion(t), () ->
                close());
        closeButton.setX(-20);

        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer("Close", "")));
        closeButton.addListener(tooltip.getController());

        addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (event.getTarget() == RadialMenu.this) {
                    return true;
                } else {
                    return super.mouseMoved(event, x, y);
                }
            }
        });
    }

    public void close() {
        RadialMenu.this.setVisible(false);
    }

    private String getEmptyNodePath() {
        return "UI\\components\\2017\\radial\\empty.png";
//        return "UI\\components\\2017\\radial\\empty dark.png";
//        return "UI\\components\\2017\\radial\\empty dark.png";
    }

    public void init(List<RadialValueContainer> nodes) {
        currentNode = closeButton;
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x, v2.y);
        closeButton.setChilds(nodes);

        setParents(closeButton, null);
        if (closeButton.getChildren().size < 1) {
            return;
        }
        setCurrentNode(closeButton);
    }

    private void setParents(RadialValueContainer el, RadialValueContainer parent) {
        el.setParent(parent);
        el.getChilds().forEach(inn -> setParents(inn, el));
    }

    private void setCurrentNode(RadialValueContainer node) {
        clearChildren();
        addActor(node);
        currentNode = node;
        updateCallbacks();
        currentNode.getChilds().forEach(this::addActor);
        currentNode.setChildVisible(true);
        updatePosition();
        setVisible(true);
    }

    public void updatePosition() {
        int step = 360 / currentNode.getChilds().size();
        int pos;

        double coefficient = currentNode.getChilds().size() > 6 ? 2 : 1.5;

        if (currentNode.getChilds().size() > 10) {
            coefficient = 2.5;
        }
        boolean makeSecondRing = false;
        if (currentNode.getChilds().size() > 15) {
            makeSecondRing = true;
            coefficient = 3.5;
        }

        radius = (int) (72 * coefficient);

        for (int i = 0; i < currentNode.getChilds().size(); i++) {
            int r = radius;
            if (makeSecondRing && i % 2 == 0) {
                r = (int) (72 * (coefficient - 1));
            }
            pos = i * step;
            int y = (int) (r * Math.sin(Math.toRadians(pos + 90)));
            int x = (int) (r * Math.cos(Math.toRadians(pos + 90)));
            currentNode.getChilds().get(i).setPosition(x + currentNode.getX(), y + currentNode.getY());
        }
    }

    private void updateCallbacks() {
        if (currentNode.getParent() != null) {
            currentNode.bindAction(() -> setCurrentNode(currentNode.getParent()));
        }
        for (final RadialValueContainer child : currentNode.getChilds()) {
            if (child.getChilds().size() > 0) {
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
}
