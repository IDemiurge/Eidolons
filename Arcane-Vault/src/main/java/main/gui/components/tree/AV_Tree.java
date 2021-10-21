package main.gui.components.tree;

import eidolons.content.PARAMS;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.gui.tree.AvTreeNode;
import main.v2_0.AV2;
import main.swing.generic.components.G_Panel;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.DefaultComparator;
import main.utilities.workspace.Workspace;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

public class AV_Tree extends G_Panel {
    Workspace workspace;
    private JTree tree;
    private Collection<ObjType> types;
    private OBJ_TYPE TYPE;
    private boolean colorsInverted = true;
    private AV_TreeCellRenderer renderer;

    public AV_Tree(Workspace workspace) {
        this.workspace = workspace;
        //TODO
        // this.tree = buildTree(DataManager.toStringList(workspace.getTypeList()), "");
        addTree();
    }

    public AV_Tree(Collection<ObjType> types, OBJ_TYPE TYPE, String group,  boolean colorsInverted) {
        this.types = types;
        this.TYPE = TYPE;
        this.tree = buildTree(types, group);
        this.colorsInverted = colorsInverted;
        addTree();
    }

    public static void setFullNodeStructureOn(boolean fullNodeStructureOn2) {
        // fullNodeStructureOn = fullNodeStructureOn2; ???
    }

    public static AvTreeNode build(Node e) {
        AvTreeNode result = new AvTreeNode(e.getNodeName());

        // logger.info(e.getNodeName());
        int x = e.getChildNodes().getLength();

        for (int i = 0; i < x; i++) {
            Node child = e.getChildNodes().item(i);
            if (child.getNodeName().contains("#text")) {
                continue;
            }
            result.add(build(child));
        }
        return result;

    }

    private void addTree() {
        add(tree, "pos 0 0");
        tree.setLargeModel(true);
        tree.setRootVisible(false);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        renderer = new AV_TreeCellRenderer();
        tree.setCellRenderer(renderer);
        if (workspace != null) {
            renderer.setWorkspace(workspace);
        } else {
            renderer.setTYPE(TYPE);
        }
        renderer.setColorsInverted(colorsInverted);
        tree.setUI(renderer);
    }

    public void setColorsInverted(boolean colorsInverted) {
        renderer.setColorsInverted(colorsInverted);
    }

    private JTree buildTree(Collection<ObjType> simpleTree, String name) {
        JTree tree = new JTree((build(simpleTree, name)));
        return tree;
    }

    public void reload(){
        TreeModel model= new DefaultTreeModel(build(types, TYPE.getName()) );
        tree.setModel(model);
        tree.revalidate();
        tree.repaint();
    }

    private AvTreeNode build(Collection<ObjType> types, String group) {
        Set<String> subGroups =null ;
        if (!StringMaster.isEmpty(group)) {
            Comparator<Object> groupSorter = null;
                    subGroups = XML_Reader.getTreeSubGroupMap().get(group);
                if (subGroups == null) {
                    subGroups = XML_Reader.getTreeSubGroupMap(!XML_Reader.isMacro()).get(group);
                }
                Class<?> ENUM = EnumMaster.getEnumClass(TYPE.getSubGroupingKey().getName());
                if (ENUM != null) {
                    groupSorter = new EnumMaster<>().getEnumSorter(ENUM);
                }

            return  AV2.getManager().getTreeBuilder().build(types, TYPE, group,
                    (Comparator<ObjType>) getComparator(), groupSorter);

        } else if (workspace != null) {
            // subGroups = new LinkedHashSet<>(workspace.getSubgroups());
            // if (subGroups == null)
            // {
            //     if (workspace.isSearch()) {
            //         subGroups = ListMaster.toStringList(DC_TYPE.values());
            //     } else {
            //         subGroups = ListMaster.toStringList(MetaEnums.WORKSPACE_GROUP.values());
            //         subGroups.add("");
            //     }
            // }
        }

        if (workspace != null){
                //TODO
                // if (workspace.isSearch()) {
                //     list = DataManager.toStringList(new Filter<ObjType>().filter(workspace
                //      .getTypeList(), G_PROPS.TYPE, subGroup));
                //     try {
                //         Collections.sort(list, new EnumMaster<>().getEnumSorter(DC_TYPE.class));
                //     } catch (Exception e) {
                //         try {
                //             Collections.sort(list, new EnumMaster<>().getEnumSorter(
                //              EnumMaster.getEnumClass(TYPE.getGroupingKey().getName())));
                //         } catch (Exception e1) {
                //             e1.printStackTrace();
                //         }
                //         printStackTrace(e);
                //     }
                // } else {
                //
                //     PROPERTY filterValue = G_PROPS.WORKSPACE_GROUP;
                //     if (workspace.getSubgroupingProp() != null) {
                //         filterValue = workspace.getSubgroupingProp();
                //     }
                //     list = DataManager.toStringList(new Filter<ObjType>().filter(workspace
                //      .getTypeList(), filterValue, subGroup));
                //     try {
                //         Collections.sort(list, new EnumMaster<>()
                //          .getEnumSorter(WORKSPACE_GROUP.class));
                //     } catch (Exception e) {
                //         printStackTrace(e);
                //     }
                // }

    }
        return null;
    }

    private Comparator<? super ObjType> getComparator() {
        // if (sortAlphabetically){
        if (TYPE instanceof DC_TYPE) {
            switch (((DC_TYPE) TYPE)) {
                case SPELLS:
                    return SortMaster.getSorter(PARAMS.SPELL_DIFFICULTY, false);
            }
        }
        return new DefaultComparator<>();
    }



    public JTree getTree() {
        return tree;
    }

}
