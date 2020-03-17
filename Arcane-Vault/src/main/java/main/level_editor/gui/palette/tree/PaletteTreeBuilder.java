package main.level_editor.gui.palette.tree;

import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.data.xml.XmlNodeMaster;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PaletteTreeBuilder {

    public static final DC_TYPE[] paletteTypes =
            {
                    DC_TYPE.BF_OBJ,
                    DC_TYPE.UNITS,
            };

    public PaletteNode buildFullPaletteTree() {
        PaletteNode root = new PaletteNode("Palette");
        PaletteNode preset = createForDefaultTypes();
//        PaletteGroupNode custom = createCustomPalettes();
        root.setChildren(preset);
        return root;
    }

    public Set<PaletteNode> getChildrenDefault(DC_TYPE TYPE, List<String> names, boolean leaf) {
        Set<PaletteNode> groupNodes = new LinkedHashSet<>();
        for (String group : names) {
            if (leaf) {
                groupNodes.add(
                        new PaletteNode(DataManager.getType(group, TYPE)));
                continue;
            }

            Set<String> subGroups = DataManager.getSubGroups(group);
            for (String subGroup : subGroups) {
                PaletteNode node = new PaletteNode(subGroup);
                List<String> types = DataManager.getTypesSubGroupNames(TYPE, subGroup);
                node.setChildrenSet(getChildrenDefault(TYPE, types, true));
                groupNodes.add(node);
            }
        }
        return groupNodes;
    }

    private PaletteNode createForDefaultTypes() {
        PaletteNode node = new PaletteNode("Standard");
        Set<PaletteNode> groupNodes = getChildTreeStandard();
        node.setChildrenSet(groupNodes);
        return node;
    }

    public PaletteNode buildPaletteTreeModel(DC_TYPE TYPE) {
        if (TYPE == null) {
            //custom
            PaletteNode root = new PaletteNode("Custom");
            Set<PaletteNode> topSet= new LinkedHashSet<>();
            root.setChildrenSet(topSet);
            List<File> files = FileManager.getFilesFromDirectory(PathFinder.getEditorWorkspacePath(),
                    false);
            for (File file : files) {
                String data = FileManager.readFile(file);
                List<ObjType> types = new ArrayList<>();
                if (data.contains("METADATA:")) {
                    data = data.split("METADATA:")[0];
                    Document doc = XML_Converter.getDoc(data);
                    for (Node n : XmlNodeMaster.getNodeListFromFirstChild(doc, true)) {
                        String s = XmlNodeMaster.getNodeList(n, true).stream().map(
                                node -> XML_Formatter.restoreXmlNodeName(node.getNodeName()
                                )).collect(Collectors.joining(";"));
                        types.addAll(DataManager.toTypeList(s, C_OBJ_TYPE.BF_OBJ));
                    }
                } else {
                    types.addAll(DataManager.toTypeList(data, C_OBJ_TYPE.BF_OBJ));
                }
                PaletteNode custom;
                topSet.add(custom = new PaletteNode(StringMaster.cropFormat(file.getName())));

                Set<PaletteNode> set = new LinkedHashSet<>();
                custom.setChildrenSet(set);
                for (ObjType type : types) {
                    set.add(new PaletteNode(type));
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
}
