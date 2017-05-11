package main.game.logic.dungeon.editor.gui;

import main.entity.obj.Obj;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import main.game.battlecraft.logic.dungeon.location.building.LocationBuilder.BLOCK_TYPE;
import main.game.battlecraft.logic.dungeon.location.building.LocationBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.battlecraft.logic.dungeon.location.building.MapZone;
import main.game.battlecraft.logic.dungeon.location.building.LocationBuilder.BLOCK_TYPE;
import main.game.battlecraft.logic.dungeon.location.building.LocationBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import main.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.battlecraft.logic.dungeon.location.building.MapZone;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.TreeMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache.NodeDimensions;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LE_TreePlanPanel extends BasicTreeUI implements TreeCellRenderer,
        TreeSelectionListener {
    public static final int ROW_HEIGHT = 32;
    public static final int MAX_ROWS_DISPLAYED = 20;
    public static final int ROWS_DISPLAYED = 15;
    private JTree tree;
    private DungeonPlan plan;
    private DefaultMutableTreeNode root;
    private LE_PlanPanel planPanel;
    private Map<Object, Component> compCache = new HashMap<>();

    public LE_TreePlanPanel(LE_PlanPanel planPanel, DungeonPlan plan) {
        this.planPanel = planPanel;
        this.plan = plan;
        initTree();

    }

    public void initTree() {
        root = new DefaultMutableTreeNode(plan.getDungeon().getName() + " plan");
        for (MapZone z : plan.getZones()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(z);
            root.add(node);
            for (MapBlock b : plan.getBlocks()) {
                if (b.getType() == BLOCK_TYPE.CORRIDOR) {
                    continue;// TODO
                }
                DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode(b);
                node.add(blockNode);
                if (b.getObjects() != null) {
                    for (Obj o : b.getObjects()) {
                        blockNode.add(new DefaultMutableTreeNode(o));
                        b.getConnectedBlocks();
                    }
                }
            }
            root.add(node);
        }
        setTree(new JTree(root));
        tree.setOpaque(false);
        tree.setSize(new Dimension(VISUALS.PLAN_PANEL_FRAME.getWidth() - 50, 20 * 32));
        getTree().setLargeModel(true);
        // getTree().setRootVisible(false);

        try {
            getTree().setUI(this);
        } catch (Exception e) {
            // new Thread (
            // getTree().setUI(this); ).start()
            e.printStackTrace();
        }
        getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        getTree().setCellRenderer(this);
        getTree().addTreeSelectionListener(this);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value == null) {
            return null;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        value = node.getUserObject();
        if (value instanceof MapZone) {
            return getZoneComponent((MapZone) value, selected);
        }

        if (value instanceof MapBlock) {
            return getBlockComponent((MapBlock) value, selected);
        }

        if (value instanceof Obj) {
            return getObjComponent((Obj) value, selected);
        }
        // TODO add dungeon icon
        JLabel label = new JLabel(value.toString());
        label.setForeground(ColorManager.GOLDEN_WHITE);
        label.setFont(FontMaster.getFont(FONT.AVQ, 19, Font.PLAIN));
        return label;
        // new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree,
        // value, selected, expanded, leaf, row, hasFocus)
    }

    public void refresh() {

    }

    public void blockAdded(MapBlock b) {
        DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode(b);

        for (Obj o : b.getObjects()) {
            blockNode.add(new DefaultMutableTreeNode(o));
        }
        DefaultMutableTreeNode node = getZoneNodeForBlock(b);
        node.insert(blockNode, node.getChildCount() - 1);
        return;
    }

    public void blockRemoved(MapBlock block) {
        DefaultMutableTreeNode node = getZoneNodeForBlock(block);
        for (DefaultMutableTreeNode aChild : TreeMaster.getChildren(node)) {
            if (aChild.getUserObject().equals(block)) {
                // node.remove(aChild);
                aChild.removeFromParent();
                tree.updateUI();
                tree.revalidate();
                return;
            }
        }
    }

    private DefaultMutableTreeNode getZoneNodeForBlock(MapBlock b) {
        for (DefaultMutableTreeNode node : TreeMaster.getChildren(root)) {
            if (node.getUserObject().equals(b.getZone())) {
                return node;

            }
        }
        return root;
    }

    private Component getObjComponent(Obj value, boolean selected) {
        // size?
        return null;
    }

    private Component getZoneComponent(MapZone value, boolean selected) {
        String labelText = value.getName()
                + ", "
                + CoordinatesMaster.getBoundsString(value.getX1(), value.getX2(), value.getY1(),
                value.getY2());
        Component comp = compCache.get(labelText);
        if (comp != null) {
            return comp;
        }
        comp = getBlockComponent(STD_IMAGES.ZONE_NODE.getPath(), labelText, selected);
        compCache.put(labelText, comp);
        return comp;
    }

    private Component getBlockComponent(MapBlock b, boolean selected) {

        String labelText = b.getShortName()
                // + ", " + CoordinatesMaster
                // .getBoundsFromCoordinates(b.getCoordinates())
                ;
        Component comp = compCache.get(labelText);
        if (comp != null) {
            return comp;
        }
        String iconPath;
        if (b.getRoomType() == null) {
            iconPath = STD_IMAGES.FOOT.getPath();
        } else {
            iconPath = getIconPath(b.getRoomType());
        }
        comp = getBlockComponent(iconPath, labelText, selected);
        compCache.put(b, comp);
        return comp;

    }

    private Component getBlockComponent(String iconPath, String labelText, boolean selected) {
        // cache?
        G_Panel comp = new G_Panel();
        // extract to m
        ImageIcon icon = ImageManager.getIcon(iconPath);
        JLabel comp2 = new JLabel(icon);
        comp.add(comp2, "id lbl");
        JLabel comp3 = new JLabel(labelText);
        comp3.setForeground(ColorManager.GOLDEN_WHITE);
        comp.add(comp3, "x lbl.x2+4");
        if (selected) {
            // comp3.setBackground(ColorManager.GOLDEN_WHITE);
            // comp3.setForeground(ColorManager.OBSIDIAN);
            comp3.setBackground(ColorManager.PURPLE);
            comp3.setForeground(ColorManager.GOLDEN_WHITE);
            comp3.setOpaque(true);
        }
        // text name?
        return comp;
    }

    public void valueChanged(final TreeSelectionEvent e1) {
        if (((JTree) e1.getSource()).getSelectionPath() == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) (((JTree) e1.getSource())
                        .getSelectionPath().getLastPathComponent());
                Object value = node.getUserObject();

                if (value instanceof MapBlock) {
                    MapBlock block = (MapBlock) value;
                    planPanel.setSelectedZone(null);
                    planPanel.setSelectedBlock(block);
                }
                if (value instanceof MapZone) {
                    MapZone mapZone = (MapZone) value;
                    planPanel.setSelectedBlock(null);
                    // planPanel.setSelectedZone(mapZone);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        planPanel.refresh();
                    }
                });
            }
        }).start();

    }

    @Override
    protected NodeDimensions createNodeDimensions() {
        return new NodeDimensionsHandler() {
            @Override
            public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded,
                                               Rectangle rect) {
                if (value == null) {
                    return new Rectangle(VISUALS.PLAN_PANEL_FRAME.getWidth() - 50, ROW_HEIGHT);
                }
                Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, rect);
                return dimensions;
                // return new Rectangle(VISUALS.PLAN_PANEL_FRAME.getWidth() -
                // 50,
                // 32);

            }
        };
    }

    private String getIconPath(ROOM_TYPE roomType) {
        switch (roomType) {
            case COMMON_ROOM:
                return STD_IMAGES.MAP_PLACE.getPath();
            case DEATH_ROOM:
                return STD_IMAGES.DEATH.getPath();
            case ENTRANCE_ROOM:
                return STD_IMAGES.FOOT.getPath();
            case EXIT_ROOM:
                return STD_IMAGES.FLAG.getPath();
            case GUARD_ROOM:
                return STD_IMAGES.GUARD.getPath();
            case SECRET_ROOM:
                return STD_IMAGES.SECRET.getPath();
            case THRONE_ROOM:
                return STD_IMAGES.HAND.getPath();
            case TREASURE_ROOM:
                return STD_IMAGES.COIN.getPath();

        }
        return null;
    }

    public JTree getTree() {
        return tree;
    }

    public void setTree(JTree tree) {
        this.tree = tree;
    }

}
