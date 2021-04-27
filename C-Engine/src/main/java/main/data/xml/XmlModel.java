package main.data.xml;

import main.entity.type.ObjType;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XmlModel {

    private boolean macro;
    protected   final Map<String, Set<String>> tabGroupMap = new HashMap<>();
    protected   final Map<String, Set<String>> treeSubGroupMap = new HashMap<>();
    protected   final Map<String, Map<String, ObjType>> typeMaps = new HashMap<>();
    protected   DequeImpl<XML_File> files = new DequeImpl<>();

    public XmlModel(boolean macro) {
        this.macro = macro;
    }
}
