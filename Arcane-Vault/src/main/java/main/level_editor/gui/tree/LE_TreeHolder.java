package main.level_editor.gui.tree;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import libgdx.StyleHolder;
import libgdx.gui.NinePatchFactory;
import main.level_editor.gui.panels.ClosablePanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_TreeHolder extends ClosablePanel {

    private final ScrollPane scroll;
    LE_TreeView treeView;

    public LE_TreeHolder() {

        setSize(300, 940);
        treeView = new LE_TreeView();
        add(scroll = new ScrollPane(treeView, StyleHolder.getScrollStyle())).width(300).height(900)
                .top().left();
        scroll.setFadeScrollBars(true);
        setBackground(NinePatchFactory.getLightPanelFilled90Drawable());

        GuiEventManager.bind(GuiEventType.LE_TREE_RESET, p -> {
            setUserObject(p.get());
            treeView.setUserObject(p.get());
            treeView.reselect();
        });
        GuiEventManager.bind(GuiEventType.LE_TREE_RESELECT, p -> {
            treeView.reselect();
        });
        GuiEventManager.bind(GuiEventType.LE_TREE_SELECT, p -> {
            treeView.select(p.get());
        });
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }
}
