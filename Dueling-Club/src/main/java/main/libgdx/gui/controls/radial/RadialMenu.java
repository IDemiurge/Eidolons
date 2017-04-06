package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.List;

public class RadialMenu extends Group {
    private RadialValueContainer currentNode;

    private RadialValueContainer closeButton;

    public RadialMenu() {
        final Texture t = new Texture(RadialMenu.class.getResource("/data/marble_green.png").getPath());
        closeButton = new RadialValueContainer(new TextureRegion(t), () -> RadialMenu.this.setVisible(false));
        setSize(64, 64);
    }

    public void init(List<RadialValueContainer> nodes) {

        currentNode = closeButton;
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x, v2.y);

        closeButton.setChilds(nodes);
        setCurrentNode(closeButton);
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
        int r = (int) (getWidth() * 1.5);

        for (int i = 0; i < currentNode.getChilds().size(); i++) {
            pos = i * step;
            int y = (int) (r * Math.sin(Math.toRadians(pos + 90)));
            int x = (int) (r * Math.cos(Math.toRadians(pos + 90)));
            currentNode.getChilds().get(i).setPosition(x /*+ getX()*/, y /*+ getY()*/);
        }
    }

/*    private void updatePosition() {
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
    }*/

    private void updateCallbacks() {
        if (currentNode.getParent() != null) {
            //currentNode.button.bindAction(() -> RadialMenu.this.setVisible(false));
            currentNode.bindAction(() -> setCurrentNode(currentNode.getParent()));
        }
        for (final RadialValueContainer child : currentNode.getChilds()) {
            if (child.getChilds().size() > 0) {
                child.bindAction(() -> setCurrentNode(child));
            }
        }
    }

/*    private List<MenuNode> createChildren(final MenuNode parent, final List<MenuNodeDataSource> creatorNodes) {
        List<MenuNode> menuNodes = new ArrayList<>();
        for (final MenuNodeDataSource node : creatorNodes) {
            final MenuNode menuNode = new MenuNode(node.getCurrent());
            menuNode.parent = parent;
            if (node.getChilds().size() == 0) {
                final Runnable r = node.getCurrent().getClickAction();
                final RadialMenu menu = this;
                menuNode.button.bindAction(() -> {
                    r.run();
                    menu.setVisible(false);
                });
            } else {
                menuNode.setChildren(createChildren(menuNode, node.getChilds()));
            }
            menuNodes.add(menuNode);
        }
        return menuNodes;
    }

    public class MenuNode extends Group {
        public MenuNode parent = null;
        public Runnable action = null;
        private List<MenuNode> childNodes = new ArrayList<>();
        private ActionValueContainer button;

        public MenuNode(ActionValueContainer button) {
            this.button = button;
            button.setPosition(-(button.getPrefWidth() / 2), -(button.getPrefHeight() / 2));
            addActor(button);
        }

        public void setChildren(List<MenuNode> childs) {
            this.childNodes = childs;
            for (MenuNode child : childs) {
                addActor(child);
            }

            updatePosition();
            setChildVisible(true);
        }

        protected void updatePosition() {
            int step = 360 / childNodes.size();
            int pos;
            int r = (int) (getWidth() * 1.5);

            for (int i = 0; i < childNodes.size(); i++) {
                pos = i * step;
                int y = (int) (r * Math.sin(Math.toRadians(pos + 90)));
                int x = (int) (r * Math.cos(Math.toRadians(pos + 90)));
                childNodes.get(i).setPosition(x + currentNode.getX(), y + currentNode.getY());
            }
        }

        public void setChildVisible(boolean visible) {
            childNodes.forEach(el -> el.button.setVisible(visible));
*//*            if (visible) {
                updatePosition();
            }*//*
        }
    }*/
}
