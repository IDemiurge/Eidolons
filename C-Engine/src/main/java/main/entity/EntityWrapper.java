package main.entity;

import com.badlogic.gdx.utils.ObjectMap;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.VALUE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.PropMap;
import main.entity.type.ObjType;
import main.game.core.game.Game;

import java.util.List;

/**
 * Created by JustMe on 5/10/2017.
 */
public interface EntityWrapper<E extends DataModel> {

    default String getToolTip() {
        return getEntity().getToolTip();
    }

    default String getDescription() {
        return getEntity().getDescription();
    }

    default Integer getCounter(String value_ref) {
        return getEntity().getCounter(value_ref);
    }

    default void setGroup(String group, boolean base) {
        getEntity().setGroup(group, base);
    }


    default boolean setCounter(String name, int newValue) {
        return getEntity().setCounter(name, newValue);
    }

    default boolean setCounter(String name, int newValue, boolean strict) {
        return getEntity().setCounter(name, newValue, strict);
    }

    default boolean modifyCounter(String name, int modValue) {
        return getEntity().modifyCounter(name, modValue);
    }

    default boolean modifyCounter(COUNTER counter, int modValue) {
        return getEntity().modifyCounter(counter, modValue);
    }

    default boolean modifyCounter(String name, int modValue, boolean strict) {
        return getEntity().modifyCounter(name, modValue, strict);
    }



    default String getParam(String p) {
        return getEntity().getParam(p);
    }

    default String getParam(PARAMETER param) {
        return getEntity().getParam(param);
    }


    default String getParamRounded(PARAMETER param, boolean base) {
        return getEntity().getParamRounded(param, base);
    }


    default Double getParamDouble(PARAMETER param) {
        return getEntity().getParamDouble(param);
    }


    default Double getParamDouble(PARAMETER param, boolean base) {
        return getEntity().getParamDouble(param, base);
    }

    default Game getGame() {
        return getEntity().getGame();
    }

    default Integer getIntParam(String param) {
        return getEntity().getIntParam(param);
    }


    default Integer getIntParam(PARAMETER param) {
        return getEntity().getIntParam(param);
    }


    default Integer getIntParam(PARAMETER param, boolean base) {
        return getEntity().getIntParam(param, base);
    }


    default ObjectMap<PARAMETER, Integer> getIntegerMap(boolean base) {
        return getEntity().getIntegerMap(base);
    }


    default ParamMap getParamMap() {
        return getEntity().getParamMap();
    }

    default void setParamMap(ParamMap paramMap) {
        getEntity().setParamMap(paramMap);
    }


    default void getBoolean(VALUE prop, Boolean b) {
        getEntity().getBoolean(prop, b);
    }


    default String getProperty(String prop) {
        return getEntity().getProperty(prop);
    }


    default String getProp(String prop) {
        return getEntity().getProp(prop);
    }

    default String getGroup() {
        return getEntity().getGroup();
    }

    default String getProperty(PROPERTY prop) {
        return getEntity().getProperty(prop);
    }


    default boolean checkParam(PARAMETER param) {
        return getEntity().checkParam(param);
    }


    default boolean checkParameter(PARAMETER param, int value) {
        return getEntity().checkParameter(param, value);
    }


    default boolean checkParam(PARAMETER param, String value) {
        return getEntity().checkParam(param, value);
    }


    default boolean checkProperty(PROPERTY p, String value) {
        return getEntity().checkProperty(p, value);
    }


    default ObjectMap<PROPERTY, ObjectMap<String, Boolean>> getPropCache(boolean base) {
        return getEntity().getPropCache(base);
    }

    default boolean checkProperty(PROPERTY p, String value, boolean base) {
        return getEntity().checkProperty(p, value, base);
    }

    default boolean checkSingleProp(String PROP, String value) {
        return getEntity().checkSingleProp(PROP, value);
    }

    default boolean checkSingleProp(PROPERTY PROP, String value) {
        return getEntity().checkSingleProp(PROP, value);
    }


    default boolean checkProperty(PROPERTY p) {
        return getEntity().checkProperty(p);
    }

    default boolean checkGroup(String string) {
        return getEntity().checkGroup(string);
    }

    default String getProperty(PROPERTY prop, boolean base) {
        return getEntity().getProperty(prop, base);
    }

    default PropMap getPropMap() {
        return getEntity().getPropMap();
    }

    default void setPropMap(PropMap propMap) {
        getEntity().setPropMap(propMap);
    }

    default Ref getRef() {
        return getEntity().getRef();
    }

    default void setRef(Ref ref) {
        getEntity().setRef(ref);
    }

    default ObjType getType() {
        return getEntity().getType();
    }

    default void setType(ObjType type) {
        getEntity().setType(type);
    }

    default String getValue(String valName) {
        return getEntity().getValue(valName);
    }

    default String getValue(VALUE valName) {
        return getEntity().getValue(valName);
    }

    default String getValue(VALUE val, boolean base) {
        return getEntity().getValue(val, base);
    }

    default boolean modifyParameter(PARAMETER param, int amount, Integer minMax, boolean quietly, String modifierKey) {
        return getEntity().modifyParameter(param, amount, minMax, quietly, modifierKey);
    }

    default boolean modifyParameter(PARAMETER param, int amount, Integer minMax, boolean quietly) {
        return getEntity().modifyParameter(param, amount, minMax, quietly);
    }

    default boolean modifyParameter(PARAMETER param, String amountString, Integer minMax, boolean quietly) {
        return getEntity().modifyParameter(param, amountString, minMax, quietly);
    }

    default boolean modifyParameter(PARAMETER param, String amountString, Integer minMax, boolean quietly, String modifierKey) {
        return getEntity().modifyParameter(param, amountString, minMax, quietly, modifierKey);
    }

    default ObjectMap<PARAMETER, ObjectMap<String, Double>> getModifierMaps() {
        return getEntity().getModifierMaps();
    }

    default boolean modifyParameter(PARAMETER param, int amount, Integer minMax, String modifierKey) {
        return getEntity().modifyParameter(param, amount, minMax, modifierKey);
    }

    default boolean modifyParameter(PARAMETER param, int amount, Integer minMax) {
        return getEntity().modifyParameter(param, amount, minMax);
    }

    default void modifyParameter(PARAMETER param, int amount, boolean base) {
        getEntity().modifyParameter(param, amount, base);
    }

    default void modifyParameter(PARAMETER param, int amount, boolean base, String modifierKey) {
        getEntity().modifyParameter(param, amount, base, modifierKey);
    }

    default boolean modifyParameter(PARAMETER param, int amount, String modifierKey) {
        return getEntity().modifyParameter(param, amount, modifierKey);
    }

    default boolean modifyParameter(PARAMETER param, int amount) {
        return getEntity().modifyParameter(param, amount);
    }

    default boolean multiplyParamByPercent(PARAMETER param, int perc, boolean base) {
        return getEntity().multiplyParamByPercent(param, perc, base);
    }

    default boolean modifyParamByPercent(PARAMETER[] params, int perc) {
        return getEntity().modifyParamByPercent(params, perc);
    }

    default boolean modifyParamByPercent(PARAMETER param, int perc) {
        return getEntity().modifyParamByPercent(param, perc);
    }

    default boolean modifyParamByPercent(PARAMETER param, int perc, boolean base) {
        return getEntity().modifyParamByPercent(param, perc, base);
    }

    default void setParam(PARAMETER param, int i, boolean quietly, boolean base) {
        getEntity().setParam(param, i, quietly, base);
    }

    default void setParam(PARAMETER param, int i, boolean quietly) {
        getEntity().setParam(param, i, quietly);
    }

    default void setParamDouble(PARAMETER param, double i, boolean quietly) {
        getEntity().setParamDouble(param, i, quietly);
    }

    default void setParameter(PARAMETER param, int i) {
        getEntity().setParameter(param, i);
    }

    default void setParam(PARAMETER param, int i) {
        getEntity().setParam(param, i);
    }

    default void setParam(String param, int i) {
        getEntity().setParam(param, i);
    }


    default String getDisplayedName() {
        return getEntity().getDisplayedName();
    }

    default void modifyParameter(String param, String string) {
        getEntity().modifyParameter(param, string);
    }

    default void modifyParamByPercent(String param, String string) {
        getEntity().modifyParamByPercent(param, string);
    }

    default boolean setParam(PARAMETER param, String value, boolean quiety) {
        return getEntity().setParam(param, value, quiety);
    }

    default void resetPercentages() {
        getEntity().resetPercentages();
    }

    default boolean setParam(PARAMETER param, String value) {
        return getEntity().setParam(param, value);
    }

    default void setProperty(PROPERTY name, String value, boolean base) {
        getEntity().setProperty(name, value, base);
    }

    default void setProperty(String prop, String value) {
        getEntity().setProperty(prop, value);
    }

    default void setProperty(PROPERTY prop, String value) {
        getEntity().setProperty(prop, value);
    }


    default boolean addProperty(PROPERTY prop, String value) {
        return getEntity().addProperty(prop, value);
    }

    default boolean addProperty(PROPERTY prop, List<String> values, boolean noDuplicates) {
        return getEntity().addProperty(prop, values, noDuplicates);
    }

    default boolean addProperty(PROPERTY prop, String value, boolean noDuplicates) {
        return getEntity().addProperty(prop, value, noDuplicates);
    }

    default boolean addProperty(PROPERTY prop, String value, boolean noDuplicates, boolean addInFront) {
        return getEntity().addProperty(prop, value, noDuplicates, addInFront);
    }

    default void addProperty(String prop, String value) {
        getEntity().addProperty(prop, value);
    }

    default boolean removeProperty(PROPERTY prop) {
        return getEntity().removeProperty(prop);
    }

    default boolean removeProperty(PROPERTY prop, String value) {
        return getEntity().removeProperty(prop, value);
    }

    default boolean removeProperty(PROPERTY prop, String value, boolean all) {
        return getEntity().removeProperty(prop, value, all);
    }

    default boolean removeMultiProp(String prop, String value, boolean all) {
        return getEntity().removeMultiProp(prop, value, all);
    }

    default String getSubGroupingKey() {
        return getEntity().getSubGroupingKey();
    }

    default boolean isSetThis() {
        return getEntity().isSetThis();
    }

    default void setValue(VALUE valName, String value) {
        getEntity().setValue(valName, value);
    }

    default void setValue(VALUE valName, String value, boolean base) {
        getEntity().setValue(valName, value, base);
    }

    default void setValue(String name, String value) {
        getEntity().setValue(name, value);
    }

    default void setValue(String name, String value, boolean base) {
        getEntity().setValue(name, value, base);
    }

    default void cloneMaps(Entity type) {
        getEntity().cloneMaps(type);
    }

    default void addParam(PARAMETER parameter, String param, boolean base) {
        getEntity().addParam(parameter, param, base);
    }


    default int getId() {
        return getEntity().getId();
    }

    default void setId(Integer id) {
        getEntity().setId(id);
    }

    default String getNameIfKnown() {
        return getEntity().getNameIfKnown();
    }

    default String getName() {
        return getEntity().getName();
    }

    default void setName(String name) {
        getEntity().setName(name);
    }

    default boolean isConstructed() {
        return getEntity().isConstructed();
    }

    default void setConstructed(boolean b) {
        getEntity().setConstructed(b);
    }

    default boolean isDirty() {
        return getEntity().isDirty();
    }

    default void setDirty(boolean dirty) {
        getEntity().setDirty(dirty);
    }

    default boolean checkBool(DYNAMIC_BOOLS bool) {
        return getEntity().checkBool(bool);
    }

    default boolean checkBool(STD_BOOLS bool) {
        return getEntity().checkBool(bool);
    }


    default void removed() {
        getEntity().removed();
    }

    default boolean isInitialized() {
        return getEntity().isInitialized();
    }

    default void setInitialized(boolean initialized) {
        getEntity().setInitialized(initialized);
    }

    default int getLevel() {
        return getEntity().getLevel();
    }


   E getEntity();

}
