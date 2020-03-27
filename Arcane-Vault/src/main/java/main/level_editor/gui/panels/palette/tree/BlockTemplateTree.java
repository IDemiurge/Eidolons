package main.level_editor.gui.panels.palette.tree;

import main.level_editor.gui.components.TreeX;
import main.level_editor.gui.dialog.BlockTemplateChooser;

import java.util.Set;

public class BlockTemplateTree extends TreeX<PaletteNode> {
    BlockTemplateChooser table;

    public BlockTemplateTree(BlockTemplateChooser table) {
        this.table = table;
        setUserObject(new BlockTreeBuilder().build());
    }

    @Override
    protected void doubleClick(PaletteNode node) {

    }

    @Override
    protected void selected(PaletteNode node) {
        if (node.getData() instanceof Set) {
            table.fadeIn();
            table.setUserObject(node.getData());
        }
    }

}
