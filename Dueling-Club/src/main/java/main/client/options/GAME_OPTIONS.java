package main.client.options;

import main.client.options.GameOptions.GAME_OPTION;
import main.client.options.GameOptions.GAME_WORLD;
import main.client.options.GameOptions.MAP_TYPE;
import main.content.CONTENT_CONSTS.RANK;
import main.game.core.game.DC_Game;
import main.system.auxiliary.EnumMaster;

public enum GAME_OPTIONS implements GAME_OPTION {
    // ROUND_NUMBER("Number of rounds", 1, 5),
    // MIN_RATING,
    // MAX_RATING,
    // MAX_PLAYER_NUMBER,
    // FRIENDS_ONLY(true),
    // PASSWORD(false),

    MAP_TYPE(MAP_TYPE.class),
    WORLD_TYPE(GAME_WORLD.class),
    GAME_MODE(DC_Game.GAME_TYPE.class),
    RES_LEVEL(RANK.class),;
    private String name;
    private String defaultValue = "";
    private String toolTip;
    private Class<?> enumClass;

    GAME_OPTIONS() {

    }

    GAME_OPTIONS(Class<?> enumClass) {
        this();
        this.enumClass = enumClass;

    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object[] getOptions() {
        return EnumMaster.getEnumConstantNames(enumClass).toArray();
    }
}
