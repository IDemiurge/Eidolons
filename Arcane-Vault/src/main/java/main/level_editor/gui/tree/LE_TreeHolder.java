package main.level_editor.gui.tree;

import eidolons.libgdx.gui.NinePatchFactory;
import main.level_editor.gui.panels.ClosablePanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_TreeHolder extends ClosablePanel {

    LE_TreeView treeView;

    public LE_TreeHolder() {
//        super(500, 900);
        setSize(500, 900);
        //size?
        add(treeView = new LE_TreeView()).width(500).height(900).center();

        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

        GuiEventManager.bind(GuiEventType.LE_TREE_RESET , p->{
            setUserObject(p.get());
            treeView.setUserObject(p.get());
        } );
    }
    // draggable? via ninepatch? closable?



    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }
}
