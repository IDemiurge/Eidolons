package main.content.values.parameters;

import main.content.ContentValsManager;
import main.content.Metainfo;
import main.content.OBJ_TYPE;

public class Param implements PARAMETER {
    private PARAMETER parameter;
    private String name;

    public Param(PARAMETER parameter) {
        this.parameter = parameter;
        // ContentManager.getPARAM(string);
    }

    public Param(String parameter) {
        this(ContentValsManager.getPARAM(parameter));
    }

    public boolean isDynamic() {
        return parameter.isDynamic();
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        if (parameter == null) {
            return "";
        }
        return parameter.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return parameter.getFullName();
    }

    public String getDescription() {
        return parameter.getDescription();
    }

    public String getEntityType() {
        return parameter.getEntityType();
    }

    public String[] getEntityTypes() {
        return parameter.getEntityTypes();
    }

    public Metainfo getMetainfo() {
        return parameter.getMetainfo();
    }

    public String getDefaultValue() {
        return parameter.getDefaultValue();
    }

    public boolean isLowPriority() {
        return parameter.isLowPriority();
    }

    public void setLowPriority(boolean lowPriority) {
        parameter.setLowPriority(lowPriority);
    }

    @Override
    public boolean isSuperLowPriority() {
        return parameter.isSuperLowPriority();
    }

    @Override
    public void setSuperLowPriority(boolean superLowPriority) {
        this.parameter.setSuperLowPriority(superLowPriority);
    }

    public boolean isAttribute() {
        return parameter.isAttribute();
    }

    public boolean isMastery() {
        return parameter.isMastery();
    }

    public String getShortName() {
        return parameter.getShortName();
    }

    public boolean isHighPriority() {
        return parameter.isHighPriority();
    }

    public void setHighPriority(boolean highPriority) {
        parameter.setHighPriority(highPriority);
    }

    @Override
    public boolean isMod() {
        return parameter.isMod();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String name() {
        return parameter.name();
    }

    @Override
    public boolean isWriteToType() {
        return parameter.isWriteToType();
    }

    @Override
    public void setWriteToType(boolean writeToType) {

    }

    public void addSpecialDefault(OBJ_TYPE type, Object value) {
        parameter.addSpecialDefault(type, value);
    }

    public Object getSpecialDefault(OBJ_TYPE type) {
        return parameter.getSpecialDefault(type);
    }

    @Override
    public INPUT_REQ getInputReq() {
        return null;
    }

}
