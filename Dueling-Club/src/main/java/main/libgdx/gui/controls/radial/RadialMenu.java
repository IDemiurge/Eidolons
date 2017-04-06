package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.List;

public class RadialMenu extends Group {
    private RadialValueContainer currentNode;

    private RadialValueContainer closeButton;
    private int radius;

    public RadialMenu() {
        final Texture t = new Texture(RadialMenu.class.getResource("/data/marble_green.png").getPath());
        closeButton = new RadialValueContainer(new TextureRegion(t), () -> RadialMenu.this.setVisible(false));
        closeButton.setX(-20);

        addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (event.getTarget() == RadialMenu.this) {
                    System.out.println("inside");
                    return true;
                } else {
                    return super.mouseMoved(event, x, y);
                }
            }
        });
    }

    public void init(List<RadialValueContainer> nodes) {
        currentNode = closeButton;
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x, v2.y);
        closeButton.setChilds(nodes);

        setParents(closeButton, null);

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
        System.out.println(getX() + " " + getY() + " | " + currentNode.getX() + " " + currentNode.getY());
    }

    public void updatePosition() {
        int step = 360 / currentNode.getChilds().size();
        int pos;

        final double coefficient = currentNode.getChilds().size() > 6 ? 2 : 1.5;
        radius = (int) (72 * coefficient);

        for (int i = 0; i < currentNode.getChilds().size(); i++) {
            pos = i * step;
            int y = (int) (radius * Math.sin(Math.toRadians(pos + 90)));
            int x = (int) (radius * Math.cos(Math.toRadians(pos + 90)));
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
            }
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null && currentNode != null) {
            Vector2 v2 = new Vector2(x, y);
            //v2 = parentToLocalCoordinates(v2);
            Vector2 v = new Vector2(currentNode.getX(), currentNode.getY());
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
