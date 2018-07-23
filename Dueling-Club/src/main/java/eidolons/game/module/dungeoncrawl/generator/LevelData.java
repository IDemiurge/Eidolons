package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.DUNGEON_TEMPLATES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import main.content.enums.DungeonEnums.SUBDUNGEON_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.content.enums.meta.MissionEnums.LOCATION_TYPE;
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
    SUBDUNGEON_TYPE subdungeonType;
    LOCATION_TYPE locationType;


    int x;
    int y;
    int z; //the deeper, the <?>

    //for Fill - enemy type(s), ...

    //so I will create these in a kind of options menu...
    public LevelData(String data) {
        super(data);
    }

    public LevelData(SUBLEVEL_TYPE sublevelType, int x, int y, int z) {
        this.sublevelType = sublevelType;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SUBLEVEL_TYPE getSublevelType() {
        return sublevelType;
    }

    public SUBDUNGEON_TYPE getSubdungeonType() {
        return subdungeonType;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
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
}
