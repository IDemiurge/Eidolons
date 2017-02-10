package main.gui.components.tree;

import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.list.ListItem;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.LogMaster;
import main.system.images.ImageManager;
import main.utilities.workspace.Workspace;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache.NodeDimensions;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class AV_TreeCellRenderer extends BasicTreeUI implements TreeCellRenderer {

    private DefaultTreeCellRenderer defRenderer;
    private int size = 64;
    private OBJ_TYPE TYPE = null;
    private boolean colorsInverted;
    private Workspace workspace;

    public AV_TreeCellRenderer() {
        defRenderer = getDefaultRenderer();
    }

    @Override
    protected NodeDimensions createNodeDimensions() {
        return new NodeDimensionsHandler() {
            @Override
            public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded,
                                               Rectangle rect) {
                Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, rect);
                dimensions.setSize((int) ArcaneVault.TREE_WIDTH, size);

                return dimensions;
            }
        };
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {

        if (tree == null) {
            main.system.auxiliary.LogMaster.log(1, "NULL TREE!");
            return null;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        String typeName = (String) node.toString();
        ObjType type;
        String parent = null;
        try {
            parent = ((DefaultMutableTreeNode) node.getParent()).getUserObject().toString();
        } catch (Exception e) {

        }
        try {
            if (workspace != null) {
                type = DataManager.getType(typeName, workspace.getOBJ_TYPE(typeName, parent));
            } else {
                type = DataManager.getType(typeName, TYPE);
            }
            if (type == null) {
                if (node.isLeaf()) {
                    LogMaster.log(1, "NULL typeName!" + " " + typeName);
                }
                return getDefaultComp(tree, value, selected, expanded, leaf, row, hasFocus);
            }
            Image img = ImageManager.getImage(type.getImagePath());
            if (img == null) {
                // main.system.auxiliary.LogMaster.log(1, "NULL img!" + " " +
                // typeName);
                return getDefaultComp(tree, value, selected, expanded, leaf, row, hasFocus);
            }
            size = Math.min(getMaxTreeIconSize(), img.getWidth(null));
            G_Panel comp = new G_Panel();
            ListItem<ObjType> item = new ListItem<>(type, selected, hasFocus, size);

            comp.add(item, "id item, pos 0 0");

            JLabel lbl = new JLabel(typeName);
            if (selected || hasFocus) {
                Color aspectColor = ColorManager.getAspectColor(type);
                Color bgColor = ColorManager.OBSIDIAN;
                // if (colorsInverted) {
                // bgColor = ColorManager.GOLDEN_WHITE;
                // aspectColor = ColorManager.getDarkerColor(aspectColor, 50);
                // }
                lbl.setForeground(aspectColor);
                lbl.setBackground(bgColor);
            } else if (colorsInverted) {
                lbl.setForeground(ColorManager.GOLDEN_WHITE);
            }

            comp.add(lbl, "pos item.x2+" + FontMaster.SIZE / 4 + " item.y2/2-" + FontMaster.SIZE
                    / 2 + "");

            return comp;

        } catch (Exception e) {
            e.printStackTrace();
            return getDefaultComp(tree, value, selected, expanded, leaf, row, hasFocus);

        }

    }

    private int getMaxTreeIconSize() {
        return GuiManager.getFullObjSize();
    }

    private Component getDefaultComp(JTree tree, Object value, boolean selected, boolean expanded,
                                     boolean leaf, int row, boolean hasFocus) {
        main.system.auxiliary.LogMaster.log(LogMaster.GUI_DEBUG, "drawing default lbl: " + value);
        Component treeCellRendererComponent = defRenderer.getTreeCellRendererComponent(tree, value,
                selected, expanded, leaf, row, hasFocus);
        ;
        if (colorsInverted) {
            Color c = ColorManager.ALLY_COLOR;
            Color c2 = ColorManager.GOLDEN_WHITE;
            treeCellRendererComponent.setBackground(c);
            treeCellRendererComponent.setForeground(c2);
        }
        return treeCellRendererComponent;

    }

    public OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public void setTYPE(OBJ_TYPE tYPE) {
        TYPE = tYPE;
    }

    public void setColorsInverted(boolean colorsInverted) {
        this.colorsInverted = colorsInverted;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;

    }

    private DefaultTreeCellRenderer getDefaultRenderer() {
        return new DefaultTreeCellRenderer() {
            public Color getBackgroundNonSelectionColor() {
                return (null);
            }

            public Color getBackgroundSelectionColor() {
                return ColorManager.LILAC;
            }

            public Color getBackground() {
                return (null);
            }
        };
    }
}
