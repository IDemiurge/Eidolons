package eidolons.game.battlecraft.logic.battlefield;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battle.encounter.Encounter;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.game.DC_Game;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import org.w3c.dom.Node;

import java.util.*;

/**
 * parses data strings for various purposes:
 * 1) create units on coordinates at any time (default game init method)
 * Syntax: "x-y=UnitName, ..."
 * Note: y==0 at the top
 * 2) Set party units' positions
 * 3) init direction/flip maps
 */
public class DC_ObjInitializer extends DungeonHandler<Location> {

    public static final String OBJ_SEPARATOR = StringMaster.getAltSeparator();
    public static final String COORDINATES_OBJ_SEPARATOR = StringMaster.getAltPairSeparator();
    private static final String MULTI_DIRECTION_SUFFIX = "MULTI_DIRECTION-";

    public DC_ObjInitializer(DungeonMaster  master) {
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

    public  DC_Obj initObject(Coordinates c, ObjType type, DC_Player owner, DC_Game game) {
        DC_Obj obj;
        if (owner == null) {
            if (type.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
                owner = DC_Player.NEUTRAL;
            } else {
            game.getPlayer(false);
                            }
        }
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
            obj = new Encounter(type, game, new Ref(), game.getPlayer(false), c);
        } else {

            if (type.getOBJ_TYPE_ENUM() == DC_TYPE.UNITS){
                obj =getSpawner().spawnUnit(type, c, owner, null, null );
            } else
                obj = game.createObject(type, c, owner);

        }


        return obj;
    }
    public   Map<Integer, BattleFieldObject> processObjects(
            Module module, Map<Integer, ObjType> idMap,
            Map<Integer, DC_Player> ownerMap, Node subNode) {
        //TODO   create player=> ids map and make multiple maps here!

        Map<Integer, BattleFieldObject> objIdMap = new LinkedHashMap<>();
        List<Encounter> encounters = new ArrayList<>();

        for (String substring : ContainerUtils.openContainer(
                subNode.getTextContent())) {
            Coordinates c = Coordinates.get(true, substring.split("=")[0]);
            List<String> ids;
            try {
                ids = ContainerUtils.openContainer(substring.split("=")[1], ",");
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                continue;
            }
            for (String idString : ids) {
                Integer id = Integer.valueOf(idString);
                ObjType type = idMap.get(id);
                if (type == null) {
                    main.system.auxiliary.log.LogMaster.log(1, "ERROR: Type not found - " + id);
                    continue;
                }
                DC_Player owner = ownerMap.get(id);
                DC_Obj value = initObject(c, type, owner, owner.getGame());

                if (value instanceof BattleFieldObject) {
                    objIdMap.put(id, (BattleFieldObject) value);
                } else if (value instanceof Encounter) {
                    encounters.add((Encounter) value);
                }
            }

//            if (level != 0) {
//                type = new UnitLevelManager().getLeveledType(type, level);
//            }

            module.setEncounters(encounters);
        }

        return objIdMap;
    }

    public static void initFlipMap(int z, Map<String, FLIP> flipMap) {
        if (flipMap != null) {
            for (String data : flipMap.keySet()) {
                Coordinates c = getCoordinatesFromObjString(data);
                FLIP d = flipMap.get(data);
                for (BattleFieldObject obj : DC_Game.game.getObjectsOnCoordinate(   c, false, false, false)) {
                    String name = getNameFromObjString(data);
                    if (name.contains(MULTI_DIRECTION_SUFFIX)) {
                        name = name.split(MULTI_DIRECTION_SUFFIX)[0];
                    }
                    if (!name.equals(obj.getName())) {
                        continue;
                    }
                    Map<BattleFieldObject, FLIP> map = obj.getGame().getFlipMap().get(c);
                    if (map == null) {
                        map = new HashMap<>();
                        obj.getGame().getFlipMap().put(c, map);
                    }
                    map.put(obj, d);
                }
            }
        }
    }

    public static Coordinates getCoordinatesFromObjString(String sub) {
        return  Coordinates.get(true, sub.split(StringMaster.getAltPairSeparator())[0]);
    }
    public static void initDirectionMap(int z, Map<String, DIRECTION> directionMap) {
        for (String data : directionMap.keySet()) {
            Coordinates c = getCoordinatesFromObjString(data);
            DIRECTION d = directionMap.get(data);
            for (BattleFieldObject obj : DC_Game.game.getObjectsOnCoordinate(  c, null, false, false)) {

                String name = getNameFromObjString(data);
                if (name.contains(MULTI_DIRECTION_SUFFIX)) {
                    name = name.split(MULTI_DIRECTION_SUFFIX)[0];
                }
                if (!name.equals(obj.getName())) {
                    continue;
                }
                Map<BattleFieldObject, DIRECTION> map = obj.getGame().getDirectionMap().get(c);
                if (map == null) {
                    map = new HashMap<>();
                    obj.getGame().getDirectionMap().put(c, map);

                }
                obj.setDirection(d);
                map.put(obj, d);

            }
        }
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
}
