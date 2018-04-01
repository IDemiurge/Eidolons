package eidolons.client.options;

import main.system.data.DataUnit;

public class GameOptions extends DataUnit<GAME_OPTIONS> {
    // enums + collections
    // static {
    // for (GAME_OPTIONS o : GAME_OPTIONS.values()) {
    // o.setName(StringMaster.getFormattedEnumString(o.name()));
    // o.setToolTip(o.getName());
    // }
    // }

    public GameOptions(String data) {
        super(data);
        enumClass = GAME_OPTIONS.class;
    }

    public GameOptions() {
        enumClass = GAME_OPTIONS.class;
        for (GAME_OPTIONS enumConst : GAME_OPTIONS.values()) {
            setValue(enumConst.name(), enumConst.getDefaultValue());
        }
    }

    public enum GAME_WORLD {
        DARK, FOREST, MOUNTAINS, LIGHT, OUTWORLD, DUNGEON,

    }

    public enum MAP_TYPE {
        STANDARD

    }

    public interface GAME_OPTION {
        String getToolTip();

        String getDefaultValue();

        String getName();

        Object[] getOptions();
    }

}
