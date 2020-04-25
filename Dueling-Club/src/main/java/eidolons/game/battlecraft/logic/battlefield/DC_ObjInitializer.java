package eidolons.game.battlecraft.logic.battlefield;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battle.encounter.Encounter;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.libgdx.bf.overlays.WallMap;
import eidolons.system.content.PlaceholderGenerator;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.ExceptionMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import org.w3c.dom.Node;

import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

/**
 * parses data strings for various purposes:
 * 1) create units on coordinates at any time (default game init method)
 * Syntax: "x-y=UnitName, ..."
 * Note: y==0 at the top
 * 2) Set party units' positions
 * 3) init direction/flip maps
 */
public class DC_ObjInitializer extends DungeonHandler {

    public static final String OBJ_SEPARATOR = StringMaster.getAltSeparator();
    public static final String COORDINATES_OBJ_SEPARATOR = StringMaster.getAltPairSeparator();
    private static final String MULTI_DIRECTION_SUFFIX = "MULTI_DIRECTION-";
    private ObjType borderType;
    private Map<Integer, DIRECTION> directionMap = new HashMap<>();

    public DC_ObjInitializer(DungeonMaster master) {
        super(master);
    }


    public static String getObjString(ObjAtCoordinate obj) {
        return obj.getCoordinates().toString() + COORDINATES_OBJ_SEPARATOR
                + obj.getType().getName();
    }


    public static String getNameFromObjStringAlt(String item) {
        return VariableManager.removeVarPart(item);
    }

    public static String getNameFromObjString(String item) {
        return getNameFromObjString(item, false);
    }

    public static String getNameFromObjString(String item, boolean alt) {
        if (alt) {
            return getNameFromObjStringAlt(item);
        }
        return item.split(COORDINATES_OBJ_SEPARATOR)[1];
    }

    public DC_Obj initObject(Module module, Coordinates c, ObjType type, DC_Player owner, DC_Game game) {
        DC_Obj obj;
        if (owner == null) {
            if (type.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
                owner = DC_Player.NEUTRAL;
            } else {
                owner = game.getPlayer(false);
            }
        }
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
            obj = new Encounter(type, game, new Ref(), game.getPlayer(false), c);
        } else {
            type = getPlaceholderResolver().resolve(module, type, c);
            if (type.getOBJ_TYPE_ENUM() == DC_TYPE.UNITS) {
                obj = getSpawner().spawnUnit(type, c, owner, null, null);
            } else
                obj = game.createObject(type, c, owner);

        }

        return obj;
    }

    public Map<Integer, BattleFieldObject> processObjects(
            Module module, Map<Integer, ObjType> idMap,
            Map<Integer, DC_Player> ownerMap, String data) {
        //TODO   create player=> ids map and make multiple maps here!

        Map<Integer, BattleFieldObject> objIdMap = new LinkedHashMap<>();
        List<Encounter> encounters = new ArrayList<>();

        for (String substring : ContainerUtils.openContainer(
                data)) {
            Coordinates c = Coordinates.get(true, substring.split("=")[0]);
            List<String> ids;
            try {
                ids = ContainerUtils.openContainer(substring.split("=")[1], ",");
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
                continue;
            }
            for (String idString : ids) {
                Integer id = Integer.valueOf(idString);
                ObjType type = idMap.get(id);
                if (type == null) {
                    LogMaster.log(1, "ERROR: Type not found - " + id);
                    continue;
                }
                DC_Player owner = ownerMap.get(id);

                DC_Obj obj = initObject(module, c, type, owner, DC_Game.game);

                if (obj instanceof BattleFieldObject) {
                    objIdMap.put(id, (BattleFieldObject) obj);
                    if (obj.isOverlaying()) {
                        DIRECTION d = directionMap.get(id);
                        ((BattleFieldObject) obj).setDirection(d);
                    }
                } else if (obj instanceof Encounter) {
                    encounters.add((Encounter) obj);
                    ((Encounter) obj).setOrigId(id);
                }
            }
            module.setEncounters(encounters);
        }

        return objIdMap;
    }

    public void processBorderObjects(
            Module module, Node subNode) {

        List<String> items = ContainerUtils.openContainer(
                subNode.getTextContent());

        log(LOG_CHANNEL.BUILDING,module.getName()+" has " + items.size() +
                " border objects...");
        for (String substring : items) {
            Coordinates c = Coordinates.get(true, substring);
            ObjType type = getBorderType();
            DC_Obj value = initObject(module, c, type, null, DC_Game.game);

            if (value instanceof BattleFieldObject) {
                ((BattleFieldObject) value).setModuleBorder(true);
            }
        }
        log(LOG_CHANNEL.BUILDING,module.getName()+": " + items.size() +
                " border objects done ");
    }

    public static Coordinates getCoordinatesFromObjString(String sub) {
        return Coordinates.get(true, sub.split(StringMaster.getAltPairSeparator())[0]);
    }

    public void initDirectionMap(String data) {
        directionMap = new HashMap<>();
        for (String substring : ContainerUtils.openContainer(data)) {
            Integer id = Integer.valueOf(substring.split("=")[0]);
            DIRECTION d = DIRECTION.valueOf(substring.split("=")[1]);
            directionMap.put(id, d);
        }
    }

    public Map<Integer, DIRECTION> getDirectionMap() {
        return directionMap;
    }

    public static String convertVarStringToObjCoordinates(String partyData) {
        String reformatted = "";
        for (String subString : ContainerUtils.open(partyData)) {
            Coordinates c = Coordinates.get(VariableManager.getVar(subString));
            if (c == null) {
                LogMaster.log(1, subString + " coordinate BLAST!!!");
            }
            subString = VariableManager.removeVarPart(subString);
            subString = c + COORDINATES_OBJ_SEPARATOR
                    + subString;
            reformatted += subString + OBJ_SEPARATOR;
        }

        return reformatted;
    }

    public static List<ObjAtCoordinate> createObjTypeMap(String textContent) {
        List<ObjAtCoordinate> list = new ArrayList<>();
        for (String substring : ContainerUtils.openContainer(textContent, OBJ_SEPARATOR)) {
            list.add(new ObjAtCoordinate(substring, C_OBJ_TYPE.BF_OBJ));
        }
        return list;
    }

    public ObjType getBorderType() {
        if (borderType == null) {
            borderType = DataManager.getType(
                    PlaceholderGenerator.getPlaceholderName(GeneratorEnums.ROOM_CELL.WALL) + WallMap.v(true));
        }
        return borderType;
    }

}
