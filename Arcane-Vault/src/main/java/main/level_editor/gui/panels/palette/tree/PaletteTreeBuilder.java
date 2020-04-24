package main.level_editor.gui.panels.palette.tree;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaletteTreeBuilder {

    public static final DC_TYPE[] paletteTypes =
            {
                    DC_TYPE.BF_OBJ,
                    DC_TYPE.UNITS,
                    DC_TYPE.ENCOUNTERS,
            };
    private boolean showLeaf;

    public Set<PaletteNode> getChildrenDefault(DC_TYPE TYPE, List<String> names, boolean leaf) {
        Set<PaletteNode> groupNodes = new LinkedHashSet<>();
        for (String group : names) {
            if (leaf) {
                    groupNodes.add(new PaletteNode(DataManager.getType(group, TYPE)));
                continue;
            }
            PaletteNode top = new PaletteNode(group);
            Set<PaletteNode> subGroupNodes = new LinkedHashSet<>();
            top.setChildrenSet(subGroupNodes);
            groupNodes.add(top);
            Set<String> subGroups = DataManager.getSubGroups(group);
            for (String subGroup : subGroups) {
                PaletteNode node = new PaletteNode(subGroup);
                subGroupNodes.add(node);
                if (isShowLeaf()){
                    List<String> types = DataManager.getTypesSubGroupNames(TYPE, subGroup);
                    node.setChildrenSet(getChildrenDefault(TYPE, types, true));
                }
            }
        }
        return groupNodes;
    }


    public PaletteNode buildPaletteTreeModel(DC_TYPE TYPE) {
        if (TYPE == null) {
            //custom
            PaletteNode root = new PaletteNode("Custom");
            Set<PaletteNode> topSet = new LinkedHashSet<>();
            root.setChildrenSet(topSet);
            Map<String, List<ObjType>> workspaceTypeMap = LevelEditor.getManager().getPaletteHandler().getWorkspaceTypeMap();
            for (String s : workspaceTypeMap.keySet()) {
                PaletteNode custom;
                topSet.add(custom = new PaletteNode(s));
                if (isShowLeaf()) {
                    Set<PaletteNode> set = new LinkedHashSet<>();
                    custom.setChildrenSet(set);
                    for (ObjType type : workspaceTypeMap.get(s)) {
                        set.add(new PaletteNode(type));
                    }
            }
            }
            return root;
        } else {
            PaletteNode root = new PaletteNode(TYPE.getName());
            root.setChildrenSet(getChildrenDefault(TYPE, DataManager.getTabGroups(TYPE), false));

            return root;
        }
    }

    private Set<PaletteNode> getChildTreeStandard() {
        Set<PaletteNode> list = new LinkedHashSet<>();
        for (DC_TYPE TYPE : paletteTypes) {
            list.add(new PaletteNode(getChildrenDefault(TYPE, DataManager.getTabGroups(TYPE), false), TYPE.getName()));
        }
        return list;
    }

    public boolean isShowLeaf() {
        return showLeaf;
    }

    public void setShowLeaf(boolean showLeaf) {
        this.showLeaf = showLeaf;
    }
}
