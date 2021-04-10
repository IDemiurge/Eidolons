package main.content.values.parameters;

import main.content.ContentValsManager;

public class Param implements PARAMETER {
    private final PARAMETER parameter;
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

    public String getDefaultValue() {
        return parameter.getDefaultValue();
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

    @Override
    public INPUT_REQ getInputReq() {
        return null;
    }

    @Override
    public void setDevOnly(boolean devOnly) {

    }

    @Override
    public boolean isDevOnly() {
        return false;
    }

}
