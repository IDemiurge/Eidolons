package eidolons.client.cc.logic;

import eidolons.client.cc.gui.MainPanel;
import main.ability.AbilityObj;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PROPERTY;
import main.entity.Ref;
import main.entity.type.ObjType;

import javax.swing.*;
import java.util.List;

public class HeroModel {
    // DC_HeroObj hero;
    private ObjType type;
    private MainPanel panel;

    public HeroModel(ObjType type) {
        this.type = type;
    }

    public String toString() {
        return getType().toString();
    }

    public void toBase() {
        getType().toBase(); // TODO ???
    }

    public String getParam(PARAMETER param, boolean base) {
        return getType().getParamRounded(param, base);
    }

    public String getParam(String p) {
        return getType().getParam(p);
    }

    public String getParam(PARAMETER param) {
        return getType().getParam(param);
    }

    public Integer getIntParam(PARAMETER param) {
        return getType().getIntParam(param);
    }

    public Integer getIntParam(PARAMETER param, boolean base) {
        return getType().getIntParam(param, base);
    }

    public String getProperty(String prop) {
        return getType().getProperty(prop);
    }

    public String getProp(String prop) {
        return getType().getProp(prop);
    }

    public String getProperty(PROPERTY prop) {
        return getType().getProperty(prop);
    }

    public boolean checkProperty(PROPERTY PROP, String value) {
        return getType().checkSingleProp(PROP, value);
    }

    public String getProperty(PROPERTY prop, boolean base) {
        return getType().getProperty(prop, base);
    }

    public Ref getRef() {
        return getType().getRef();
    }

    public String getValue(VALUE valName) {
        return getType().getValue(valName);
    }

    public String getValue(VALUE valName, boolean base) {
        return getType().getValue(valName, base);
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax) {
        return getType().modifyParameter(param, amount, minMax);
    }

    public boolean modifyParameter(PARAMETER param, int amount) {
        return getType().modifyParameter(param, amount);
    }

    public void setParameter(PARAMETER param, int i) {
        getType().setParam(param, i);
    }

    public boolean setParam(PARAMETER param, String value) {
        return getType().setParam(param, value);
    }

    public void setParamMap(ParamMap paramMap) {
        getType().setParamMap(paramMap);
    }

    public void setProperty(PROPERTY name, String value) {
        getType().setProperty(name, value);
    }

    public void addProperty(PROPERTY prop, String value) {
        getType().addProperty(prop, value);
    }

    public void addProp(String prop, String value) {
        getType().addProperty(prop, value);
    }

    public Integer getId() {
        return getType().getId();
    }

    public ImageIcon getIcon() {
        return getType().getIcon();
    }

    public String getName() {
        return getType().getName();
    }

    public String getOBJ_TYPE() {
        return getType().getOBJ_TYPE();
    }

    public OBJ_TYPE getOBJ_TYPE_ENUM() {
        return getType().getOBJ_TYPE_ENUM();
    }

    public OBJ_TYPE getObjType() {
        return getType().getOBJ_TYPE_ENUM();
    }

    public List<AbilityObj> getPassives() {
        return getType().getPassives();
    }

    public boolean isDirty() {
        return getType().isDirty();
    }

    public ObjType getType() {
        return type;
    }

    public void setType(ObjType type) {
        this.type = type;
    }

    public MainPanel getPanel() {
        return panel;
    }

    public void setPanel(MainPanel panel) {
        this.panel = panel;
    }

}
