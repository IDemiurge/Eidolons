package main.content.parameters;

import main.content.Metainfo;
import main.content.OBJ_TYPE;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;

public enum G_PARAMS implements PARAMETER {
    EMPTY_PARAMETER(null, true, "meta"),

    POS_X("Pos_x", true, "units"),
    POS_Y("Pos_y", true, "units"),
    POS_Z("Pos_z", true, "units"),

    C_DURATION("CURRENT DURATION", true, "spells", "buffs"),
    DURATION("Duration", false, "spells", "buffs", "actions", "items", "Track"),
    TURN_CREATED("Turn created", true, "all"),
    TURNS_IN_GAME("TURNS_IN_GAME", true, "all"),
    N_OF_CORPSES("N_OF_CORPSES", true, "terrain"),

    RADIUS("RADIUS", false, "spells", "actions", "items"),
    Z_LEVEL("Z", false, "dungeons"),
    CHANCE("Chance", true, "all"),;

    boolean writeToType;
    INPUT_REQ inputReq;
    private String entityType;
    private String[] entityTypes;
    private String defaultValue = "0";
    private boolean dynamic;
    private boolean lowPriority = false;
    private String name;
    private String shortName;
    private String fullName;
    private boolean superLowPriority;
    private boolean highPriority;
    private String description;
    private Map<OBJ_TYPE, Object> defaultValuesMap;

    G_PARAMS(String description, boolean dynamic, String... s) {
        this.setName(StringMaster.getWellFormattedString(name()));
        this.fullName = name();
        this.shortName = name;
        this.description = description;
        this.dynamic = dynamic;
        this.entityTypes = s;
        this.entityType = s[0];
    }

    @Override
    public INPUT_REQ getInputReq() {
        return inputReq;
    }

    public Map<OBJ_TYPE, Object> getDefaultValuesMap() {
        if (defaultValuesMap == null) {
            defaultValuesMap = new HashMap<OBJ_TYPE, Object>();
        }
        return defaultValuesMap;
    }

    @Override
    public void addSpecialDefault(OBJ_TYPE type, Object value) {
        getDefaultValuesMap().put(type, value);

    }

    @Override
    public Object getSpecialDefault(OBJ_TYPE type) {
        return getDefaultValuesMap().get(type);

    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDefaultValue() {
        return String.valueOf(defaultValue);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String[] getEntityTypes() {
        return entityTypes;
    }

    public void setEntityTypes(String[] entityTypes) {
        this.entityTypes = entityTypes;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Metainfo getMetainfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    @Override
    public boolean isAttribute() {
        return false;
    }

    @Override
    public boolean isSuperLowPriority() {
        return superLowPriority;
    }

    @Override
    public void setSuperLowPriority(boolean superLowPriority) {
        this.superLowPriority = superLowPriority;
    }

    @Override
    public boolean isMastery() {
        return false;
    }

    @Override
    public boolean isHighPriority() {
        return highPriority;
    }

    @Override
    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isWriteToType() {
        return writeToType;
    }

    public void setWriteToType(boolean writeToType) {
        this.writeToType = writeToType;
    }

    @Override
    public boolean isMod() {
        // TODO Auto-generated method stub
        return false;
    }
}
