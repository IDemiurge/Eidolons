package main.libgdx.bf.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.List;

public class RadialMenu extends Group {
    private Texture closeTex;
    private MenuNode currentNode;
    private Image closeImage;

    public RadialMenu() {
        final Texture t = new Texture(RadialMenu.class.getResource("/data/marble_green.png").getPath());
        closeImage = new Image(t);
        this.closeTex = t;
    }

    public void init(List<CreatorNode> nodes) {
        currentNode = new MenuNode(closeImage, "Close");
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setBounds(
                v2.x - currentNode.getWidth() / 2,
                v2.y - currentNode.getHeight() / 2,
                currentNode.getWidth(),
                currentNode.getHeight()
        );

        currentNode.setX(getX());
        currentNode.setY(getY());

        currentNode.childNodes = createChildren(currentNode, nodes);

        final RadialMenu menu = this;
        currentNode.action = () -> menu.setVisible(false);

        updateCallbacks();
        updatePosition();
        currentNode.drawChildren = true;
        setVisible(true);
        //addActor(currentNode);
    }

    private void setCurrentNode(MenuNode node) {
        removeActor(currentNode);
        currentNode.drawChildren = false;
        currentNode = node;
        updatePosition();
        updateCallbacks();
        currentNode.drawChildren = true;
        // addActor(currentNode);
    }

    private void updatePosition() {

        int step = 360 / currentNode.childNodes.size();
        int pos;
        int r = (int) (currentNode.getWidth() * 1.5);

        for (int i = 0; i < currentNode.childNodes.size(); i++) {
            pos = i * step;
            int y = (int) (r * Math.sin(Math.toRadians(pos + 90)));
            int x = (int) (r * Math.cos(Math.toRadians(pos + 90)));
            currentNode.childNodes.get(i).setX(x + currentNode.getX());
            currentNode.childNodes.get(i).setY(y + currentNode.getY());
        }
    }

    private void updateCallbacks() {
        if (currentNode.parent == null) {
            currentNode.action = () -> setVisible(false);
        } else {
            currentNode.action = () -> setCurrentNode(currentNode.parent);
        }
        for (final MenuNode child : currentNode.childNodes) {
            if (child.childNodes.size() > 0) {
                child.action = () -> setCurrentNode(child);
            }
        }
    }

    private List<MenuNode> createChildren(final MenuNode parent, final List<CreatorNode> creatorNodes) {
        List<MenuNode> menuNodes = new ArrayList<>();
        for (final CreatorNode node : creatorNodes) {
            final MenuNode menuNode = new MenuNode(
                    node.texture == null ?
                            new Image(closeTex)
                            : new Image(node.texture), node.name,
                    node.w, node.h
            );
            menuNode.parent = parent;
            if (node.action != null) {
                final Runnable r = node.action;
                final RadialMenu menu = this;
                menuNode.action = () -> {
                    r.run();
                    menu.setVisible(false);
                };
            } else {
                menuNode.setChildren(createChildren(menuNode, node.childNodes));
            }
            menuNodes.add(menuNode);
            //parent.addActor(menuNode);
        }
        return menuNodes;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (!isVisible() || currentNode == null) {
            return null;
        }
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        return currentNode.hit(v2.x, v2.y, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible() || currentNode == null) {
            return;
        }
        currentNode.draw(batch, parentAlpha);
    }


    public static class CreatorNode {
        public Texture texture;
        public String name;
        public List<CreatorNode> childNodes;
        public Runnable action;
        public float w;
        public float h;
    }

    public class MenuNode extends Group {
        public MenuNode parent = null;
        public Runnable action = null;
        private List<MenuNode> childNodes = new ArrayList<>();
        private boolean drawChildren = false;
        private Image image;
        private Label text = null;

        public MenuNode(Image image, String text) {
            this(image, text, image.getWidth(), image.getHeight());
        }

        public MenuNode(Image image, String text, float w, float h) {
//            addActor(image);
            if (text != null && text.length() > 0) {
                this.text = new Label(text, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            }
            this.image = image;
            if (w != 0) {
                image.setWidth(w);
            }
            if (h != 0) {
                image.setHeight(h);
            }
            setHeight(image.getHeight());
            setWidth(image.getWidth());

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (action == null) {
                        LogMaster.log(1, "action == null");
                    } else {

                        action.run();
                    }
                    event.stop();
                    return true;
                }

                @Override
                public boolean mouseMoved(InputEvent event, float x, float y) {
                    return true;
                }
            });
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            image.setX(x);
            if (text != null) {
                text.setX(x);
            }
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            image.setY(y);
            if (text != null) {
                text.setY(y);
            }
        }

        public void setChildren(List<MenuNode> childs) {
            this.childNodes = childs;
            for (MenuNode child : childs) {
                addActor(child);
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            image.draw(batch, parentAlpha);
            if (text != null) {
                text.draw(batch, parentAlpha);
            }
            if (drawChildren) {
                for (MenuNode child : childNodes) {
                    child.draw(batch, parentAlpha);
                }
            }
        }

        @Override
        public Actor hit(float x, float y, boolean touchable) {
            Actor a = image.hit(x - image.getX(), y - image.getY(), touchable);
            if (a != null) {
                a = this;
            }
            if (a == null && drawChildren) {
                for (MenuNode child : childNodes) {
                    a = child.hit(x, y, touchable);
                    if (a != null) {
                        a = child;
                        break;
                    }
                }
            }
            return a;
        }
    }
}
