package main.content.values.properties;

import main.content.Metainfo;
import main.content.OBJ_TYPE;

public class Prop implements PROPERTY {

    private PROPERTY property;
    private boolean dynamic = false;

    public Prop(PROPERTY property) {
        this.property = property;
        // ContentManager.getPROP(string);
    }

    public String name() {
        return property.name();
    }

    public boolean isHighPriority() {
        return property.isHighPriority();
    }

    public void setHighPriority(boolean highPriority) {
        property.setHighPriority(highPriority);
    }

    public boolean isWriteToType() {
        return property.isWriteToType();
    }

    public void setWriteToType(boolean writeToType) {
        property.setWriteToType(writeToType);
    }

    public boolean isContainer() {
        return property.isContainer();
    }

    public String getName() {
        return property.getName();
    }

    public String getFullName() {
        return property.getFullName();
    }

    public String getDescription() {
        return property.getDescription();
    }

    public String getEntityType() {
        return property.getEntityType();
    }

    public String[] getEntityTypes() {
        return property.getEntityTypes();
    }

    public Metainfo getMetainfo() {
        return property.getMetainfo();
    }

    public String getDefaultValue() {
        return property.getDefaultValue();
    }

    public boolean isLowPriority() {
        return property.isLowPriority();
    }

    public void setLowPriority(boolean lowPriority) {
        property.setLowPriority(lowPriority);
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public boolean isSuperLowPriority() {
        return property.isSuperLowPriority();
    }

    @Override
    public void setSuperLowPriority(boolean superLowPriority) {
        this.property.setSuperLowPriority(superLowPriority);
    }

    public String getShortName() {
        return property.getShortName();
    }

    public void addSpecialDefault(OBJ_TYPE type, Object value) {
        property.addSpecialDefault(type, value);
    }

    public Object getSpecialDefault(OBJ_TYPE type) {
        return property.getSpecialDefault(type);
    }

    @Override
    public INPUT_REQ getInputReq() {
        return null;
    }

}
