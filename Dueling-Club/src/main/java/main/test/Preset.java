package main.test;

import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.net.data.DataUnit;
import main.test.Preset.PRESET_DATA;

public class Preset extends DataUnit<PRESET_DATA> {

    static PRESET_DATA[] displayed_values = {PRESET_DATA.PLAYER_PARTY, PRESET_DATA.ENEMY_PARTY,
            PRESET_DATA.DUNGEONS,};
    String path;
    private String name;

    public Preset(String name, String path) {
        this(name, path, null);
    }

    public Preset(String name, String path, String data) {
        this.name = name;
        this.path = path;
        if (data != null) {
            setData(data);
        }
    }

    @Override
    public Boolean getFormat() {
        return false;
    }

    @Override
    public String toString() {
        // for (PRESET_DATA val : displayed_values) {
        // }
        return getPath() + getName();
    }

    public String getXml() {

        return getData();
    }

    public ObjType getDungeonType() {
        String dungeonName = getFirstDungeonName();
        // GOTTA PRELOAD!
        // DataManager.getType(typeName, OBJ_TYPES.DUNGEONS);
        return null;
    }

    public String getEnemies() {
        return getValue(PRESET_DATA.ENEMIES);
    }

    public String getAllies() {
        return getValue(PRESET_DATA.PLAYER_UNITS);
    }

    public String getLevelPath() {
        return getFirstDungeonName();
    }

    public String getFirstDungeonName() {
        if (!StringMaster.isEmpty(getValue(PRESET_DATA.FIRST_DUNGEON))) {
            return getValue(PRESET_DATA.FIRST_DUNGEON);
        }
        return StringMaster.openContainer(getValue(PRESET_DATA.DUNGEONS)).get(0);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name2) {

        this.name = name2;
    }

    public enum PRESET_TYPE {
        TEST, FREE, SKIRMISH, SPECIAL, CAMPAIGN,
    }

    public enum PRESET_OPTION {
        DEBUG, ITEM_GENERATION_OFF, OMNIVISION,
    }

    public enum PRESET_DATA {
        PLAYER_PARTY, // CHAR IF NULL
        ENEMY_PARTY,
        DUNGEONS, // PATHS
        PRESET_OPTION_PARAMS,
        OPTIONS,
        ENEMIES,
        FIRST_DUNGEON,
        PLAYER_UNITS,
        PRESET_TYPE, //
        CONTENT_SCOPE, // CAMPAIGN, BASIC, ARCADE, FREE
        LAYER_FILTER, // BOSS/SPECIAL/CAMPAIGN/SCENARIO
        LEVEL_RANGE, // ANIM/AI DELAYS, INFO ICONS/TEXT,
        MODE, // FREE, TEST, ARCADE, SEQUENCE...

    }

}
