package main.data;

import main.content.*;
import main.content.CONTENT_CONSTS.MATERIAL;
import main.content.CONTENT_CONSTS.QUALITY_LEVEL;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.ability.construct.VariableManager;
import main.data.xml.XML_Reader;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.Game;
import main.system.auxiliary.*;
import main.system.launch.CoreEngine;

import java.util.*;
import java.util.Map.Entry;

/**
 * Contains utility methods for finding and managing ObjType's
 */

public class DataManager {

    public static final String BF_TYPES = "CHARS;UNITS;BF OBJ;TERRAIN";
    public static final String MISC = "Misc";
    public static final String ATTR = "Attribute";
    public static final String MSTR = "Mastery";
    public static final String ENCH = "Passive";
    public static final String EMPTY = "Empty";
    public static final String[] CUSTOM_JEWELRY_GROUPS = {MISC, MISC, MSTR, ENCH, ATTR, EMPTY,};
    private static final String RES_LEVEL_PROP = G_PROPS.RANK.name();
    private static Map<OBJ_TYPE, Map<String, List<String>>> typesSubGroups;
    private static List<ObjType> overwrittenTypes;
    private static Map<OBJ_TYPE, Map<String, List<String>>> subGroupsMaps;
    private static Map<C_OBJ_TYPE, List<ObjType>> customObjTypeCache;
    private static Collection<ObjType> baseWeaponTypes = new ArrayList<>();
    private static Collection<ObjType> baseJewelryTypes = new ArrayList<>();
    private static Collection<ObjType> baseItemTypes = new ArrayList<>();
    private static Collection<ObjType> baseArmorTypes = new ArrayList<>();
    private static Collection<ObjType> baseGarmentTypes = new ArrayList<>();
    private static Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> itemMaps = new ConcurrentMap();

    public static void init() {
        subGroupsMaps = new HashMap<>();
        for (OBJ_TYPES T : OBJ_TYPES.values()) {
            subGroupsMaps.put(T, new HashMap<String, List<String>>());
        }

    }

    public static List<List<String>> getTabTreeData(PROPERTY filterValue, String type) {
        List<List<String>> lists = new LinkedList<List<String>>();

        for (String criterion : XML_Reader.getSubGroups(type)) {
            if (criterion == null) {
                continue;
            }
            lists.add(getFilteredTypeNameList(criterion, ContentManager.getOBJ_TYPE(type),
                    filterValue));
        }

        return lists;
    }

    public static ObjType getType(String typeName) {
        return getType(typeName, false);
    }

    public static ObjType getType(String typeName, boolean recursion) {
        for (String group : XML_Reader.getTypeMaps().keySet()) {
            ObjType type;
            OBJ_TYPE TYPE = ContentManager.getOBJ_TYPE(group);
            try {
                type = getType(typeName, TYPE, recursion);
            } catch (Exception e) {
                continue;
            }
            if (type != null) {
                return type;
            } else {
                continue;
            }
        }
        return null;
    }

    public static ObjType getType(String typeName, OBJ_TYPE obj_type) {
        return getType(typeName, obj_type, true);
    }

    public static List<ObjType> findTypes(String typeName, boolean strict) {
        List<ObjType> list = new LinkedList<>();
        for (OBJ_TYPES TYPE : OBJ_TYPES.values()) {
            list.addAll(findTypes(typeName, strict, TYPE));
        }
        return list;
    }

    public static List<ObjType> findTypes(String typeName, boolean strict, OBJ_TYPE... TYPES) {

        List<ObjType> list = new LinkedList<>();

        for (OBJ_TYPE TYPE : TYPES) {
            if (TYPE instanceof C_OBJ_TYPE) {
                C_OBJ_TYPE C_TYPE = (C_OBJ_TYPE) TYPE;
                list.addAll(findTypes(typeName, strict, C_TYPE.getTypes()));
                continue;
            }
            Map<String, ObjType> map = getTypeMap(TYPE); // C_TYPES
            if (map != null) {
                for (String name : map.keySet()) {
                    if (StringMaster.compareByChar(name, typeName, false)) {
                        list.add(map.get(name));
                        continue;
                    }
                    if (!strict) {
                        if (StringMaster.compare(name, typeName, false)) {
                            list.add(map.get(name));
                        }
                    }
                }
            }
        }
        return list;
    }

    public static ObjType findType(String typeName, OBJ_TYPE TYPE) {
        List<ObjType> list = (findTypes(typeName, false, TYPE));
        return new SearchMaster<ObjType>().findClosest(typeName, list);
    }

    public static ObjType getType(String typeName, OBJ_TYPE obj_type, boolean recursion) {
        if (C_OBJ_TYPE.ITEMS.equals(obj_type)) {
            // 1) get quality 2) find base type name 3) crop and use
            ObjType type = null;
            try {
                type = getItemType(typeName, obj_type);
            } catch (Exception e) {

            }
            if (type != null) {
                return type;
            }
        }
        if (StringMaster.isEmpty(typeName)) {
            return null;
        }
        typeName = typeName.trim();
        if (obj_type == null) {
            if (recursion) {
                return getType(typeName, false);
            }
        }

        if (obj_type instanceof C_OBJ_TYPE) {
            return getConstructedType(typeName, (C_OBJ_TYPE) obj_type);
        }
        if (obj_type instanceof C_MACRO_OBJ_TYPE) {
            return getConstructedMacroType(typeName, (C_MACRO_OBJ_TYPE) obj_type);
        }

        Map<String, ObjType> map = getTypeMap(obj_type);
        if (map == null) {
            return null;
        }
        ObjType type = map.get(typeName);
        if (type == null) {
            // typeName = typeName.replace(";", " "); never needed???
            type = map.get(typeName);
        }
        if (type == null) {
            // typeName = typeName.replace(" ", "_");
            type = map.get(typeName);
        }

        if (type != null) {
            return type;
        }

        if (!recursion) {
            for (String s : map.keySet()) {
                if (StringMaster
                        .compareByChar(s.replace(" ", ""), typeName.replace(" ", ""), false)) {
                    return map.get(s);
                }
            }
            main.system.auxiliary.LogMaster.log(LogMaster.DATA_DEBUG, "Type not found: " + obj_type
                    + ":" + typeName);
            return null;
        }
        if (typeName.endsWith(";")) {
            typeName = StringMaster.getFormattedTypeName(typeName);
        }
        // String formattedTypeName =
        // StringMaster.getFormattedTypeName(typeName);
        type = getType(typeName, obj_type, false);
        if (type != null) {
            return type;
        }
        return getType(StringMaster.getWellFormattedString(typeName), obj_type, false);
    }

    private static ObjType getConstructedMacroType(String typeName, C_MACRO_OBJ_TYPE obj_type) {
        ObjType type = null;
        for (MACRO_OBJ_TYPES TYPE : (obj_type).getTypes()) {
            type = getType(typeName, TYPE, false);
            if (type != null) {
                return type;
            }
        }
        main.system.auxiliary.LogMaster.log(LogMaster.DATA_DEBUG, "Type not found: " + obj_type
                + ":" + typeName);
        return type;
    }

    // refactor
    private static ObjType getConstructedType(String typeName, C_OBJ_TYPE obj_type) {
        ObjType type = null;
        for (OBJ_TYPES TYPE : obj_type.getTypes()) {
            type = getType(typeName, TYPE, false);
            if (type != null) {
                return type;
            }
        }
        main.system.auxiliary.LogMaster.log(LogMaster.DATA_DEBUG, "Type not found: " + obj_type
                + ":" + typeName);
        return type;
    }

    private static ObjType getItemType(String typeName, OBJ_TYPE obj_type) {
        ObjType type = getBaseItemType(typeName, obj_type);
        if (type != null) {
            return type;
        }

        if (obj_type.equals(OBJ_TYPES.JEWELRY)) {
            return null;
        }
        int i = 0;
        List<String> parts = StringMaster.openContainer(typeName, " ");
        String qualityName = parts.get(0);
        QUALITY_LEVEL q = new EnumMaster<QUALITY_LEVEL>().retrieveEnumConst(QUALITY_LEVEL.class,
                qualityName);
        if (q == null) {
            q = QUALITY_LEVEL.NORMAL;
        } else {
            i++;
        }

        String materialName = parts.get(i);
        MATERIAL m = new EnumMaster<MATERIAL>().retrieveEnumConst(MATERIAL.class, materialName);
        while (m == null) {
            i++;
            if (i > parts.size() - 1) {
                return null;
            }
            materialName += " " + parts.get(i);
            m = new EnumMaster<MATERIAL>().retrieveEnumConst(MATERIAL.class, materialName);
        }

        String baseTypeName = "";
        for (int a = i; a <= parts.size() - 1; a++) {
            baseTypeName += parts.get(a) + " ";
        }

        ObjType baseType = getBaseItemType(baseTypeName, obj_type);

        // int j = 0;
        // if (baseType == null) {
        // baseType = getBaseItemType(parts.get(parts.size() - 2) + " "
        // + baseTypeName, obj_type);
        // j++;
        // }
        // typeName.replace(baseTypeName, "");
        // materialName = typeName.replace(qualityName, "");
        // while (parts.size() - i > j) {
        // materialName += parts.get(i) + " ";
        // i++;
        // }
        Map<MATERIAL, Map<ObjType, ObjType>> map = itemMaps.get(q);
        Map<ObjType, ObjType> map2 = map.get(m);
        return map2.get(baseType);
    }

    private static ObjType getBaseItemType(String string, OBJ_TYPE obj_type) {
        Collection<ObjType> list = null;
        if (obj_type.equals(OBJ_TYPES.ARMOR)) {
            list = (baseArmorTypes);
        }
        if (obj_type.equals(OBJ_TYPES.WEAPONS)) {
            list = (baseWeaponTypes);
        }
        if (obj_type.equals(OBJ_TYPES.ITEMS)) {
            list = (baseItemTypes);
        }
        if (obj_type.equals(OBJ_TYPES.JEWELRY)) {
            list = (baseJewelryTypes);
        }

        for (ObjType t : list) {
            if (StringMaster.compareByChar(string, t.getName(), true)) {
                return t;
            }
        }
        return null;
    }

    public static Map<String, ObjType> getTypeMap(String type) {
        Map<String, ObjType> map = XML_Reader.getTypeMaps().get(type);
        if (map == null) {
            for (String sub : XML_Reader.getTypeMaps().keySet()) {
                if (StringMaster.compare(sub, type)) {
                    map = XML_Reader.getTypeMaps().get(sub);
                    break;
                }
            }
        }
        if (map == null) {
            if (ContentManager.getOBJ_TYPE(type) == null) {
                return null;
            }
            if (!CoreEngine.checkReadNecessary(type)) {
                return null;
            }
            map = new HashMap<>();
            XML_Reader.getTypeMaps().put(type, map);
        }
        return map;
    }

    public static Map<String, ObjType> getTypeMap(OBJ_TYPE type) {
        return getTypeMap(type.toString());
    }

    public static void displayData() {
        for (Map<String, ObjType> m : XML_Reader.getTypeMaps().values()) {
            for (Entry<String, ObjType> t : m.entrySet()) {
                LogMaster.log(0, t.getKey() + " " + t.getValue().toString());
            }
        }
    }

    public static ObjType getOrAddType(String name, OBJ_TYPE TYPE) {
        ObjType type = getType(name, TYPE);
        if (type == null) {
            type = addType(name, TYPE);
        }
        return type;
    }

    public static void addType(String name, String group, ObjType type) {
        getTypeMap(group).put(name.trim(), type);

    }

    public static void addType(String name, OBJ_TYPE group, ObjType type) {
        addType(name, group.toString(), type);
    }

    public static void renameType(ObjType type, String newName) {
        XML_Reader.getTypeMaps().get(type.getProperty(G_PROPS.TYPE)).remove(type.getName());
        XML_Reader.getTypeMaps().get(type.getProperty(G_PROPS.TYPE)).put(newName, type);

    }

    public static void removeType(ObjType t) {
        ObjType removed = getTypeMap(t.getOBJ_TYPE_ENUM()).remove(t.getName());

    }

    public static void removeType(String name, String group) {
        getTypeMap(group).remove(getType(name, group).getName());

    }

    public static PROPERTY getGroupingKey(String typeName) {
        OBJ_TYPE TYPE = ContentManager.getOBJ_TYPE(typeName);

        if (TYPE == null) {
            return null;
        }
        return TYPE.getGroupingKey();
    }

    public static PROPERTY getSubGroupingKey(String typeName) {
        OBJ_TYPE TYPE = ContentManager.getOBJ_TYPE(typeName);
        if (TYPE == null) {
            return null;
        }
        return TYPE.getSubGroupingKey();
    }

    public static ObjType getType(String key, String group) {
        if (group == null) {
            return getType(key);
        }
        return getType(key, ContentManager.getOBJ_TYPE(group));
    }

    public static PROPERTY getGroupingKey(OBJ_TYPE TYPE) {
        return TYPE.getGroupingKey();
    }

    public static List<ObjType> getTypes() {
        List<ObjType> list = new LinkedList<>();
        for (OBJ_TYPES type : OBJ_TYPES.values()) {
            list.addAll(getTypes(type));
        }
        return list;
    }

    public static List<ObjType> getTypes(OBJ_TYPE key) {
        if (key instanceof C_OBJ_TYPE) {

            List<ObjType> group = getCustomObjTypeCache().get(key);
            if (group == null) {
                group = new LinkedList<>();
            }
            for (OBJ_TYPES type : ((C_OBJ_TYPE) key).getTypes()) {
                group.addAll(getTypes(type));
            }
            getCustomObjTypeCache().put((C_OBJ_TYPE) key, group);
            return group;
        }

        Map<String, ObjType> map = getTypeMap(key);
        if (map == null) {
            return new LinkedList<>();
        }
        return new LinkedList<ObjType>(map.values());
    }

    public static List<String> getTypeNames(OBJ_TYPE TYPE) {
        if (TYPE instanceof C_OBJ_TYPE) {
            List<String> group = new LinkedList<>();
            for (OBJ_TYPES type : ((C_OBJ_TYPE) TYPE).getTypes()) {
                group.addAll(getTypeNames(type));
            }
            return group;
        }
        return new LinkedList<>(getTypeMap(TYPE).keySet());
    }

    public static List<ObjType> getTypesSubGroup(OBJ_TYPE TYPE, String subgroup) {
        return toTypeList(getTypesSubGroupNames(TYPE, subgroup), TYPE);
        // TODO refactor!
    }

    public static List<String> getTypesSubGroupNames(OBJ_TYPE TYPE, String subgroup) {
        if (TYPE instanceof C_OBJ_TYPE) {
            List<String> data = null;
            for (OBJ_TYPES type : ((C_OBJ_TYPE) TYPE).getTypes()) {
                try {
                    data = getTypesSubGroupNames(type, subgroup);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                if (data != null) {
                    if (!data.isEmpty()) {
                        break;
                    }
                }
            }
            return data;
        }

        if (subgroup == null) {
            return getTypeNames(TYPE);
        }

        List<String> list = getTypesSubGroups().get(TYPE).get(subgroup);
        // if (list != null)
        // return list;

        List<String> groupsList = EnumMaster.findEnumConstantNames(TYPE.getSubGroupingKey()
                .toString());
        // TODO check TYPES!
        if (groupsList.isEmpty()) {
            if (OBJ_TYPES.isOBJ_TYPE(subgroup)) {
                groupsList = toStringList(getTypes(OBJ_TYPES.getType(subgroup)));
            } else {
                if (isTypeName(subgroup)) {
                    groupsList = toStringList(getTypes(getType(subgroup).getOBJ_TYPE_ENUM()));
                }
            }
        }
        // if (ListMaster.contains(groupsList, subgroup, true)
        // || isCustomGroup(subgroup)) {
        list = new LinkedList<String>();

        Map<String, Map<String, ObjType>> map = XML_Reader.getTypeMaps();
        Collection<ObjType> set = map.get(TYPE.toString()).values();
        for (ObjType type : set) {
            if (StringMaster.compare(type.getProperty(TYPE.getSubGroupingKey()), subgroup, true)) {
                list.add(type.getName());
            }
        }
        // }

        getTypesSubGroups().get(TYPE).put(subgroup, list);
        return list;
    }

    public static boolean isCustomGroup(String subgroup) {
        return Arrays.asList(CUSTOM_JEWELRY_GROUPS).contains(subgroup);
    }

    // TODO SORT
    public static List<String> getFilteredTypeNameList(String filter, OBJ_TYPE OBJ_TYPE,
                                                       VALUE filterValue) {
        Map<String, ObjType> map = XML_Reader.getTypeMaps().get(OBJ_TYPE.toString());
        List<String> list = new LinkedList<String>();
        for (String objName : map.keySet()) {
            if (StringMaster.compare(map.get(objName).getValue(filterValue), filter)) {
                list.add(objName);
            }
        }
        return list;
    }

    public static List<ObjType> getFilteredTypes(String filter, OBJ_TYPE OBJ_TYPE, VALUE filterValue) {
        Map<String, ObjType> map = XML_Reader.getTypeMaps().get(OBJ_TYPE.toString());

        List<ObjType> list = new LinkedList<ObjType>();
        boolean or = false;
        if (filter.contains(StringMaster.OR)) {
            or = true;
        }
        for (ObjType objName : map.values()) {
            if (or) {
                if (StringMaster.contains(filter, objName.getValue(filterValue))) {
                    list.add(objName);
                }
            } else if (StringMaster.compare(objName.getValue(filterValue),
             filter, true)) {
                list.add(objName);
            }
        }
        if (list.isEmpty()) {
            for (ObjType objName : map.values()) {
                if (StringMaster.compare(objName.getValue(filterValue), filter, false)) {
                    list.add(objName);
                }
            }
        }
        return list;
    }

    public static List<String> getTypesGroupNames(OBJ_TYPE TYPE, String group) {
        return toStringList(getTypesGroup(TYPE, group));
    }

    public static List<ObjType> getTypesGroup(OBJ_TYPE TYPE, String group) {
        if (group == null) {
            return getTypes(TYPE);
        }
        List<ObjType> list = new LinkedList<ObjType>();
        Map<String, Map<String, ObjType>> map = XML_Reader.getTypeMaps();
        Collection<ObjType> set = map.get(TYPE.toString()).values();
        for (ObjType type : set) {
            if (StringMaster.compareByChar(type.getProperty(TYPE.getGroupingKey()), group, true)) {
                list.add(type);
            }
        }
        if (list.isEmpty()) {
            for (ObjType type : set) {
                if (StringMaster.compareByChar(type.getProperty(TYPE.getSubGroupingKey()), group,
                        true)) {
                    list.add(type);
                }
            }
        }
        return list;
    }

    public static List<String> getTypeNamesGroup(OBJ_TYPE TYPE, String group) {
        if (group == null) {
            return getTypeNames(TYPE);
        }
        List<String> list = new LinkedList<String>();
        Map<String, Map<String, ObjType>> map = XML_Reader.getTypeMaps();
        Collection<ObjType> set = new HashSet<>();
        if (TYPE instanceof C_OBJ_TYPE) {
            for (OBJ_TYPES T : ((C_OBJ_TYPE) TYPE).getTypes()) {
                list = getTypeNamesGroup(T, group);
                if (ListMaster.isNotEmpty(list)) {
                    return list;
                }
            }
        } else {
            set = map.get(TYPE.toString()).values();
        }
        for (ObjType type : set) {
            if (type.getProperty(TYPE.getGroupingKey()).equalsIgnoreCase(group)) {
                list.add(type.getName());
            }
        }
        return list;
    }

    public static boolean isTypeName(String item) {
        return isTypeName(item, null);
    }

    public static boolean isTypeName(String item, OBJ_TYPES TYPE) {
        if (item == null) {
            return false;
        }
        item = VariableManager.removeVarPart(item);
        return (getType(item, TYPE) != null);
    }

    public static List<String> getHeroList(String res_level) {
        List<String> list = new LinkedList<String>();

        for (String name : getTypeNames(OBJ_TYPES.CHARS)) {
            ObjType type = getType(name, OBJ_TYPES.CHARS);
            if (res_level == null) {
                list.add(name);
                continue;
            }
            if (StringMaster.compare(type.getProperty(ContentManager.getPROP(RES_LEVEL_PROP)),
                    res_level)) {
                list.add(name);
            }
        }
        return list;
    }

    public static List<ObjType> toTypeList(String string, OBJ_TYPE TYPE) {
        return toTypeList(StringMaster.openContainer(string), TYPE);
    }

    public static List<ObjType> toTypeList(List<String> strings, OBJ_TYPE TYPE) {
        if (strings == null) {
            return null;
        }
        List<ObjType> list = new LinkedList<ObjType>();
        for (String string : strings) {
            ObjType type = getType(string, TYPE);
            if (type == null) {
                if (StringMaster.isInteger(string)) {
                    type = Game.game.getTypeById(StringMaster.getInteger(string));
                    if (type == null) {
                        Obj obj = Game.game.getObjectById(StringMaster.getInteger(string));
                        if (obj != null) {
                            type = obj.getType();
                        }
                    }
                }
            }
            if (type != null) {
                list.add(type);
            }
        }
        return list;
    }

    public static Collection<? extends Obj> toObjList(Collection<Integer> values, Game game) {
        List<Obj> list = new LinkedList<Obj>();
        for (Integer id : values) {
            list.add(game.getObjectById(id));
        }
        return list;
    }

    public static String toString(Collection<? extends Entity> listTypeData) {
        return StringMaster.constructContainer(toStringList(listTypeData));
    }

    public static List<String> toStringList(Collection<? extends Entity> listTypeData) {
        List<String> list = new LinkedList<String>();
        for (Entity type : listTypeData) {
            if (type == null) {
                continue;
            }
            list.add(type.getName());
        }
        return list;
    }

    public static List<String> convertObjToStringList(Collection<? extends Obj> listTypeData) {
        List<String> list = new LinkedList<String>();
        for (Entity type : listTypeData) {
            list.add(type.getName());
        }
        return list;
    }

    public static Map<OBJ_TYPE, Map<String, List<String>>> getTypesSubGroups() {
        if (typesSubGroups == null) {
            typesSubGroups = new HashMap<>();
            for (String sub : XML_Reader.getXmlMap().keySet()) {
                typesSubGroups.put(ContentManager.getOBJ_TYPE(sub),
                        new HashMap<String, List<String>>());
            }
        }
        return typesSubGroups;
    }


    public static Class<?> getGroupingClass(OBJ_TYPE TYPE) {
        return EnumMaster.getEnumClass(TYPE.getGroupingKey().getName(), CONTENT_CONSTS.class);
    }


    public static List<String> getTabsGroup(OBJ_TYPE TYPE) {

        if (TYPE instanceof C_OBJ_TYPE) {
            List<String> group = new LinkedList<>();
            for (OBJ_TYPES type : ((C_OBJ_TYPE) TYPE).getTypes()) {
                group.addAll(XML_Reader.getTabGroupMap().get(type.getName()));
            }
            return group;
        }

        return new LinkedList<>(XML_Reader.getTabGroupMap().get(TYPE.getName()));
    }

    public static void addType(ObjType type) {
        addType(type.getName(), type.getOBJ_TYPE_ENUM(), type);
    }

    public static ObjType addType(String typeName, OBJ_TYPE TYPE) {
        ObjType objType = new ObjType(typeName, TYPE);
        addType(objType);
        return objType;
    }

    public static void overwriteType(ObjType type) {
        ObjType oldType = getType(type.getName(), type.getOBJ_TYPE_ENUM());
        if (oldType != null) {
            if (overwrittenTypes == null) {
                overwrittenTypes = new LinkedList<>();
            }
            overwrittenTypes.add(oldType);
        }
        addType(type);

    }

    public static void reloadOverwrittenTypes() {
        if (overwrittenTypes == null) {
            return;
        }
        for (ObjType type : overwrittenTypes) {
            addType(type);
        }
        overwrittenTypes.clear();
    }

    public static boolean checkTypeName(String newName) {

        return StringMaster.checkSymbolsStandard(newName); // TODO
    }

    public static List<ObjType> toTypeList(Collection<? extends Entity> data) {
        List<ObjType> list = new LinkedList<>();
        for (Entity e : data) {
            list.add(e.getType());
        }
        return list;
    }


    public static List<String> getSubGroupsForTYPE(OBJ_TYPE TYPE, String group) {
        if (TYPE instanceof C_OBJ_TYPE) {
            for (OBJ_TYPES T : ((C_OBJ_TYPE) TYPE).getTypes()) {
                List<String> list = getSubGroupsForTYPE(T, group);
                if (ListMaster.isNotEmpty(list)) {
                    return list;
                }
            }
        }
        // if (subGroupsMaps.get(TYPE).get(group) != null)
        // return (subGroupsMaps.get(TYPE).get(group));

        Set<String> fullList = (XML_Reader.getTreeSubGroupMap().get(group));
        Set<String> allGroupsForType = XML_Reader.getTreeSubGroupMap().get(group); // TODO

        List<String> filteredSubGroups = new LinkedList<>();

        for (String subgroup : fullList) {
            if (allGroupsForType.contains(subgroup)) {
                filteredSubGroups.add(subgroup);
            }
        }
        subGroupsMaps.get(TYPE).put(group, filteredSubGroups);

        return filteredSubGroups;
    }

    public static boolean isIdSorted(OBJ_TYPE TYPE) {
        if (TYPE instanceof OBJ_TYPES) {
            if (TYPE == OBJ_TYPES.DEITIES) {
                return true;
            }
            if (TYPE == OBJ_TYPES.CHARS) {
                return true;
            }
        }

        return false;
    }

    public static Map<C_OBJ_TYPE, List<ObjType>> getCustomObjTypeCache() {
        if (customObjTypeCache == null) {
            customObjTypeCache = new HashMap<>();
        }
        return customObjTypeCache;
    }

    public static Collection<ObjType> getBaseWeaponTypes() {
        return baseWeaponTypes;
    }

    public static void setBaseWeaponTypes(Collection<ObjType> baseWeaponTypes) {
        DataManager.baseWeaponTypes = baseWeaponTypes;
    }

    public static Collection<ObjType> getBaseJewelryTypes() {
        return baseJewelryTypes;
    }

    public static void setBaseJewelryTypes(Collection<ObjType> baseJewelryTypes) {
        DataManager.baseJewelryTypes = baseJewelryTypes;
    }

    public static Collection<ObjType> getBaseItemTypes() {
        return baseItemTypes;
    }

    public static void setBaseItemTypes(Collection<ObjType> baseItemTypes) {
        DataManager.baseItemTypes = baseItemTypes;
    }

    public static Collection<ObjType> getBaseArmorTypes() {
        return baseArmorTypes;
    }

    public static void setBaseArmorTypes(Collection<ObjType> baseArmorTypes) {
        DataManager.baseArmorTypes = baseArmorTypes;
    }

    public static Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> getItemMaps() {
        return itemMaps;
    }

    public static void setItemMaps(Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> itemMaps) {
        DataManager.itemMaps = itemMaps;
    }

    public static Collection<ObjType> getBaseGarmentTypes() {
        return baseGarmentTypes;
    }

    public static void setBaseGarmentTypes(Collection<ObjType> baseGarmentTypes) {
        DataManager.baseGarmentTypes = baseGarmentTypes;
    }

    public static List<ObjType> getRootTypes(List<ObjType> data) {
        List<ObjType> list = new LinkedList<>();
        for (ObjType type : data) {
            if (type.isUpgrade()) {
                continue;
            }
            list.add(type);
        }
        return list;
    }

    public static List<ObjType> toTypeList(List<ObjAtCoordinate> types) {
        List<ObjType> list = new LinkedList<>();
        for (ObjAtCoordinate type : types) {
            list.add(type.getType());
        }
        return list;
    }


    public static List<ObjType> getSublings(ObjType type, List<ObjType> data) {
        if (getParent(type) == null) {
            return getRootTypes(data);
        }
        return getChildren(getParent(type), data);
    }

    public static ObjType getWeaponItem(QUALITY_LEVEL quality, MATERIAL material, ObjType type) {
        return DataManager.getItemMaps().get(quality).get(material).get(type);
    }

    public static ObjType getParent(ObjType type) {
        String property = type.getProperty(G_PROPS.BASE_TYPE);
        if (property.isEmpty()) {
            if (type.getOBJ_TYPE_ENUM() != OBJ_TYPES.CLASSES) {
                return null;
            } else if (type.getProp("Class Group").equals("Multiclass")) {
                property = type.getProperty("base classes one");
            }
        }
        return getType(property, type.getOBJ_TYPE_ENUM());
    }

    public static List<ObjType> getChildren(ObjType type, List<ObjType> data) {
        G_PROPS prop = G_PROPS.BASE_TYPE;
        G_PROPS prop2 = G_PROPS.NAME;
        return getChildren(type, data, prop, prop2);

    }

    public static List<ObjType> getChildren(ObjType type, List<ObjType> data, PROPERTY prop,
                                            PROPERTY prop2) {
        List<ObjType> list = new LinkedList<>();
        for (ObjType child : data) {
            // if (!type.isUpgrade())
            // continue;
            if (!StringMaster.compareByChar(child.getProperty(prop), type.getProperty(prop2))) {
                continue;
            }
            list.add(child);
        }
        return list;
    }

    public static ObjType getRandomType(OBJ_TYPES TYPE, String group) {
        List<ObjType> list = getTypesGroup(TYPE, group);
        return list.get(RandomWizard.getRandomListIndex(list));
    }

}
