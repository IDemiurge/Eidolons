package main.entity;

import main.ability.AbilityObj;
import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.entity.obj.ActiveObj;
import main.entity.type.ObjType;

import java.io.Serializable;
import java.util.List;

public interface OBJ extends Referred, Serializable, Runnable {
    String getOBJ_TYPE();

    OBJ_TYPE getOBJ_TYPE_ENUM();

    String getProp(String string);

    void setParam(PARAMETER param, int i);

    void toBase();

    Integer getId();

    void init();

    Integer getCounter(String value_ref);

    void removeCounter(String name);

    List<ActiveObj> getActives();

    List<AbilityObj> getPassives();

    void construct();

    boolean setCounter(String name, int defaultValue);

    boolean modifyCounter(String name, int defaultValue);

    Integer getIntParam(PARAMETER param, boolean base);

    void setProperty(PROPERTY name, String value);

    boolean modifyParameter(PARAMETER param, Number amount);

    boolean modifyParamByPercent(PARAMETER param, int perc, boolean base);

    boolean multiplyParamByPercent(PARAMETER param, int perc, boolean base);

    ObjType getType();

    String getProperty(PROPERTY prop, boolean base);

    String getName();

    boolean checkBool(STD_BOOLS bool);

    boolean checkSingleProp(PROPERTY PROP, String value);

    boolean checkProperty(PROPERTY PROP, String value);

    boolean modifyParameter(PARAMETER param, Number amount, Integer minmax);

    boolean isFull(PARAMETER p);

    String getParam(PARAMETER param);

    Integer getIntParam(PARAMETER param);

    boolean isMine();
}
