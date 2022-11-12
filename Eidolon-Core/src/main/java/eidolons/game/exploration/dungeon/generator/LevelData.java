package eidolons.game.exploration.dungeon.generator;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.exploration.dungeon.generator.LevelDataMaker.LEVEL_REQUIREMENTS;
import eidolons.game.exploration.dungeon.generator.test.LevelStats.LEVEL_GEN_FLAG;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE_GROUP;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.auxiliary.RandomWizard;
import main.system.data.DataUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/14/2018.
 * <p>
 * to create boss level crypt for a cemetery...
 */
public class LevelData extends DataUnit<GeneratorEnums.LEVEL_VALUES> {
    SUBLEVEL_TYPE sublevelType; //determines room type too
    //per zone
    GeneratorEnums.ROOM_TEMPLATE_GROUP[] templateGroups;

    DataUnit<LEVEL_GEN_FLAG> flags = LevelDataMaker.getDefaultLevelFlags();
    DataUnit<LEVEL_REQUIREMENTS> reqs;
    int x;
    int y;
    int z; //the deeper, the <?>
    private LOCATION_TYPE locationType;
    private Map<ROOM_TYPE, Integer> doorChanceMap = new HashMap<>();
    private boolean initializeRequired;

    //for Fill - enemy type(s), ...

    //so I will create these in a kind of options menu...
    public LevelData(String data) {
        super(data);
    }

    @Override
    public float getFloatValue(GeneratorEnums.LEVEL_VALUES value) {
        return super.getFloatValue(value);
    }

    public DataUnit<LEVEL_GEN_FLAG> getFlags() {
        return flags;
    }

    public DataUnit<LEVEL_REQUIREMENTS> getReqs() {
        return reqs;
    }

    public void setReqs(DataUnit<LEVEL_REQUIREMENTS> reqs) {
        this.reqs = reqs;
    }

    public int getRoomCoeF(ROOM_TYPE type) {
        return getIntValue(getROOM_COEF(type));
    }

    public static GeneratorEnums.LEVEL_VALUES getROOM_COEF(ROOM_TYPE type) {
        return GeneratorEnums.LEVEL_VALUES.valueOf(type.name() + "_COEF");
    }

    public SUBLEVEL_TYPE getSublevelType() {
        return sublevelType;
    }

    public void setSublevelType(SUBLEVEL_TYPE sublevelType) {
        this.sublevelType = sublevelType;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
    }

    public void setLocationType(LOCATION_TYPE locationType) {
        this.locationType = locationType;
    }

    public GeneratorEnums.ROOM_TEMPLATE_GROUP[] getTemplateGroups() {
        return templateGroups;
    }

    public void setTemplateGroups(GeneratorEnums.ROOM_TEMPLATE_GROUP[] templateGroups) {
        this.templateGroups = templateGroups;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public boolean isRandomRotation() {
        return RandomWizard.chance(getIntValue(GeneratorEnums.LEVEL_VALUES.
         RANDOM_ROTATION_CHANCE));
    }

    public boolean isMergeLinksAllowed() {
        return flags.getBooleanValue(LEVEL_GEN_FLAG.isMergeLinksAllowed);
    }

    public boolean isFinalizerOn() {
        return true;
    }

    public boolean isBuildFromExitAllowed() {
        return false;
    }

    public boolean isRandomizedSizeSort() {
        return false;
    }

    public boolean isShearWallsAllowed() {
        return false;
    }

    public boolean isShearLinkWallsAllowed() {
        return flags.getBooleanValue(LEVEL_GEN_FLAG.isShearLinkWallsAllowed);
    }

    public boolean isAlignExitsAllowed() {
        return false;
    }

    public int getDoorChance(ROOM_TYPE type) {
        Integer c = doorChanceMap.get(type);
        if (c == null) {
            c = getIntValue("DOOR_CHANCE_"
             + type.name())* getIntValue(GeneratorEnums.LEVEL_VALUES.DOOR_CHANCE_MOD) /100;
            doorChanceMap.put(type, c);
        }
        return c;
    }
    public int getFillPercentage(GeneratorEnums.ROOM_CELL type) {
         return getIntValue (getFillCoefValue(type));
    }

    public static GeneratorEnums.LEVEL_VALUES getFillCoefValue(GeneratorEnums.ROOM_CELL type) {
        return GeneratorEnums.LEVEL_VALUES.valueOf("FILL_" + type.name() + "_COEF");
    }

    public boolean isSurface() {
        return getBooleanValue(GeneratorEnums.LEVEL_VALUES.SURFACE);
    }
    public boolean isNatural() {
        return locationType.getGroup()== LOCATION_TYPE_GROUP.NATURAL||
         locationType.getGroup()== LOCATION_TYPE_GROUP.NATURAL_SURFACE;
    }

    public boolean isInitializeRequired() {
        return initializeRequired;
    }

    public void setInitializeRequired(boolean initializeRequired) {
        this.initializeRequired = initializeRequired;
    }

    public boolean isRemoveDeadendLinks() {
        return false;
    }

    public boolean isSubstituteRoomsAllowed() {
        return false;
    }

    public float getMinFloorPercentage(ROOM_TYPE roomType) {
        return 0.5f;
    }

    public boolean isLoopBackAllowed() {
        return true;
    }

    public boolean isPresetEntrancesAllowed() {
        return false;
    }


    //    public boolean isShearDisplacedOnly() {
    //        return getBooleanValue(LEVEL_VALUES.ShearDisplacedOnly);
    //    }
    //    public boolean isJoinAllowed() {
    //        return getBooleanValue(LEVEL_VALUES.JoinAllowed);
    //    }
}
