package main.game.logic.dungeon.generator;

import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.DUNGEON_TEMPLATES;
import main.game.logic.dungeon.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;

/**
 * Created by JustMe on 2/14/2018.
 */
public class LevelData {
    SUBLEVEL_TYPE sublevelType; //determines room type too
    //per zone
    ROOM_TEMPLATE_GROUP[] templateGroup;
    DUNGEON_TEMPLATES[] templates;

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
