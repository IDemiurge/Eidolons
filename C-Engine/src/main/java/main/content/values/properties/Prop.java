package main.content.values.properties;

public class Prop implements PROPERTY {
//Core Review - this is only for Mapping, I could find a way to use original VALUE's

    private final PROPERTY property;
    private boolean dynamic = false;

    public Prop(PROPERTY property) {
        this.property = property;
        // ContentManager.getPROP(string);
    }

    public String name() {
        return property.name();
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

    public String getDefaultValue() {
        return property.getDefaultValue();
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getShortName() {
        return property.getShortName();
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
