package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;
import java.util.List;

public class DC_GDX_RadialMenu extends Group {
    private List<MenuNode> menuNodes;
    private MenuNode curentNode;

    public DC_GDX_RadialMenu(List<CreatorNode> nodes) {
        MenuNode base = new MenuNode();
        menuNodes = createChilds(base, nodes);
        curentNode = base;

        calcPositons(base);

        addActor(base);
    }

    private void calcPositons(MenuNode node) {
        if (node.parent != null) {
            node.parent.setX(node.parent.getWidth() / 2);
            node.parent.setY(node.parent.getHeight() / 2);
        }

        int step = 360 / node.childs.size();
        int pos;
        int r = (int) (node.parent.getWidth() * 3);
        for (int i = 0; i < node.childs.size(); i++) {
            pos = i * step;
            int x = (int) (r * Math.sin(pos));
            int y = (int) (r * Math.cos(pos));
            node.childs.get(i).setX(x);
            node.childs.get(i).setY(y);
        }
    }

    private List<MenuNode> createChilds(MenuNode parent, List<CreatorNode> creatorNodes) {
        List<MenuNode> menuNodes = new ArrayList<>();
        for (CreatorNode node : creatorNodes) {
            MenuNode menuNode = new MenuNode();
            menuNode.image = new Image(node.texture);
            menuNode.action = node.action;
            menuNodes.add(menuNode);
            menuNode.parent = parent;
            if (node.action != null) {
                menuNode.action = node.action;
            } else {
                menuNode.childs = createChilds(menuNode, node.childNodes);
            }
        }

        return menuNodes;
    }

    private class MenuNode extends Group {
        public MenuNode parent = null;
        public List<MenuNode> childs;
        public Image image;
        public Runnable action = null;


    }

    public class CreatorNode {
        public Texture texture;
        public List<CreatorNode> childNodes;
        public Runnable action;
    }
}
