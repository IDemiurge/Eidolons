package main.data;

import main.content.*;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.enums.entity.ItemEnums.QUALITY_LEVEL;
import main.content.enums.macro.C_MACRO_OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.ability.construct.VariableManager;
import main.data.xml.XML_Reader;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.system.auxiliary.*;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

import java.util.*;
import java.util.Map.Entry;

/**
 * Contains utility methods for finding and managing ObjType's
 */

public class DataBackend {

    public   final String MISC = "Misc";
    public   final String ATTR = "Attribute";
    public   final String MSTR = "Mastery";
    public   final String ENCH = "Passive";
    public   final String EMPTY = "Empty";
    public   final String[] CUSTOM_JEWELRY_GROUPS = {MISC, MISC, MSTR, ENCH, ATTR, EMPTY,};
    private   final String RES_LEVEL_PROP = G_PROPS.RANK.name();
    private   Map<OBJ_TYPE, Map<String, List<String>>> typesSubGroups;
    private   List<ObjType> overwrittenTypes;
    private   Map<OBJ_TYPE, Map<String, List<String>>> subGroupsMaps;
    private   Map<C_OBJ_TYPE, List<ObjType>> customObjTypeCache;
    private final Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> itemMaps = new ConcurrentMap();
    private final Map<OBJ_TYPE, Map<String, ObjType>> caches = new HashMap<>();

    private Map<String, String> uuidToNameMap = new LinkedHashMap<>();
    private Map<String, String> nameToIdMap = new LinkedHashMap<>();

    public   void init() {
        subGroupsMaps = new HashMap<>();
        for (DC_TYPE T : DC_TYPE.values()) {
            subGroupsMaps.put(T, new HashMap<>());
        }
    }

    public   List<List<String>> getTabTreeData(PROPERTY filterValue, String type) {
        List<List<String>> lists = new ArrayList<>();

        for (String criterion : XML_Reader.getSubGroups(type)) {
            if (criterion == null) {
                continue;
            }
            lists.add(getFilteredTypeNameList(criterion, ContentValsManager.getOBJ_TYPE(type),
                    filterValue));
        }

        return lists;
    }

    public   ObjType getType(String typeName) {
        return getType(typeName, false);
    }

    public   ObjType getType(String typeName, boolean recursion) {
        for (String group : XML_Reader.getTypeMaps().keySet()) {
            ObjType type;
            OBJ_TYPE TYPE = ContentValsManager.getOBJ_TYPE(group);
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

    public   ObjType getType(String typeName, OBJ_TYPE obj_type) {
        if (isUseUUID(obj_type)) {
            typeName = nameToIdMap.get(typeName);
        }
        Map<String, ObjType> cache = caches.get(obj_type);
        if (cache == null)
            caches.put(obj_type, cache = new HashMap<>());

        ObjType t = cache.get(typeName.toLowerCase());
        if (t != null) {
            return t;
        }
        t = getTypeNoCache(typeName, obj_type);
        cache.put(typeName.toLowerCase(), t);
        return t;
    }

    public   ObjType getTypeNoCache(String typeName, OBJ_TYPE obj_type) {

        if (typeName.isEmpty())
            return null;
        ObjType type = getType(typeName, obj_type, true);
        if (type == null) {
            if (C_OBJ_TYPE.ITEMS.equals(obj_type))
                try {
                    return Game.game.getItemGenerator().generateItemType(StringMaster.format(
                            typeName), obj_type);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    return null;
                }
        }
        return type;
    }

    public   List<ObjType> findTypes(String typeName, boolean strict) {
        List<ObjType> list = new ArrayList<>();
        for (DC_TYPE TYPE : DC_TYPE.values()) {
            list.addAll(findTypes(typeName, strict, TYPE));
        }
        return list;
    }

    public   List<ObjType> findTypes(String typeName, boolean strict, OBJ_TYPE... TYPES) {

        List<ObjType> list = new ArrayList<>();

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

    public   ObjType findType(String typeName, OBJ_TYPE TYPE) {
        List<ObjType> list = (findTypes(typeName, false, TYPE));
        return new SearchMaster<ObjType>().findClosest(typeName, list);
    }

    public   ObjType getType(String typeName, OBJ_TYPE TYPE, boolean recursion) {
        if (C_OBJ_TYPE.ITEMS.equals(TYPE)) {
            // 1) get quality 2) find base type name 3) crop and use
            ObjType type = null;
            try {
                type = getItemType(typeName, TYPE);
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
        if (TYPE == null) {
            if (recursion) {
                return getType(typeName, false);
            }
        }

        if (TYPE instanceof C_OBJ_TYPE) {
            return getConstructedType(typeName, (C_OBJ_TYPE) TYPE);
        }
        if (TYPE instanceof C_MACRO_OBJ_TYPE) {
            return getConstructedMacroType(typeName, (C_MACRO_OBJ_TYPE) TYPE);
        }

        Map<String, ObjType> map = getTypeMap(TYPE);
        if (map == null) {
            //main.system.auxiliary.src.main.log.LogMaster.src.main.log(src.main.log,"NO TYPE MAP: "+obj_type );
            return null;
        }
        ObjType type = map.get(typeName);
        if (type == null) {
            for (String item : map.keySet()) {
                if (item.equalsIgnoreCase(typeName))
                    type = map.get(item);
            }
        }

        if (type != null) {
            return type;
        }

        if (!recursion) {
            return null;
        }
        if (typeName.endsWith(";")) {
            typeName = StringMaster.getFormattedTypeName(typeName);
            type = getType(typeName, TYPE, false);
            if (type != null) {
                return type;
            }
        }
        return getType(StringMaster.format(typeName), TYPE, false);

    }

    private   ObjType getConstructedMacroType(String typeName, C_MACRO_OBJ_TYPE obj_type) {
        ObjType type = null;
        for (MACRO_OBJ_TYPES TYPE : (obj_type).getTypes()) {
            type = getType(typeName, TYPE, false);
            if (type != null) {
                return type;
            }
        }
        LogMaster.log(LogMaster.DATA_DEBUG, "Type not found: " + obj_type
                + ":" + typeName);
        return type;
    }

    // refactor
    private   ObjType getConstructedType(String typeName, C_OBJ_TYPE obj_type) {
        ObjType type = null;
        for (DC_TYPE TYPE : obj_type.getTypes()) {
            type = getType(typeName, TYPE, false);
            if (type != null) {
                return type;
            }
        }
        LogMaster.log(LogMaster.DATA_DEBUG, "Type not found: " + obj_type
                + ":" + typeName);
        return type;
    }

    private   ObjType getItemType(String typeName, OBJ_TYPE obj_type) {
        ObjType type = getBaseItemType(typeName, obj_type);
        if (type != null) {
            return type;
        }

        if (obj_type.equals(DC_TYPE.JEWELRY)) {
            //main.system.auxiliary.src.main.log.LogMaster.src.main.log(src.main.log,"NO JEWELRY!  "  );
            return null;
        }
        int i = 0;
        List<String> parts = ContainerUtils.openContainer(typeName, " ");
        String qualityName = parts.get(0);
        QUALITY_LEVEL q = new EnumMaster<QUALITY_LEVEL>().retrieveEnumConst(QUALITY_LEVEL.class,
                qualityName);
        if (q == null) {
            q = ItemEnums.QUALITY_LEVEL.NORMAL;
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

        i++;
        StringBuilder baseTypeName = new StringBuilder();
        for (int a = i; a <= parts.size() - 1; a++) {
            baseTypeName.append(parts.get(a)).append(" ");
        }

        ObjType baseType = getBaseItemType(baseTypeName.toString(), obj_type);

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

    private   ObjType getBaseItemType(String string, OBJ_TYPE obj_type) {
        ObjType[] list = null;
        if (obj_type.equals(C_OBJ_TYPE.ITEMS)) {
            list = (DataManager.baseAllItemTypes);
        } else if (obj_type.equals(DC_TYPE.ARMOR)) {
            list = (DataManager.baseArmorTypes);
        } else if (obj_type.equals(DC_TYPE.WEAPONS)) {
            list = (DataManager.baseWeaponTypes);
        } else if (obj_type.equals(DC_TYPE.ITEMS)) {
            list = (DataManager.baseItemTypes);
        } else if (obj_type.equals(DC_TYPE.JEWELRY)) {
            list = (DataManager.baseJewelryTypes);
        }

        for (ObjType t : list) {
            if (StringMaster.compareByChar(string.toLowerCase().trim(), t.getName().toLowerCase(), true)) {
                return t;
            }
        }
        return null;
    }

    public   Map<String, ObjType> getTypeMap(String type) {
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
            if (ContentValsManager.getOBJ_TYPE(type) == null) {
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

    protected XML_Reader getXmlReader() {
        return XML_Reader.getInstance();
    }

    public   Map<String, ObjType> getTypeMap(OBJ_TYPE type) {
        return getTypeMap(type.toString());
    }

    public   void displayData() {
        for (Map<String, ObjType> m : XML_Reader.getTypeMaps().values()) {
            for (Entry<String, ObjType> t : m.entrySet()) {
                //LogMaster.src.main.log(src.main.log, t.getKey() + " " + t.getValue().toString());
            }
        }
    }

    public   ObjType getOrAddType(String name, OBJ_TYPE TYPE) {
        ObjType type = getType(name, TYPE);
        if (type == null) {
            type = addType(name, TYPE);
        }
        return type;
    }

    public   void addType(String name, String group, ObjType type) {
        String key = name.trim();
        if (isUseUUID(type.getOBJ_TYPE_ENUM())){
            uuidToNameMap.put(key= type.getUniqueId(), key);
            //append something if already exists?
            nameToIdMap.put(name.trim(), key);
        }
        getTypeMap(group).put(key, type);

    }

    private static boolean isUseUUID(OBJ_TYPE TYPE) {
        return false;
    }
    public   void addType(String name, OBJ_TYPE group, ObjType type) {
        addType(name, group.toString(), type);
    }

    public   void renameType(ObjType type, String newName) {
        ObjType prev = XML_Reader.getTypeMaps().get(type.getProperty(G_PROPS.TYPE)).remove(type.getName());
        XML_Reader.getTypeMaps().get(type.getProperty(G_PROPS.TYPE)).put(newName, type);

    }

    public   void removeType(ObjType t) {
        ObjType removed = getTypeMap(t.getOBJ_TYPE_ENUM()).remove(t.getName());
    }

    public   void removeType(String name, String group) {
        getTypeMap(group).remove(getType(name, group).getName());

    }

    public   PROPERTY getGroupingKey(String typeName) {
        OBJ_TYPE TYPE = ContentValsManager.getOBJ_TYPE(typeName);

        if (TYPE == null) {
            return null;
        }
        return TYPE.getGroupingKey();
    }

    public   PROPERTY getSubGroupingKey(String typeName) {
        OBJ_TYPE TYPE = ContentValsManager.getOBJ_TYPE(typeName);
        if (TYPE == null) {
            return null;
        }
        return TYPE.getSubGroupingKey();
    }

    public   ObjType getType(String key, String group) {
        if (group == null) {
            return getType(key);
        }
        return getType(key, ContentValsManager.getOBJ_TYPE(group));
    }

    public   PROPERTY getGroupingKey(OBJ_TYPE TYPE) {
        return TYPE.getGroupingKey();
    }

    public   List<ObjType> getTypes() {
        List<ObjType> list = new ArrayList<>();
        for (DC_TYPE type : DC_TYPE.values()) {
            list.addAll(getTypes(type));
        }
        return list;
    }

    public   List<ObjType> getBaseTypes(OBJ_TYPE key) {
        List<ObjType> types = new ArrayList<>(getTypes(key));
        types.removeIf(t -> t.isGenerated());
        return types;
    }

    public   List<ObjType> getTypes(OBJ_TYPE key) {
        return getTypes(key, false);
    }

    public   List<ObjType> getTypes(OBJ_TYPE key, boolean shuffled) {
        if (key instanceof C_OBJ_TYPE) {

            List<ObjType> group = getCustomObjTypeCache().get(key);
            if (group == null) {
                group = new ArrayList<>();
            }
            for (DC_TYPE type : ((C_OBJ_TYPE) key).getTypes()) {
                group.addAll(getTypes(type));
            }
            getCustomObjTypeCache().put((C_OBJ_TYPE) key, group);
            return group;
        }

        Map<String, ObjType> map = getTypeMap(key);
        if (map == null) {
            return new ArrayList<>();
        }
        List<ObjType> list = new ArrayList<>(map.values());
        if (shuffled)
            Collections.shuffle(list);
        return list;
    }

    public   List<String> getTypeNames(OBJ_TYPE TYPE) {
        if (TYPE instanceof C_OBJ_TYPE) {
            List<String> group = new ArrayList<>();
            for (DC_TYPE type : ((C_OBJ_TYPE) TYPE).getTypes()) {
                group.addAll(getTypeNames(type));
            }
            return group;
        }
        return new ArrayList<>(getTypeMap(TYPE).keySet());
    }

    public   List<ObjType> getTypesSubGroup(OBJ_TYPE TYPE, String subgroup) {
        return toTypeList(getTypesSubGroupNames(TYPE, subgroup), TYPE);
        // TODO refactor!
    }

    public   List<String> getTypesSubGroupNames(OBJ_TYPE TYPE, String subgroup) {
        if (TYPE instanceof C_OBJ_TYPE) {
            List<String> data = null;
            for (DC_TYPE type : ((C_OBJ_TYPE) TYPE).getTypes()) {
                try {
                    data = getTypesSubGroupNames(type, subgroup);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
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

//        List<String> list = getTypesSubGroups().get(TYPE).get(subgroup);
        // if (list != null)
        // return list;

        //        List<String> groupsList = EnumMaster.findEnumConstantNames(TYPE.getSubGroupingKey()
        //         .toString());
        //        // TODO preCheck TYPES!
        //        if (groupsList.isEmpty()) {
        //            if (DC_TYPE.isOBJ_TYPE(subgroup)) {
        //                groupsList = toStringList(getTypes(DC_TYPE.getType(subgroup)));
        //            } else {
        //                if (isTypeName(subgroup)) {
        //                    groupsList = toStringList(getTypes(getType(subgroup).getOBJ_TYPE_ENUM()));
        //                }
        //            }
        //        }
        // if (ListMaster.contains(groupsList, subgroup, true)
        // || isCustomGroup(subgroup)) {

        List<String> list = new ArrayList<>();

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

    public   boolean isCustomGroup(String subgroup) {
        return Arrays.asList(CUSTOM_JEWELRY_GROUPS).contains(subgroup);
    }

    // TODO SORT
    public   List<String> getFilteredTypeNameList(String filter, OBJ_TYPE OBJ_TYPE,
                                                       VALUE filterValue) {
        Map<String, ObjType> map = XML_Reader.getTypeMaps().get(OBJ_TYPE.toString());
        if (map == null) {
            char[] chars = OBJ_TYPE.toString().toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            map = XML_Reader.getTypeMaps().get(new String(chars));
        }
        List<String> list = new ArrayList<>();
        for (String objName : map.keySet()) {
            if (StringMaster.compare(map.get(objName).getValue(filterValue), filter)) {
                list.add(objName);
            }
        }
        return list;
    }

    public   List<ObjType> getFilteredTypes(OBJ_TYPE OBJ_TYPE, String filter, VALUE filterValue) {
        Map<String, ObjType> map = XML_Reader.getTypeMaps().get(OBJ_TYPE.toString());

        List<ObjType> list = new ArrayList<>();
        boolean or = false;
        if (filter.contains(Strings.OR)) {
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

    public   List<String> getTypesGroupNames(OBJ_TYPE TYPE, String group) {
        return toStringList(getTypesGroup(TYPE, group));
    }

    public   List<ObjType> getTypesGroup(OBJ_TYPE TYPE, String group) {
        if (StringMaster.isEmpty(group)) {
            return getTypes(TYPE);
        }
        List<ObjType> list = new ArrayList<>();
        if (TYPE instanceof C_OBJ_TYPE) {
            for (DC_TYPE T : ((C_OBJ_TYPE) TYPE).getTypes())
                list.addAll(getTypesGroup(T, group));
            return list;
        }
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



    public   List<String> getTypeNamesGroup(OBJ_TYPE TYPE, String group) {
        if (group == null) {
            return getTypeNames(TYPE);
        }
        List<String> list = new ArrayList<>();
        Map<String, Map<String, ObjType>> map = XML_Reader.getTypeMaps();
        Collection<ObjType> set = new HashSet<>();
        if (TYPE instanceof C_OBJ_TYPE) {
            for (DC_TYPE T : ((C_OBJ_TYPE) TYPE).getTypes()) {
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

    public   boolean isTypeName(String item) {
        return isTypeName(item, null);
    }

    public   boolean isTypeName(String item, OBJ_TYPE TYPE) {
        return isTypeName(item, TYPE, false);
    }

    public   boolean isTypeName(String item, OBJ_TYPE TYPE, boolean allowVarPart) {
        if (item == null) {
            return false;
        }
        if (!allowVarPart)
            item = VariableManager.removeVarPart(item);
        return (getType(item, TYPE) != null);
    }

    public   List<String> getHeroList(String res_level) {
        List<String> list = new ArrayList<>();

        for (String name : getTypeNames(DC_TYPE.CHARS)) {
            ObjType type = getType(name, DC_TYPE.CHARS);
            if (res_level == null) {
                list.add(name);
                continue;
            }
            if (StringMaster.compare(type.getProperty(ContentValsManager.getPROP(RES_LEVEL_PROP)),
                    res_level)) {
                list.add(name);
            }
        }
        return list;
    }

    public   List<ObjType> toTypeList(String string, OBJ_TYPE TYPE) {
        return toTypeList(ContainerUtils.openContainer(string), TYPE);
    }

    public   List<ObjType> toTypeList(Collection<String> strings, OBJ_TYPE TYPE) {
        if (strings == null) {
            return null;
        }
        List<ObjType> list = new ArrayList<>();
        for (String string : strings) {
            ObjType type = getType(string, TYPE);
            if (type == null) {
                if (NumberUtils.isInteger(string)) {
                    type = Game.game.getTypeById(NumberUtils.getIntParse(string));
                    if (type == null) {
                        Obj obj = Game.game.getObjectById(NumberUtils.getIntParse(string));
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

    public   Collection<? extends Obj> toObjList(Collection<Integer> values, Game game) {
        List<Obj> list = new ArrayList<>();
        for (Integer id : values) {
            list.add(game.getObjectById(id));
        }
        return list;
    }

    public   String toString(Collection<? extends Entity> listTypeData) {
        return ContainerUtils.constructContainer(toStringList(listTypeData));
    }

    public   List<String> toStringList(Collection<? extends Entity> listTypeData) {
        List<String> list = new ArrayList<>();
        for (Entity type : listTypeData) {
            if (type == null) {
                continue;
            }
            list.add(type.getName());
        }
        return list;
    }

    public   List<String> convertObjToStringList(Collection<? extends Obj> listTypeData) {
        List<String> list = new ArrayList<>();
        for (Entity type : listTypeData) {
            list.add(type.getName());
        }
        return list;
    }

    private   Map<OBJ_TYPE, Map<String, List<String>>> getTypesSubGroups() {
        if (typesSubGroups == null) {
            typesSubGroups = new HashMap<>();
            for (String sub : XML_Reader.getTypeMaps().keySet()) {
                typesSubGroups.put(ContentValsManager.getOBJ_TYPE(sub),
                        new HashMap<>());
            }
        }
        return typesSubGroups;
    }


    public   Class<?> getGroupingClass(OBJ_TYPE TYPE) {
        return EnumMaster.getEnumClass(TYPE.getGroupingKey().getName(), CONTENT_CONSTS.class);
    }


    public   List<String> getTabsGroup(OBJ_TYPE TYPE) {

        if (TYPE instanceof C_OBJ_TYPE) {
            List<String> group = new ArrayList<>();
            for (DC_TYPE type : ((C_OBJ_TYPE) TYPE).getTypes()) {
                group.addAll(XML_Reader.getTabGroupMap().get(type.getName()));
            }
            return group;
        }
        if (!ListMaster.isNotEmpty(XML_Reader.getTabGroupMap().get(TYPE.getName()))) {
            return new ArrayList<>();
        }
        return new ArrayList<>(XML_Reader.getTabGroupMap().get(TYPE.getName()));
    }

    public   void addType(ObjType type) {
        String name = type.getName();
        if (name.isEmpty()) {
            return;
        }
        addType(name, type.getOBJ_TYPE_ENUM(), type);
    }

    public   ObjType addType(String typeName, OBJ_TYPE TYPE) {
        ObjType objType = new ObjType(typeName, TYPE);
        addType(objType);
        return objType;
    }

    public   void overwriteType(ObjType type) {
        ObjType oldType = getType(type.getName(), type.getOBJ_TYPE_ENUM());
        if (oldType != null) {
            if (overwrittenTypes == null) {
                overwrittenTypes = new ArrayList<>();
            }
            overwrittenTypes.add(oldType);
        }
        addType(type);

    }

    public   void reloadOverwrittenTypes() {
        if (overwrittenTypes == null) {
            return;
        }
        for (ObjType type : overwrittenTypes) {
            addType(type);
        }
        overwrittenTypes.clear();
    }

    public   boolean checkTypeName(String newName) {

        return StringMaster.checkSymbolsStandard(newName); // TODO
    }

    public   List<ObjType> toTypeList(Collection<? extends Entity> data) {
        List<ObjType> list = new ArrayList<>();
        for (Entity e : data) {
            list.add(e.getType());
        }
        return list;
    }


    private   List<String> getSubGroupsForTYPE(OBJ_TYPE TYPE, String group) {
        if (TYPE instanceof C_OBJ_TYPE) {
            for (DC_TYPE T : ((C_OBJ_TYPE) TYPE).getTypes()) {
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

        List<String> filteredSubGroups = new ArrayList<>();

        for (String subgroup : fullList) {
            if (allGroupsForType.contains(subgroup)) {
                filteredSubGroups.add(subgroup);
            }
        }
        subGroupsMaps.get(TYPE).put(group, filteredSubGroups);

        return filteredSubGroups;
    }

    public   boolean isIdSorted(OBJ_TYPE TYPE) {
        if (TYPE instanceof DC_TYPE) {
            if (TYPE == DC_TYPE.DEITIES) {
                return true;
            }
            return TYPE == DC_TYPE.CHARS;
        }

        return false;
    }

    public   Map<C_OBJ_TYPE, List<ObjType>> getCustomObjTypeCache() {
        if (customObjTypeCache == null) {
            customObjTypeCache = new HashMap<>();
        }
        return customObjTypeCache;
    }

    public   Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> getItemMaps() {
        return itemMaps;
    }


    public   List<ObjType> getRootTypes(List<ObjType> data) {
        List<ObjType> list = new ArrayList<>();
        for (ObjType type : data) {
            if (type.isUpgrade()) {
                continue;
            }
            list.add(type);
        }
        return list;
    }

    public   List<ObjType> toTypeList(List<ObjAtCoordinate> types) {
        List<ObjType> list = new ArrayList<>();
        for (ObjAtCoordinate type : types) {
            list.add(type.getType());
        }
        return list;
    }


    public   List<ObjType> getSublings(ObjType type, List<ObjType> data) {
        if (getParent(type) == null) {
            return getRootTypes(data);
        }
        return getChildren(getParent(type), data);
    }

    public   ObjType getItem(QUALITY_LEVEL quality, MATERIAL material, ObjType type) {
        if (GenericItemGenerator.OFF) {
            return type;
        }
        ObjType itemType = DataManager.getItemMaps().get(quality).get(material).get(type);
        if (itemType != null)
            return itemType;
        boolean weapon = type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS;
        return Game.game.getItemGenerator().generateItem(weapon, quality, material, type);
    }

    public   ObjType getParent(ObjType type) {
        String property = type.getProperty(G_PROPS.BASE_TYPE);
        if (property.isEmpty()) {
            if (type.getOBJ_TYPE_ENUM() != DC_TYPE.CLASSES) {
                return null;
            } else if (type.getProp("Class Group").equals("Multiclass")) {
                property = type.getProperty("base classes one");
            }
        }
        return getType(property, type.getOBJ_TYPE_ENUM());
    }

    public   List<ObjType> getChildren(ObjType type, List<ObjType> data) {
        G_PROPS prop = G_PROPS.BASE_TYPE;
        G_PROPS prop2 = G_PROPS.NAME;
        return getChildren(type, data, prop, prop2);

    }

    public   List<ObjType> getChildren(ObjType type, List<ObjType> data, PROPERTY prop,
                                            PROPERTY prop2) {
        List<ObjType> list = new ArrayList<>();
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

    public   ObjType getRandomType(OBJ_TYPE TYPE) {
        return getRandomType(TYPE, null);
    }

    public   ObjType getRandomType(OBJ_TYPE TYPE, String group) {
        List<ObjType> list = getTypesGroup(TYPE, group);
        return list.get(RandomWizard.getRandomIndex(list));
    }

    public   List<ObjType> getUpgradedTypes(ObjType baseType) {
        List<ObjType> list = new ArrayList<>(
                getTypesSubGroup(baseType.getOBJ_TYPE_ENUM(), baseType.getSubGroupingKey()));
        list.removeIf(type -> type.getProperty(G_PROPS.BASE_TYPE).equalsIgnoreCase(baseType.getName()));
        return list;
    }



    public   boolean isTypesRead(DC_TYPE type) {
//        return  !getTypes(type).isEmpty();
        return XML_Reader.getTypeMaps().get(type.getName()) != null;
    }

    public   String getObjImage(String name) {
        ObjType type = getType(name, DC_TYPE.BF_OBJ);
        if (type == null) {
            type = getType(name, DC_TYPE.UNITS);
        }
        if (type == null) {
            type = getType(name);
        }
        if (type == null) {
            return "";
        }
        return type.getImagePath();
    }
}
