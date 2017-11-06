package main.entity;

import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.PropMap;
import main.data.XLinkedMap;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/10/2017.
 */
public class EntityWrapper<E extends DataModel> {
    E entity;

    public EntityWrapper(E entity) {
        this.entity = entity;
    }


    public String getToolTip() {
        return entity.getToolTip();
    }

    public String getDescription() {
        return entity.getDescription();
    }

    public String getCustomValue(String value_ref) {
        return entity.getCustomValue(value_ref);
    }


    public String getCustomProperty(String value_ref) {
        return entity.getCustomProperty(value_ref);
    }

    public Integer getCounter(String value_ref) {
        return entity.getCounter(value_ref);
    }


    public void setGroup(String group, boolean base) {
        entity.setGroup(group, base);
    }


    public boolean setCounter(String name, int newValue) {
        return entity.setCounter(name, newValue);
    }

    public boolean setCounter(String name, int newValue, boolean strict) {
        return entity.setCounter(name, newValue, strict);
    }

    public void removeCounter(String name) {
        entity.removeCounter(name);
    }

    public boolean modifyCounter(String name, int modValue) {
        return entity.modifyCounter(name, modValue);
    }

    public boolean modifyCounter(COUNTER counter, int modValue) {
        return entity.modifyCounter(counter, modValue);
    }

    public boolean modifyCounter(String name, int modValue, boolean strict) {
        return entity.modifyCounter(name, modValue, strict);
    }

    public String find(String p) {
        return entity.find(p);
    }


    public String getParam(String p) {
        return entity.getParam(p);
    }

    public String getParam(PARAMETER param) {
        return entity.getParam(param);
    }


    public String getParamRounded(PARAMETER param, boolean base) {
        return entity.getParamRounded(param, base);
    }


    public Double getParamDouble(PARAMETER param) {
        return entity.getParamDouble(param);
    }


    public Double getParamDouble(PARAMETER param, boolean base) {
        return entity.getParamDouble(param, base);
    }



    public String getDoubleParam(PARAMETER param) {
        return entity.getDoubleParam(param);
    }

    public Game getGame() {
        return entity.getGame();
    }


    public String getDoubleParam(PARAMETER param, boolean base) {
        return entity.getDoubleParam(param, base);
    }


    public Integer getIntParam(String param) {
        return entity.getIntParam(param);
    }

    public String getStrParam(PARAMETER param) {
        return entity.getStrParam(param);
    }


    public Integer getIntParam(PARAMETER param) {
        return entity.getIntParam(param);
    }


    public Integer getIntParam(PARAMETER param, boolean base) {
        return entity.getIntParam(param, base);
    }


    public Map<PARAMETER, Integer> getIntegerMap(boolean base) {
        return entity.getIntegerMap(base);
    }


    public ParamMap getParamMap() {
        return entity.getParamMap();
    }

    public void setParamMap(ParamMap paramMap) {
        entity.setParamMap(paramMap);
    }


    public void getBoolean(VALUE prop, Boolean b) {
        entity.getBoolean(prop, b);
    }


    public Boolean getBoolean(String prop) {
        return entity.getBoolean(prop);
    }

    public String getProperty(String prop) {
        return entity.getProperty(prop);
    }


    public String getProp(String prop) {
        return entity.getProp(prop);
    }

    public String getGroup() {
        return entity.getGroup();
    }

    public String getProperty(PROPERTY prop) {
        return entity.getProperty(prop);
    }

    public boolean checkValue(VALUE v) {
        return entity.checkValue(v);
    }


    public boolean checkValue(VALUE v, String value) {
        return entity.checkValue(v, value);
    }


    public boolean checkParam(PARAMETER param) {
        return entity.checkParam(param);
    }


    public boolean checkParameter(PARAMETER param, int value) {
        return entity.checkParameter(param, value);
    }


    public boolean checkParam(PARAMETER param, String value) {
        return entity.checkParam(param, value);
    }


    public boolean checkProperty(PROPERTY p, String value) {
        return entity.checkProperty(p, value);
    }


    public Map<PROPERTY, Map<String, Boolean>> getPropCache(boolean base) {
        return entity.getPropCache(base);
    }

    public boolean checkProperty(PROPERTY p, String value, boolean base) {
        return entity.checkProperty(p, value, base);
    }

    public boolean checkSingleProp(String PROP, String value) {
        return entity.checkSingleProp(PROP, value);
    }

    public boolean checkSingleProp(PROPERTY PROP, String value) {
        return entity.checkSingleProp(PROP, value);
    }

    public boolean checkContainerProp(PROPERTY PROP, String value) {
        return entity.checkContainerProp(PROP, value);
    }

    public boolean checkContainerProp(PROPERTY PROP, String value, boolean any) {
        return entity.checkContainerProp(PROP, value, any);
    }

    public boolean checkSubGroup(String string) {
        return entity.checkSubGroup(string);
    }

    public boolean checkProperty(PROPERTY p) {
        return entity.checkProperty(p);
    }

    public boolean checkGroup(String string) {
        return entity.checkGroup(string);
    }

    public String getProperty(PROPERTY prop, boolean base) {
        return entity.getProperty(prop, base);
    }

    public PropMap getPropMap() {
        return entity.getPropMap();
    }

    public void setPropMap(PropMap propMap) {
        entity.setPropMap(propMap);
    }

    public Ref getRef() {
        return entity.getRef();
    }

    public void setRef(Ref ref) {
        entity.setRef(ref);
    }

    public ObjType getType() {
        return entity.getType();
    }

    public void setType(ObjType type) {
        entity.setType(type);
    }

    public String getValue(String valName) {
        return entity.getValue(valName);
    }

    public String getValue(VALUE valName) {
        return entity.getValue(valName);
    }

    public String getValue(VALUE val, boolean base) {
        return entity.getValue(val, base);
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, boolean quietly, String modifierKey) {
        return entity.modifyParameter(param, amount, minMax, quietly, modifierKey);
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, boolean quietly) {
        return entity.modifyParameter(param, amount, minMax, quietly);
    }

    public boolean modifyParameter(PARAMETER param, String amountString, Integer minMax, boolean quietly) {
        return entity.modifyParameter(param, amountString, minMax, quietly);
    }

    public boolean modifyParameter(PARAMETER param, String amountString, Integer minMax, boolean quietly, String modifierKey) {
        return entity.modifyParameter(param, amountString, minMax, quietly, modifierKey);
    }

    public Map<PARAMETER, Map<String, Double>> getModifierMaps() {
        return entity.getModifierMaps();
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, String modifierKey) {
        return entity.modifyParameter(param, amount, minMax, modifierKey);
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax) {
        return entity.modifyParameter(param, amount, minMax);
    }

    public void modifyParameter(PARAMETER param, int amount, boolean base) {
        entity.modifyParameter(param, amount, base);
    }

    public void modifyParameter(PARAMETER param, int amount, boolean base, String modifierKey) {
        entity.modifyParameter(param, amount, base, modifierKey);
    }

    public boolean modifyParameter(PARAMETER param, int amount, String modifierKey) {
        return entity.modifyParameter(param, amount, modifierKey);
    }

    public boolean modifyParameter(PARAMETER param, int amount) {
        return entity.modifyParameter(param, amount);
    }

    public void decrementParam(PARAMETER param) {
        entity.decrementParam(param);
    }

    public int getContainerCount(PROPERTY p) {
        return entity.getContainerCount(p);
    }

    public void incrementParam(PARAMETER param) {
        entity.incrementParam(param);
    }

    public boolean multiplyParamByPercent(PARAMETER param, int perc, boolean base) {
        return entity.multiplyParamByPercent(param, perc, base);
    }

    public boolean modifyParamByPercent(PARAMETER[] params, int perc) {
        return entity.modifyParamByPercent(params, perc);
    }

    public boolean modifyParamByPercent(PARAMETER param, int perc) {
        return entity.modifyParamByPercent(param, perc);
    }

    public boolean modifyParamByPercent(PARAMETER param, int perc, boolean base) {
        return entity.modifyParamByPercent(param, perc, base);
    }

    public boolean firePropEvent(CONSTRUCTED_EVENT_TYPE EVENT_TYPE, String val) {
        return entity.firePropEvent(EVENT_TYPE, val);
    }

    public boolean fireParamEvent(PARAMETER param, String amount, CONSTRUCTED_EVENT_TYPE event_type) {
        return entity.fireParamEvent(param, amount, event_type);
    }

    public void resetParam(PARAMETER param) {
        entity.resetDynamicParam(param);
    }

    public void setParam(PARAMETER param, int i, boolean quietly, boolean base) {
        entity.setParam(param, i, quietly, base);
    }

    public void setParam(PARAMETER param, int i, boolean quietly) {
        entity.setParam(param, i, quietly);
    }

    public void setParamDouble(PARAMETER param, double i, boolean quietly) {
        entity.setParamDouble(param, i, quietly);
    }

    public void setParameter(PARAMETER param, int i) {
        entity.setParameter(param, i);
    }

    public void setParam(PARAMETER param, int i) {
        entity.setParam(param, i);
    }

    public void setParam(String param, int i) {
        entity.setParam(param, i);
    }

    public void setParamMax(PARAMETER p, int i) {
        entity.setParamMax(p, i);
    }

    public void setParamMin(PARAMETER p, int i) {
        entity.setParamMin(p, i);
    }

    public String getDisplayedName() {
        return entity.getDisplayedName();
    }

    public void modifyParameter(String param, String string) {
        entity.modifyParameter(param, string);
    }

    public void modifyParamByPercent(String param, String string) {
        entity.modifyParamByPercent(param, string);
    }

    public boolean setParam(PARAMETER param, String value, boolean quiety) {
        return entity.setParam(param, value, quiety);
    }

    public void resetPercentages() {
        entity.resetPercentages();
    }

    public void resetCurrentValues() {
        entity.resetCurrentValues();
    }

    public void resetCurrentValue(PARAMETER base_p) {
        entity.resetCurrentValue(base_p);
    }

    public void resetPercentage(PARAMETER p) {
        entity.resetPercentage(p);
    }

    public boolean setParam(PARAMETER param, String value) {
        return entity.setParam(param, value);
    }

    public void setProperty(PROPERTY name, String value, boolean base) {
        entity.setProperty(name, value, base);
    }

    public void setProperty(String prop, String value) {
        entity.setProperty(prop, value);
    }

    public void setProperty(PROPERTY prop, String value) {
        entity.setProperty(prop, value);
    }

    public void modifyProperty(MOD_PROP_TYPE p, PROPERTY prop, String value) {
        entity.modifyProperty(p, prop, value);
    }

    public void removeLastPartFromProperty(PROPERTY prop) {
        entity.removeLastPartFromProperty(prop);
    }

    public void removeFromProperty(PROPERTY prop, String value) {
        entity.removeFromProperty(prop, value);
    }

    public void appendProperty(PROPERTY prop, String value) {
        entity.appendProperty(prop, value);
    }

    public boolean addOrRemoveProperty(PROPERTY prop, String value) {
        return entity.addOrRemoveProperty(prop, value);
    }

    public boolean addProperty(PROPERTY prop, String value) {
        return entity.addProperty(prop, value);
    }

    public boolean addProperty(PROPERTY prop, List<String> values, boolean noDuplicates) {
        return entity.addProperty(prop, values, noDuplicates);
    }

    public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates) {
        return entity.addProperty(prop, value, noDuplicates);
    }

    public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates, boolean addInFront) {
        return entity.addProperty(prop, value, noDuplicates, addInFront);
    }

    public void putProperty(PROPERTY prop, String value) {
        entity.putProperty(prop, value);
    }

    public void putParameter(PARAMETER param, String value) {
        entity.putParameter(param, value);
    }

    public boolean isTypeLinked() {
        return entity.isTypeLinked();
    }

    public void addProperty(String prop, String value) {
        entity.addProperty(prop, value);
    }

    public boolean clearProperty(PROPERTY prop) {
        return entity.clearProperty(prop);
    }

    public boolean removeProperty(PROPERTY prop) {
        return entity.removeProperty(prop);
    }

    public boolean removeProperty(PROPERTY prop, String value) {
        return entity.removeProperty(prop, value);
    }

    public boolean removeProperty(PROPERTY prop, String value, boolean all) {
        return entity.removeProperty(prop, value, all);
    }

    public boolean removeMultiProp(String prop, String value, boolean all) {
        return entity.removeMultiProp(prop, value, all);
    }

    public String getSubGroupingKey() {
        return entity.getSubGroupingKey();
    }

    public boolean isSetThis() {
        return entity.isSetThis();
    }

    public void setValue(VALUE valName, String value) {
        entity.setValue(valName, value);
    }

    public void setValue(VALUE valName, String value, boolean base) {
        entity.setValue(valName, value, base);
    }

    public void setValue(String name, String value) {
        entity.setValue(name, value);
    }

    public void setValue(String name, String value, boolean base) {
        entity.setValue(name, value, base);
    }

    public void cloneMaps(Entity type) {
        entity.cloneMaps(type);
    }

    public void mergeValues(Entity type, VALUE... vals) {
        entity.mergeValues(type, vals);
    }

    public void addParam(PARAMETER parameter, String param, boolean base) {
        entity.addParam(parameter, param, base);
    }

    public void copyValues(Entity type, List<VALUE> list) {
        entity.copyValues(type, list);
    }

    public void copyValues(Entity type, VALUE... vals) {
        entity.copyValues(type, vals);
    }

    public void cloneMapsWithExceptions(Entity type, VALUE... exceptions) {
        entity.cloneMapsWithExceptions(type, exceptions);
    }

    public ParamMap cloneParamMap(Map<PARAMETER, String> map) {
        return entity.cloneParamMap(map);
    }

    public PropMap clonePropMap(Map<PROPERTY, String> map) {
        return entity.clonePropMap(map);
    }

    public Integer getId() {
        return entity.getId();
    }

    public void setId(Integer id) {
        entity.setId(id);
    }

    public String getNameIfKnown() {
        return entity.getNameIfKnown();
    }

    public String getName() {
        return entity.getName();
    }

    public void setName(String name) {
        entity.setName(name);
    }

    public String getUniqueId() {
        return entity.getUniqueId();
    }

    public boolean isConstructed() {
        return entity.isConstructed();
    }

    public void setConstructed(boolean b) {
        entity.setConstructed(b);
    }

    public boolean isDirty() {
        return entity.isDirty();
    }

    public void setDirty(boolean dirty) {
        entity.setDirty(dirty);
    }

    public boolean isPassivesReady() {
        return entity.isPassivesReady();
    }

    public void setPassivesReady(boolean passivesReady) {
        entity.setPassivesReady(passivesReady);
    }

    public boolean isActivesReady() {
        return entity.isActivesReady();
    }

    public void setActivesReady(boolean activesReady) {
        entity.setActivesReady(activesReady);
    }

    public boolean checkBool(DYNAMIC_BOOLS bool) {
        return entity.checkBool(bool);
    }

    public boolean checkBool(STD_BOOLS bool) {
        return entity.checkBool(bool);
    }


    public void removed() {
        entity.removed();
    }

    public boolean isInitialized() {
        return entity.isInitialized();
    }

    public void setInitialized(boolean initialized) {
        entity.setInitialized(initialized);
    }

    public String getNameOrId() {
        return entity.getNameOrId();
    }

    public String getRawValue(VALUE value) {
        return entity.getRawValue(value);
    }

    public XLinkedMap<VALUE, String> getRawValues() {
        return entity.getRawValues();
    }

    public void setRawValues(XLinkedMap<VALUE, String> rawValues) {
        entity.setRawValues(rawValues);
    }

    public boolean isDefaultValuesInitialized() {
        return entity.isDefaultValuesInitialized();
    }

    public void setDefaultValuesInitialized(boolean defaultValuesInitialized) {
        entity.setDefaultValuesInitialized(defaultValuesInitialized);
    }

    public void cloned() {
        entity.cloned();
    }

    public int getLevel() {
        return entity.getLevel();
    }

    public String getOriginalName() {
        return entity.getOriginalName();
    }

    public void setOriginalName(String originalName) {
        entity.setOriginalName(originalName);
    }

    public ImageIcon getCustomIcon() {
        return entity.getCustomIcon();
    }

    public void setCustomIcon(ImageIcon customIcon) {
        entity.setCustomIcon(customIcon);
    }

    public WORKSPACE_GROUP getWorkspaceGroup() {
        return entity.getWorkspaceGroup();
    }

    public void setWorkspaceGroup(WORKSPACE_GROUP value) {
        entity.setWorkspaceGroup(value);
    }

    public int getTypeId() {
        return entity.getTypeId();
    }

    public List<ObjType> getListFromProperty(OBJ_TYPE TYPE, PROPERTY prop) {
        return entity.getListFromProperty(TYPE, prop);
    }

    public void resetPropertyFromList(PROPERTY prop, List<? extends Entity> list) {
        entity.resetPropertyFromList(prop, list);
    }

    public HashMap<PROPERTY, Map<String, Boolean>> getPropCache() {
        return entity.getPropCache();
    }

    public int getSumOfParams(PARAMETER... params) {
        return entity.getSumOfParams(params);
    }
}
