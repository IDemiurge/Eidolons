package eidolons.game.exploration.dungeon.generator.tilemap;

import eidolons.game.exploration.dungeon.generator.LevelData;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import eidolons.game.exploration.dungeon.generator.model.LevelModel;
import eidolons.game.exploration.dungeon.generator.model.RoomModel;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static main.content.enums.DungeonEnums.DUNGEON_STYLE.*;

/**
 * Created by JustMe on 7/20/2018.
 */
public class TileConverter {

    public TileConverter(LevelModel model, LevelData metaData) {
    }

    public static DUNGEON_STYLE getStyle(LOCATION_TYPE subdungeonType) {
        return getStyle(subdungeonType, false);
    }

    public static DUNGEON_STYLE getStyle(LOCATION_TYPE subdungeonType, boolean alt) {

        switch (subdungeonType) {
            case CAVE:
                if (alt)
                    return RandomWizard.random() ? Cold : Brimstone;
                return Stony;
            case HIVE:
                if (alt)
                    return RandomWizard.random() ? Somber : Brimstone;
                return Grimy;
            case CASTLE:
                if (alt)
                    return RandomWizard.random() ? Knightly : DarkElegance;
                return Knightly;
            case SEWER:
                if (alt)
                    return RandomWizard.random() ?
                     Stony : Somber;
                return Grimy;
            case HELL:
                if (alt)
                    return PureEvil;
                return Brimstone;
            case ASTRAL:
                if (alt)
                    return PureEvil;
                return Arcane;
            case TOWER:
                return Arcane;
            case CEMETERY:
                if (alt)
                    return RandomWizard.random() ? DarkElegance : PureEvil;
                return Cold;
            case CRYPT:
                if (alt)
                    return RandomWizard.random() ? DarkElegance : PureEvil;
                return Somber;
            case BARROW:
                if (alt)
                    return RandomWizard.random() ? Brimstone : Cold;
                return Somber;
            case DUNGEON:
                if (alt)
                    return RandomWizard.random() ? DarkElegance : Cold;
                return Somber;
            case RUIN:
                if (alt)
                    return RandomWizard.random() ? DarkElegance : Somber;
                return Cold;
            case DEN:
                return Pagan;
            case HOUSE:
                if (alt)
                    return RandomWizard.random() ? DarkElegance : Somber;
                return Pagan;
            case CAMP:
                if (alt)
                    return RandomWizard.random() ? Stony : Somber;
                return Pagan;
            case TEMPLE:
                if (alt)
                    return RandomWizard.random() ? DarkElegance : Somber;
                return Holy;
        }
        return DarkElegance;
    }

    private String getWall(RoomModel room, int x, int y) {
        return "Stone Wall";
    }

    private Pair<String, OBJ_TYPE>[] getBfObjPair(String... names) {
        String string = ContainerUtils.join(";", names);
        return new Pair[]{
         new ImmutablePair(string, DC_TYPE.BF_OBJ)
        };
    }

    private String getDecorOverlaying(LevelBlock block, int x, int y) {
        return null;
    }

    private String getLightEmitterOverlaying(LevelBlock block, int x, int y) {
        return null;
    }

    private String getDoor(LevelBlock block, int x, int y) {
        return null;
    }

    private String getContainerObj(LevelBlock block, int x, int y) {
        return null;
    }

    private String getExitObj(LevelBlock block, int x, int y) {
        return null;
    }

    private String getLightEmitter(LevelBlock block, int x, int y) {
        DUNGEON_STYLE style = block.getStyle();
        WeightMap<String> map = new WeightMap<>();
        switch (style) {

            case Brimstone:
            case Grimy:
            case DarkElegance:
            case Stony:
                break;
            case Knightly:
                //               map.put(BF_OBJ_TYPES_LIGHT_EMITTERS.BRAZIER.getName(), 5);
                break;
        }
        return map.toString();
    }


}
