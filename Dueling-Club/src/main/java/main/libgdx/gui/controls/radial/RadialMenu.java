package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;

import java.util.ArrayList;
import java.util.List;

public class RadialMenu extends Group {
    private Texture closeTex;
    private MenuNode currentNode;
    private Image closeImage;

    private ActionValueContainer closeButton;

    public RadialMenu() {
        final Texture t = new Texture(RadialMenu.class.getResource("/data/marble_green.png").getPath());
        closeButton = new ActionValueContainer(new TextureRegion(t), () -> RadialMenu.this.setVisible(false));
    }

    public void init(List<MenuNodeDataSource> nodes) {

        currentNode = new MenuNode(closeButton);
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setBounds(
                v2.x - currentNode.getWidth() / 2,
                v2.y - currentNode.getHeight() / 2,
                currentNode.getWidth(),
                currentNode.getHeight()
        );

        addActor(currentNode);

        currentNode.childNodes = createChildren(currentNode, nodes);

        updateCallbacks();
        //updatePosition();
        currentNode.setChildVisible(true);
        setVisible(true);
    }

    private void setCurrentNode(MenuNode node) {
        removeActor(currentNode);
        currentNode.setChildVisible(false);
        currentNode = node;
//        updatePosition();
        updateCallbacks();
        currentNode.setChildVisible(true);
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
        if (currentNode.parent == null) {
            currentNode.button.bindAction(() -> RadialMenu.this.setVisible(false));
        } else {
            currentNode.button.bindAction(() -> setCurrentNode(currentNode.parent));
        }
        for (final MenuNode child : currentNode.childNodes) {
            if (child.childNodes.size() > 0) {
                child.action = () -> setCurrentNode(child);
            }
        }
    }

    private List<MenuNode> createChildren(final MenuNode parent, final List<MenuNodeDataSource> creatorNodes) {
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
            addActor(button);
        }

        public void setChildren(List<MenuNode> childs) {
            this.childNodes = childs;
            for (MenuNode child : childs) {
                addActor(child);
            }

            positionChanged();
        }

        @Override
        protected void positionChanged() {
            super.positionChanged();

            button.setPosition(button.getWidth() / 2, button.getHeight() / 2);

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
            childNodes.forEach(el -> el.setVisible(visible));
            if (visible) {
                positionChanged();
            }
        }
    }
}
