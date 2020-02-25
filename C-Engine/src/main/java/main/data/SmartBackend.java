package main.data;

import main.content.OBJ_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;

import java.util.Collection;
import java.util.List;

public class SmartBackend extends DataBackend {

    public List<ObjType> buildTypes(List<ObjType> list) {
        list.forEach(type -> type.checkBuild() );
        return list;
    }

    public ObjType buildType(ObjType type) {
        if (type == null) {
            return null;
        }
        type.checkBuild() ;
        return type;
    }

    @Override
    public List<ObjType> toTypeList(String string, OBJ_TYPE TYPE) {

        return buildTypes(super.toTypeList(string, TYPE));
    }

    @Override
    public ObjType getType(String key, String group) {
        return buildType(super.getType(key, group));
    }

    @Override
    public List<ObjType> getBaseTypes(OBJ_TYPE key) {
        return buildTypes(super.getBaseTypes(key));
    }


    @Override
    public List<ObjType> toTypeList(Collection<String> strings, OBJ_TYPE TYPE) {
        return buildTypes(super.toTypeList(strings, TYPE));
    }


    @Override
    public List<ObjType> getRootTypes(List<ObjType> data) {
        return buildTypes(super.getRootTypes(data));
    }

    @Override
    public List<ObjType> toTypeList(List<ObjAtCoordinate> types) {
        return buildTypes(super.toTypeList(types));
    }

    @Override
    public List<ObjType> getSublings(ObjType type, List<ObjType> data) {
        return buildTypes(super.getSublings(type, data));
    }

    @Override
    public ObjType getItem(ItemEnums.QUALITY_LEVEL quality, ItemEnums.MATERIAL material, ObjType type) {
        return buildType(super.getItem(quality, material, type));
    }

    @Override
    public ObjType getParent(ObjType type) {
        return buildType(super.getParent(type));
    }

    @Override
    public List<ObjType> getChildren(ObjType type, List<ObjType> data) {
        return buildTypes(super.getChildren(type, data));
    }

    @Override
    public List<ObjType> getChildren(ObjType type, List<ObjType> data, PROPERTY prop, PROPERTY prop2) {
        return buildTypes(super.getChildren(type, data, prop, prop2));
    }

    @Override
    public ObjType getRandomType(OBJ_TYPE TYPE) {
        return buildType(super.getRandomType(TYPE));
    }

    @Override
    public ObjType getRandomType(OBJ_TYPE TYPE, String group) {
        return buildType(super.getRandomType(TYPE, group));
    }

    @Override
    public List<ObjType> getUpgradedTypes(ObjType baseType) {
        return buildTypes(super.getUpgradedTypes(baseType));
    }

    @Override
    public ObjType getType(String typeName) {
        return buildType(super.getType(typeName));
    }

    @Override
    public ObjType getType(String typeName, boolean recursion) {
        return buildType(super.getType(typeName, recursion));
    }

    @Override
    public ObjType getType(String typeName, OBJ_TYPE obj_type) {
        return buildType(super.getType(typeName, obj_type));
    }

    @Override
    public ObjType getTypeNoCache(String typeName, OBJ_TYPE obj_type) {
        return buildType(super.getTypeNoCache(typeName, obj_type));
    }

    @Override
    public List<ObjType> findTypes(String typeName, boolean strict) {
        return buildTypes(super.findTypes(typeName, strict));
    }

    @Override
    public List<ObjType> findTypes(String typeName, boolean strict, OBJ_TYPE... TYPES) {
        return buildTypes(super.findTypes(typeName, strict, TYPES));
    }

    @Override
    public List<ObjType> getTypesGroup(OBJ_TYPE TYPE, String group) {
        return buildTypes(super.getTypesGroup(TYPE, group));
    }

    @Override
    public ObjType getType(String typeName, OBJ_TYPE TYPE, boolean recursion) {
        return buildType(super.getType(typeName, TYPE, recursion));
    }
}
