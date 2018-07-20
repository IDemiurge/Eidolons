package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.DUNGEON_TEMPLATES;
import main.content.enums.DungeonEnums.SUBDUNGEON_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import main.content.enums.meta.MissionEnums.LOCATION_TYPE;

/**
 * Created by JustMe on 2/14/2018.
 *
 * to create boss level crypt for a cemetery...
 *
 *
 */
public class LevelData {
    SUBLEVEL_TYPE sublevelType; //determines room type too
    //per zone
    ROOM_TEMPLATE_GROUP[] templateGroup;
    DUNGEON_TEMPLATES[] templates;
    SUBDUNGEON_TYPE subdungeonType;
    LOCATION_TYPE locationType;


    int x;
    int y;
    int z; //the deeper, the <?>

    //for Fill - enemy type(s), ...

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

    public ROOM_TEMPLATE_GROUP[] getTemplateGroup() {
        return templateGroup;
    }

    public DUNGEON_TEMPLATES[] getTemplates() {
        return templates;
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

    public void setTemplateGroup(ROOM_TEMPLATE_GROUP[] templateGroup) {
        this.templateGroup = templateGroup;
    }

    public void setTemplates(DUNGEON_TEMPLATES[] templates) {
        this.templates = templates;
    }
}
