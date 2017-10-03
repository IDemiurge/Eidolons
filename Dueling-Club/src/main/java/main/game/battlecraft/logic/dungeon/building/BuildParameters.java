package main.game.battlecraft.logic.dungeon.building;

import main.game.battlecraft.logic.dungeon.building.BuildHelper.BUILD_PARAMS;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.DUNGEON_TEMPLATES;
import main.system.auxiliary.RandomWizard;
import main.system.net.data.DataUnit;

import java.util.Map;

/**
 * Created by JustMe on 10/3/2017.
 */
public class BuildParameters extends DataUnit<BUILD_PARAMS> {
    public int CORRIDOR_OFFSET_CHANCE = 35;
    public int CUL_DE_SACS = 1;
    public int WALL_WIDTH = 1;
    public int TURN_CHANCE = 33;
    public int FILL_PERCENTAGE = 80;
    public int PREFERRED_FILL_PERCENTAGE = 80;

    public BuildParameters() {
        this(false);
    }

    public BuildParameters(boolean empty) {
        // setValue(BUILD_PARAMS.MAIN_ROOMS, value)
        // DungeonBuilder.

        if (empty) {
            setValue(BUILD_PARAMS.FILLER_TYPE, "");
        }

        // setValue(BUILD_PARAMS.PREFERRED_FILL_PERCENTAGE, "" +
        // PREFERRED_FILL_PERCENTAGE);

    }

    public BuildParameters(String data) {
        this(new RandomWizard<BUILD_PARAMS>()
         .constructStringWeightMap(data, BUILD_PARAMS.class));
    }

    public BuildParameters(Map<BUILD_PARAMS, String> map) {
        getValues().clear();
        for (BUILD_PARAMS p : map.keySet()) {
            setValue(p, map.get(p));
        }
    }

    public BuildParameters init(DUNGEON_TEMPLATES templates) {
        for (BUILD_PARAMS val : BUILD_PARAMS.values()) {
            Object value = getValueForTemplate(templates, val);
            if (value == null) {
                continue;
            }
            setValue(val, value.toString());
        }
        return this;

    }

    private Object getValueForTemplate(DUNGEON_TEMPLATES templates, BUILD_PARAMS val) {
        switch (val) {
            case CORRIDOR_OFFSET_CHANCE:
                break;
            case CUL_DE_SACS:
                break;
            case TURN_CHANCE:
                break;
            case FILL_PERCENTAGE:
                break;
            case ADDITIONAL_ROOMS:
                break;
            case MAIN_ROOMS:
                break;
            case WIDTH_MOD:
                break;
            case HEIGHT_MOD:
                break;
            case SIZE_MOD:
                switch (templates) {
                    case GREAT_ROOM:
                        return 150;
                    case STAR:
                        break;
                    case RING:
                        break;
                    case CROSS:
                        break;
                    case LABYRINTH:
                        return 50;
                    case PROMENADE:
                        break;
                    case SERPENT:
                        break;
                    case CLASSIC:
                        break;
                    case PRISON_CELLS:
                        return 50;
                }
                break;
            case RANDOM_ROOMS:
                break;
            case CORRIDORS:
                break;
        }
        return getDefault(val);
    }

    private String getDefault(BUILD_PARAMS val) {
        return null ;
    }

    public BuildParameters init(boolean generation) {
        if (!generation)
            return this;
        setValue(BUILD_PARAMS.CORRIDOR_OFFSET_CHANCE, "" + CORRIDOR_OFFSET_CHANCE);
        setValue(BUILD_PARAMS.WALL_WIDTH, "" + WALL_WIDTH);
        setValue(BUILD_PARAMS.CORRIDOR_OFFSET_CHANCE, "" + CORRIDOR_OFFSET_CHANCE);
        setValue(BUILD_PARAMS.CUL_DE_SACS, "" + CUL_DE_SACS);
        setValue(BUILD_PARAMS.TURN_CHANCE, "" + TURN_CHANCE);
        setValue(BUILD_PARAMS.FILL_PERCENTAGE, "" + FILL_PERCENTAGE);
        setValue(BUILD_PARAMS.RANDOM_ROOMS, "def");
        setValue(BUILD_PARAMS.CORRIDORS, "def");
        setValue(BUILD_PARAMS.MAIN_ROOMS, "");
        setValue(BUILD_PARAMS.FILLER_TYPE, "Stone Wall");
        return this;
    }

    @Override
    public String getValue(BUILD_PARAMS t) {
        String value = super.getValue(t);
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    public String getValue(String name) {
        return super.getValue(name);
    }

    @Override
    public int getIntValue(String value) {
        return super.getIntValue(value);
    }

    public boolean isNoRandomRooms() {
        return getValue(BUILD_PARAMS.RANDOM_ROOMS).isEmpty();
    }

    public boolean isNoCorridors() {
        return getValue(BUILD_PARAMS.CORRIDORS).isEmpty();
    }

}
