package main.level_editor.gui.panels.palette.tree;

import main.data.tree.DataNode;
import main.level_editor.backend.metadata.decor.LE_DecorHandler;
import main.level_editor.gui.components.TreeX;
import main.level_editor.gui.panels.palette.table.DecorChooser;

public class DecorTree extends TreeX {
    private final DecorChooser table;

    public DecorTree(DecorChooser table) {
        this.table = table;
        setUserObject(new DecorTreeBuilder().build());
    }

    @Override
    protected void rightClick(DataNode node) {

    }

    @Override
    protected void doubleClick(DataNode node) {

    }

    @Override
    protected void selected(DataNode node, Node n) {
        LE_DecorHandler.DECOR decor= (LE_DecorHandler.DECOR) node.getData();
        table.setUserObject(LE_DecorHandler.decorPalette.get(decor));
    }
}
