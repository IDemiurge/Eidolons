package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.DUNGEON_TEMPLATES;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats.LEVEL_GEN_FLAG;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.auxiliary.RandomWizard;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 2/14/2018.
 * <p>
 * to create boss level crypt for a cemetery...
 */
public class LevelData extends DataUnit<LEVEL_VALUES> {
    SUBLEVEL_TYPE sublevelType; //determines room type too
    //per zone
    ROOM_TEMPLATE_GROUP[] templateGroups = new ROOM_TEMPLATE_GROUP[]{
     ROOM_TEMPLATE_GROUP.CRYPT
    };
    DUNGEON_TEMPLATES[] templates;

    DataUnit<LEVEL_GEN_FLAG> flags=LevelDataMaker.getDefaultLevelFlags();

    int x;
    int y;
    int z; //the deeper, the <?>
    private LOCATION_TYPE locationType;

    //for Fill - enemy type(s), ...

    //so I will create these in a kind of options menu...
    public LevelData(String data) {
        super(data);
    }

public LEVEL_VALUES getROOM_COEF(ROOM_TYPE type){
    return LEVEL_VALUES.valueOf(type.name() + "_COEF");
}
    public SUBLEVEL_TYPE getSublevelType() {
        return sublevelType;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
    }
    public void setLocationType(LOCATION_TYPE locationType) {
        this.locationType = locationType;
    }

    public ROOM_TEMPLATE_GROUP[] getTemplateGroups() {
        return templateGroups;
    }

    public DUNGEON_TEMPLATES[] getTemplates() {
        return templates;
    }

    public void setTemplates(DUNGEON_TEMPLATES[] templates) {
        this.templates = templates;
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


    public  boolean isRandomRotation() {
        return RandomWizard.chance(getIntValue(LEVEL_VALUES.
        RANDOM_ROTATION_CHANCE));
    }
    public boolean isMergeLinksAllowed() {
        return false;
    }

    public boolean isFinalizerOn() {
        return false;
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

    public void setSublevelType(SUBLEVEL_TYPE sublevelType) {
        this.sublevelType = sublevelType;
    }
    //    public boolean isShearDisplacedOnly() {
//        return getBooleanValue(LEVEL_VALUES.ShearDisplacedOnly);
//    }
//    public boolean isJoinAllowed() {
//        return getBooleanValue(LEVEL_VALUES.JoinAllowed);
//    }
}
