package main.utilities.search;

import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class TypeFinder {

    public static final String TIP = "Enter type search...";
    public static final char HOTKEY_CHAR = 'f';
    private static String lastSearch;

    public static List<ObjType> findTypes(OBJ_TYPE[] TYPES) {
        String typeName = DialogMaster.inputText(TIP, lastSearch);
        if (StringMaster.isEmpty(typeName)) {
            return null;
        }
        // map to chosen type?
        return findTypes(typeName, TYPES);
    }

    public static List<ObjType> findTypes(String typeName, OBJ_TYPE... TYPES) {
        lastSearch = typeName;
        List<ObjType> list = new ArrayList<>();
        for (OBJ_TYPE TYPE : TYPES) {
            List<ObjType> foundTypes = DataManager.findTypes(typeName, false, TYPE);
            list.addAll(foundTypes);
        }

        return list;
    }

    public static ObjType findType(boolean strict) {
        String typeName = DialogMaster.inputText(TIP, lastSearch);
        if (StringMaster.isEmpty(typeName)) {
            return null;
        }
        lastSearch = typeName; // map to chosen type?
        ObjType type = DataManager.getType(typeName);

        List<ObjType> foundTypes = DataManager.findTypes(typeName, strict);
        if (foundTypes.size() == 1) {
            return foundTypes.get(0);
        }
        if (foundTypes.isEmpty()) {
            return null;
        }
        if (foundTypes.size() > 1) {
            type = (ObjType) DialogMaster.entityChoice(foundTypes);
        } else {
            type = foundTypes.get(0);
        }

        return type;
    }

    public static String getLastSearch() {
        return lastSearch;
    }

    public static void setLastSearch(String lastSearch) {
        TypeFinder.lastSearch = lastSearch;
    }

}
