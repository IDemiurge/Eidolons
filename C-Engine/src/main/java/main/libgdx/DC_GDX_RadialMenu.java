package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;
import java.util.List;

public class DC_GDX_RadialMenu extends Group {
    private MenuNode curentNode;

    public DC_GDX_RadialMenu(Texture green, List<CreatorNode> nodes) {
        curentNode = new MenuNode(new Image(green));
        curentNode.childs = createChilds(curentNode, nodes);
        setHeight(curentNode.getHeight());
        setWidth(curentNode.getWidth());

        final DC_GDX_RadialMenu menu = this;
        curentNode.action = new Runnable() {
            @Override
            public void run() {
                menu.setVisible(false);
            }
        };

        updateCallbacks();
        curentNode.drawChilds = true;
        //addActor(curentNode);
    }

    @Override
    protected void positionChanged() {
        curentNode.setX(getX());
        curentNode.setY(getY());
        updatePosition();
    }

    private void setCurentNode(MenuNode node) {
        removeActor(curentNode);
        curentNode.drawChilds = false;
        curentNode = node;
        updatePosition();
        updateCallbacks();
        curentNode.drawChilds = true;
        // addActor(curentNode);
    }

    private void updatePosition() {

        int step = 360 / curentNode.childs.size();
        int pos;
        int r = (int) (curentNode.getWidth() * 1.5);

        for (int i = 0; i < curentNode.childs.size(); i++) {
            pos = i * step;
            int y = (int) (r * Math.sin(Math.toRadians(pos)));
            int x = (int) (r * Math.cos(Math.toRadians(pos)));
            curentNode.childs.get(i).setX(x + curentNode.getX());
            curentNode.childs.get(i).setY(y + curentNode.getY());
        }
    }

    private void updateCallbacks() {
        if (curentNode.parent == null) {
            curentNode.action = new Runnable() {
                @Override
                public void run() {
                    setVisible(false);
                }
            };
        } else {
            curentNode.action = new Runnable() {
                @Override
                public void run() {
                    setCurentNode(curentNode.parent);
                }
            };
        }
        for (final MenuNode child : curentNode.childs) {
            if (child.childs.size() > 0) {
                child.action = new Runnable() {
                    @Override
                    public void run() {
                        setCurentNode(child);
                    }
                };
            }
        }
    }

    private List<MenuNode> createChilds(final MenuNode parent, final List<CreatorNode> creatorNodes) {
        List<MenuNode> menuNodes = new ArrayList<>();
        for (CreatorNode node : creatorNodes) {
            final MenuNode menuNode = new MenuNode(new Image(node.texture));
            menuNode.action = node.action;
            menuNode.parent = parent;
            if (node.action != null) {
                menuNode.action = node.action;
            } else {
                menuNode.setChilds(createChilds(menuNode, node.childNodes));
            }
            menuNodes.add(menuNode);
            //parent.addActor(menuNode);
        }
        return menuNodes;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (!isVisible()) return null;
        return curentNode.hit(x, y, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        curentNode.draw(batch, parentAlpha);
    }


    public class MenuNode extends Group {
        public MenuNode parent = null;
        private List<MenuNode> childs = new ArrayList<>();
        public Runnable action = null;
        private boolean drawChilds = false;
        private Image image;

        public MenuNode(Image image) {
//            addActor(image);
            this.image = image;
            setHeight(image.getHeight());
            setWidth(image.getWidth());
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            image.setX(x);
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            image.setY(y);
        }

        public void setChilds(List<MenuNode> childs) {
            this.childs = childs;
            for (MenuNode child : childs) {
                addActor(child);
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            image.draw(batch, parentAlpha);
            if (drawChilds) {
                for (MenuNode child : childs) {
                    child.draw(batch, parentAlpha);
                }
            }
        }

        @Override
        public Actor hit(float x, float y, boolean touchable) {
            Actor a = image.hit(x - image.getX(), y - image.getY(), touchable);
            if (a != null) a = this;
            if (a == null && drawChilds) {
                for (MenuNode child : childs) {
                    a = child.hit(x , y , touchable);
                    if (a != null) {
                        a = child;
                        break;
                    }
                }
            }
            return a;
        }
    }

    public static class CreatorNode {
        public Texture texture;
        public List<CreatorNode> childNodes;
        public Runnable action;
    }
}
