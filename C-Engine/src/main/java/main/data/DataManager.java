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

public class DataManager {

    public static final String BF_TYPES = "CHARS;UNITS;BF OBJ;TERRAIN";
    public static final String MISC = "Misc";
    public static final String ATTR = "Attribute";
    public static final String MSTR = "Mastery";
    public static final String ENCH = "Passive";
    public static final String EMPTY = "Empty";
    private static Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> itemMaps = new ConcurrentMap();
    protected static ObjType[] baseWeaponTypes;
     protected static ObjType[] baseJewelryTypes;
     protected static ObjType[] baseItemTypes;
     protected static ObjType[] baseArmorTypes;
     protected static ObjType[] baseGarmentTypes;
     protected static ObjType[] baseAllItemTypes;

     protected static final   DataBackend backend= new SmartBackend();

    public static void init() {
        backend.init();
    }

    public static List<List<String>> getTabTreeData(PROPERTY filterValue, String type) {
        return backend.getTabTreeData(filterValue, type);
    }

    public static ObjType getType(String typeName) {
        return backend.getType(typeName);
    }

    public static ObjType getType(String typeName, boolean recursion) {
        return backend.getType(typeName, recursion);
    }

    public static ObjType getType(String typeName, OBJ_TYPE obj_type) {
        return backend.getType(typeName, obj_type);
    }

    public static ObjType getTypeNoCache(String typeName, OBJ_TYPE obj_type) {
        return backend.getTypeNoCache(typeName, obj_type);
    }

    public static List<ObjType> findTypes(String typeName, boolean strict) {
        return backend.findTypes(typeName, strict);
    }

    public static List<ObjType> findTypes(String typeName, boolean strict, OBJ_TYPE... TYPES) {
        return backend.findTypes(typeName, strict, TYPES);
    }

    public static ObjType findType(String typeName, OBJ_TYPE TYPE) {
        return backend.findType(typeName, TYPE);
    }

    public static ObjType getType(String typeName, OBJ_TYPE TYPE, boolean recursion) {
        return backend.getType(typeName, TYPE, recursion);
    }

    public static Map<String, ObjType> getTypeMap(String type) {
        return backend.getTypeMap(type);
    }

    public static Map<String, ObjType> getTypeMap(OBJ_TYPE type) {
        return backend.getTypeMap(type);
    }

    public static void displayData() {
        backend.displayData();
    }

    public static ObjType getOrAddType(String name, OBJ_TYPE TYPE) {
        return backend.getOrAddType(name, TYPE);
    }

    public static void addType(String name, String group, ObjType type) {
        backend.addType(name, group, type);
    }

    public static void addType(String name, OBJ_TYPE group, ObjType type) {
        backend.addType(name, group, type);
    }

    public static void renameType(ObjType type, String newName) {
        backend.renameType(type, newName);
    }

    public static void removeType(ObjType t) {
        backend.removeType(t);
    }

    public static void removeType(String name, String group) {
        backend.removeType(name, group);
    }

    public static PROPERTY getGroupingKey(String typeName) {
        return backend.getGroupingKey(typeName);
    }

    public static PROPERTY getSubGroupingKey(String typeName) {
        return backend.getSubGroupingKey(typeName);
    }

    public static ObjType getType(String key, String group) {
        return backend.getType(key, group);
    }

    public static PROPERTY getGroupingKey(OBJ_TYPE TYPE) {
        return backend.getGroupingKey(TYPE);
    }

    public static List<ObjType> getTypes() {
        return backend.getTypes();
    }

    public static List<ObjType> getBaseTypes(OBJ_TYPE key) {
        return backend.getBaseTypes(key);
    }

    public static List<ObjType> getTypes(OBJ_TYPE key) {
        return backend.getTypes(key);
    }

    public static List<ObjType> getTypes(OBJ_TYPE key, boolean shuffled) {
        return backend.getTypes(key, shuffled);
    }

    public static List<String> getTypeNames(OBJ_TYPE TYPE) {
        return backend.getTypeNames(TYPE);
    }

    public static List<ObjType> getTypesSubGroup(OBJ_TYPE TYPE, String subgroup) {
        return backend.getTypesSubGroup(TYPE, subgroup);
    }

    public static List<String> getTypesSubGroupNames(OBJ_TYPE TYPE, String subgroup) {
        return backend.getTypesSubGroupNames(TYPE, subgroup);
    }

    public static boolean isCustomGroup(String subgroup) {
        return backend.isCustomGroup(subgroup);
    }

    public static List<String> getFilteredTypeNameList(String filter, OBJ_TYPE OBJ_TYPE, VALUE filterValue) {
        return backend.getFilteredTypeNameList(filter, OBJ_TYPE, filterValue);
    }

    public static List<ObjType> getFilteredTypes(OBJ_TYPE OBJ_TYPE, String filter, VALUE filterValue) {
        return backend.getFilteredTypes(OBJ_TYPE, filter, filterValue);
    }

    public static List<String> getTypesGroupNames(OBJ_TYPE TYPE, String group) {
        return backend.getTypesGroupNames(TYPE, group);
    }

    public static List<ObjType> getTypesGroup(OBJ_TYPE TYPE, String group) {
        return backend.getTypesGroup(TYPE, group);
    }

    public static List<String> getTypeNamesGroup(OBJ_TYPE TYPE, String group) {
        return backend.getTypeNamesGroup(TYPE, group);
    }

    public static boolean isTypeName(String item) {
        return backend.isTypeName(item);
    }

    public static boolean isTypeName(String item, OBJ_TYPE TYPE) {
        return backend.isTypeName(item, TYPE);
    }

    public static boolean isTypeName(String item, OBJ_TYPE TYPE, boolean allowVarPart) {
        return backend.isTypeName(item, TYPE, allowVarPart);
    }

    public static List<String> getHeroList(String res_level) {
        return backend.getHeroList(res_level);
    }

    public static List<ObjType> toTypeList(String string, OBJ_TYPE TYPE) {
        return backend.toTypeList(string, TYPE);
    }

    public static List<ObjType> toTypeList(Collection<String> strings, OBJ_TYPE TYPE) {
        return backend.toTypeList(strings, TYPE);
    }

    public static Collection<? extends Obj> toObjList(Collection<Integer> values, Game game) {
        return backend.toObjList(values, game);
    }

    public static String toString(Collection<? extends Entity> listTypeData) {
        return backend.toString(listTypeData);
    }

    public static List<String> toStringList(Collection<? extends Entity> listTypeData) {
        return backend.toStringList(listTypeData);
    }

    public static List<String> convertObjToStringList(Collection<? extends Obj> listTypeData) {
        return backend.convertObjToStringList(listTypeData);
    }

    public static Class<?> getGroupingClass(OBJ_TYPE TYPE) {
        return backend.getGroupingClass(TYPE);
    }

    public static List<String> getTabsGroup(OBJ_TYPE TYPE) {
        return backend.getTabsGroup(TYPE);
    }

    public static void addType(ObjType type) {
        backend.addType(type);
    }

    public static ObjType addType(String typeName, OBJ_TYPE TYPE) {
        return backend.addType(typeName, TYPE);
    }

    public static void overwriteType(ObjType type) {
        backend.overwriteType(type);
    }

    public static void reloadOverwrittenTypes() {
        backend.reloadOverwrittenTypes();
    }

    public static boolean checkTypeName(String newName) {
        return backend.checkTypeName(newName);
    }

    public static List<ObjType> toTypeList(Collection<? extends Entity> data) {
        return backend.toTypeList(data);
    }

    public static boolean isIdSorted(OBJ_TYPE TYPE) {
        return backend.isIdSorted(TYPE);
    }

    public static Map<C_OBJ_TYPE, List<ObjType>> getCustomObjTypeCache() {
        return backend.getCustomObjTypeCache();
    }


    public static List<ObjType> getRootTypes(List<ObjType> data) {
        return backend.getRootTypes(data);
    }

    public static List<ObjType> toTypeList(List<ObjAtCoordinate> types) {
        return backend.toTypeList(types);
    }

    public static List<ObjType> getSublings(ObjType type, List<ObjType> data) {
        return backend.getSublings(type, data);
    }

    public static ObjType getItem(QUALITY_LEVEL quality, MATERIAL material, ObjType type) {
        return backend.getItem(quality, material, type);
    }

    public static ObjType getParent(ObjType type) {
        return backend.getParent(type);
    }

    public static List<ObjType> getChildren(ObjType type, List<ObjType> data) {
        return backend.getChildren(type, data);
    }

    public static List<ObjType> getChildren(ObjType type, List<ObjType> data, PROPERTY prop, PROPERTY prop2) {
        return backend.getChildren(type, data, prop, prop2);
    }

    public static ObjType getRandomType(OBJ_TYPE TYPE) {
        return backend.getRandomType(TYPE);
    }

    public static ObjType getRandomType(OBJ_TYPE TYPE, String group) {
        return backend.getRandomType(TYPE, group);
    }

    public static List<ObjType> getUpgradedTypes(ObjType baseType) {
        return backend.getUpgradedTypes(baseType);
    }

    public static ObjType[] getBaseWeaponTypes() {
        return baseWeaponTypes;
    }

    public static void setBaseWeaponTypes(ObjType[] baseWeaponTypes) {
        DataManager.baseWeaponTypes = baseWeaponTypes;
    }

    public static ObjType[] getBaseJewelryTypes() {
        return baseJewelryTypes;
    }

    public static void setBaseJewelryTypes(ObjType[] baseJewelryTypes) {
        DataManager.baseJewelryTypes = baseJewelryTypes;
    }

    public static ObjType[] getBaseItemTypes() {
        return baseItemTypes;
    }

    public static void setBaseItemTypes(ObjType[] baseItemTypes) {
        DataManager.baseItemTypes = baseItemTypes;
    }

    public static ObjType[] getBaseArmorTypes() {
        return baseArmorTypes;
    }

    public static void setBaseArmorTypes(ObjType[] baseArmorTypes) {
        DataManager.baseArmorTypes = baseArmorTypes;
    }

    public static ObjType[] getBaseGarmentTypes() {
        return baseGarmentTypes;
    }

    public static void setBaseGarmentTypes(ObjType[] baseGarmentTypes) {
        DataManager.baseGarmentTypes = baseGarmentTypes;
    }

    public static ObjType[] getBaseAllItemTypes() {
        return baseAllItemTypes;
    }

    public static void setBaseAllItemTypes(ObjType[] baseAllItemTypes) {
        DataManager.baseAllItemTypes = baseAllItemTypes;
    }

    public static boolean isTypesRead(DC_TYPE type) {
        return backend.isTypesRead(type);
    }

    public static String getObjImage(String name) {
        return backend.getObjImage(name);
    }

    public static void setItemMaps(Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> a) {
        itemMaps = a;
    }

    public static Map<QUALITY_LEVEL, Map<MATERIAL, Map<ObjType, ObjType>>> getItemMaps() {
        return itemMaps;
    }
}
