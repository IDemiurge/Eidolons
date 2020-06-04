package main.level_editor.gui.panels.palette.tree;

import main.level_editor.gui.components.TreeX;
import main.level_editor.gui.dialog.BlockTemplateChooser;

import java.util.Set;

public class BlockTemplateTree extends TreeX<PaletteNode> {
    BlockTemplateChooser table;
    public static String room_type;
    public static String room_group;

    public BlockTemplateTree(BlockTemplateChooser table) {
        this.table = table;
        setUserObject(new BlockTreeBuilder().build());
    }

    @Override
    protected boolean isShowRoot() {
        return true;
    }

    @Override
    protected void rightClick(PaletteNode node) {

    }

    @Override
    protected void doubleClick(PaletteNode node) {

    }

    public void reselect() {
        for (Node node : nodes) {
            try {
                if (node.toString().equalsIgnoreCase(room_type)) {
                    if (node.getParent().toString().equalsIgnoreCase(room_group)) {
                        if (node.getParent().toString().equalsIgnoreCase("Custom")) {
                            click(1, null, node, (PaletteNode) node.getObject());
                            node.expandTo();
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        deselect();
    }

    @Override
    public void deselect() {
        super.deselect();
        room_type = null;
        room_group = null;
    }

    @Override
    protected void selected(PaletteNode node, Node n) {
        if (node.getData() instanceof Set) {
            table.fadeIn();
            table.setUserObject(node.getData());
        }
        if (n.getParent() != null)
            if (n.getParent().getObject() instanceof PaletteNode) {
                String s = n.getParent().getObject().toString();
                String s1 = node.toString();

                room_group = s;
                room_type = s1;

            }
    }
}
