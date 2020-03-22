package main.level_editor.backend.functions.palette;

import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.data.xml.XmlNodeMaster;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaletteHandlerImpl extends LE_Handler implements IPaletteHandler {

    private   Map<String, List<ObjType>> workspaceTypeMap;

    public PaletteHandlerImpl(LE_Manager manager) {
        super(manager);
        initWorkspaceTypeMap();
    }
    private void initWorkspaceTypeMap() {
        workspaceTypeMap = new HashMap<>();
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
                    types.addAll(DataManager.toTypeList(s, C_OBJ_TYPE.BF_OBJ_LE));
                }
            } else {
                types.addAll(DataManager.toTypeList(data, C_OBJ_TYPE.BF_OBJ_LE));
            }
            String fileName = StringMaster.cropFormat(file.getName());
            workspaceTypeMap.put(fileName, types);
    }
    }
    @Override
    public void createPalette() {
        //displayed as tree?
        //what is the data, txt files? yeah, but maybe with folder structure!

        //path via layered grouping...

//        selectedPalettePath =  getModel().getPaletteSelection();
//        if ( == null ){
//            path = getDefaultPath() + name;
//        }
//        //paletteCreationDialog
//
//        FileManager.write(types, path);
    }

    @Override
    public void removePalette() {

    }

    @Override
    public void mergePalettes() {

    }

    @Override
    public void clonePalette() {

    }

    @Override
    public void addToPalette() {

    }

    @Override
    public void removeFromPalette() {

    }

    public List<ObjType> getTypesForTreeNode(DC_TYPE TYPE, Object object) {
        List<ObjType> list = new ArrayList<>();
        if (TYPE == null) {
            return getWorkspaceTypeMap().get(object.toString());
        } else
        if (object instanceof DC_TYPE) {
            list = DataManager.getTypes(((DC_TYPE) object));
        } else {
            if (object instanceof String) {
                list =   DataManager.getTypesSubGroup(TYPE, object.toString());
                if (list.isEmpty()) {
                    list = DataManager.getTypesGroup(TYPE, object.toString());
                }
            } else {

            }
        }
        return list;
    }

    public Map<String, List<ObjType>> getWorkspaceTypeMap() {
        return workspaceTypeMap;
    }
}


