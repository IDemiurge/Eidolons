package main.game.battlefield;

import main.ability.UnitTrainingMaster;
import main.client.cc.logic.UnitLevelManager;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_GameData;
import main.game.logic.arena.UnitGroupMaster;
import main.game.logic.battle.player.DC_Player;
import main.game.logic.battle.player.Player;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.building.MapBlock;
import main.game.logic.generic.PartyManager;
import main.game.logic.generic.Positioner;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.test.TestMasterContent;
import main.test.frontend.FAST_DC;

import java.util.*;

/**
 * parses data strings for various purposes:
 * 1) create units on coordinates at any time (default game init method)
 * Syntax: "x-y=UnitName, ..."
 * Note: y==0 at the top
 * 2) Set party units' positions
 * 3) init direction/flip maps
 */
public class DC_ObjInitializer {

    public static final String OBJ_SEPARATOR = StringMaster.getAltSeparator();
    public static final String COORDINATES_OBJ_SEPARATOR = StringMaster.getAltPairSeparator();
    private static final String MULTI_DIRECTION_SUFFIX = "MULTI_DIRECTION-";
    private static DC_GameData data;
    private static boolean mapBlockMode;
    private static MapBlock block;
    private static Dungeon c_dungeon;
    private static Coordinates offset;


    public static void processUnitData(DC_GameData gameData, DC_Game game) {
        data = gameData;
        processUnitDataStringToMap(game.getPlayer(true), data.getPlayerUnitData(), game, true);
        processUnitDataStringToMap(game.getPlayer(false), data.getPlayer2UnitData(), game, true);
    }

    public static Map<Coordinates, ? extends Obj> initMapBlockObjects(Dungeon dungeon, MapBlock b,
                                                                      String textContent) {
        mapBlockMode = true;
        block = b;
        c_dungeon = dungeon;
        Map<Coordinates, ? extends Obj> map = new HashMap<>();
        try {
            map = processUnitDataStringToMap(Player.NEUTRAL, textContent, DC_Game.game);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mapBlockMode = false;
        }

        return map;
    }

    public static String getObjStringAlt(Obj obj) {
        return obj.getName() + StringMaster.wrapInParenthesis(obj.getCoordinates().toString());
    }

    public static String getObjString(ObjAtCoordinate obj) {
        return obj.getCoordinates().toString() + COORDINATES_OBJ_SEPARATOR
                + obj.getType().getName();
    }

    public static String getObjString(Obj obj) {
        return obj.getCoordinates().toString() + COORDINATES_OBJ_SEPARATOR + obj.getName();
    }

    public static Coordinates getCoordinatesFromObjStringAlt(String item) {
        return new Coordinates(true, VariableManager.getVar(item));
    }

    public static Coordinates getCoordinatesFromObjString(String item, boolean alt) {
        if (alt) {
            return getCoordinatesFromObjStringAlt(item);
        }
        try {
            return new Coordinates(true, item.split(COORDINATES_OBJ_SEPARATOR)[0]);
        } catch (Exception e) {
            return new Coordinates(0, 0);
        }
    }

    public static Coordinates getCoordinatesFromObjString(String item) {
        return getCoordinatesFromObjString(item, false);
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

    public static String getObj_CoordinateString(List<String> partyTypes,
                                                 List<Coordinates> coordinates) {
        int index = 0;
        String dataString = "";
        for (String item : partyTypes) {
            dataString += item + OBJ_SEPARATOR;
            dataString += coordinates.get(index).toString();
            dataString += ";";
            index++;
        }
        return dataString;
    }

    public static List<MicroObj> processUnitDataString(Player owner, String objData, DC_Game game) {
        if (StringMaster.isEmpty(objData)) {
            return new LinkedList<>();
        }
        Map<Coordinates, MicroObj> processUnitDataStringToMap = processUnitDataStringToMap(owner,
                objData, game);
        if (processUnitDataStringToMap == null) {
            return new LinkedList<>();
        }
        return new LinkedList<>(processUnitDataStringToMap.values());
    }

    public static List<Coordinates> getUnitDataStringCoordinateList(Player owner, String objData,
                                                                    DC_Game game) {
        return new LinkedList<>(processUnitDataStringToMap(owner, objData, game).keySet());
    }

    public static Map<Coordinates, MicroObj> processUnitDataStringToMap(Player owner,
                                                                        String objData, DC_Game game) {
        return processUnitDataStringToMap(owner, objData, game, false);
    }

    public static Map<Coordinates, MicroObj> processUnitDataStringToMap(Player owner,
                                                                        String objData, DC_Game game, boolean alt) {
        if (objData == null || objData.equals("")) {
            return null;
        }

        String[] items = objData.split(getObjSeparator(alt));
        Map<Coordinates, MicroObj> map = new HashMap<>();
        int i = 0;
        boolean first = true;
        boolean creeps = false;
        List<Coordinates> excludedCoordinates = new LinkedList<>();
        Boolean last = null;

        for (int indx = 0; indx < items.length; indx++) {
            String item = items[indx];
            boolean excludeCoordinate = false;
            if (mapBlockMode) {
                if (item.contains("%")) {
                    Integer chance = StringMaster.getInteger(VariableManager.getVarPart(item)
                     .replace("%", " "));
                    if (chance < 0) {
                        chance = -chance;
                        excludeCoordinate = true;
                    }
                    if (RandomWizard.chance(chance)) {
                        continue;
                    }
                }


            }
            try {
                String typeName = getNameFromObjString(item, alt);
                i++;
                if (i == items.length) {
                    last = true;
                }
                int level = 0;
                if (typeName.contains(UnitGroupMaster.TYPE_LEVEL_SEPARATOR)) {
                    level = StringMaster.getInteger(StringMaster.getLastPart(typeName,
                     UnitGroupMaster.TYPE_LEVEL_SEPARATOR));

                }
                ObjType type = DataManager.getType(typeName, C_OBJ_TYPE.BF_OBJ);
                if (level != 0) {
                    type = new UnitLevelManager().getLeveledType(type, level);
                }

                Coordinates c = null;
                if (!item.contains("null=")) {
                    try {
                        c = getCoordinatesFromObjString(item, alt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                int height = UnitGroupMaster.getGroupSizeY(owner);// UnitGroupMaster.getCurrentGroupHeight();
                int width = UnitGroupMaster.getGroupSizeX(owner);

                if (UnitGroupMaster.getFlip() == FLIP.CW90) {
                    int buffer = c.x;
                    c.setX(c.y);
                    c.setY(buffer);
                } else if (UnitGroupMaster.getFlip() == FLIP.CCW90) {
                    int buffer = width - c.x;
                    c.setX(height - c.y);
                    c.setY(buffer);

                }
                if (UnitGroupMaster.isMirror()) {
                    if (UnitGroupMaster.getFlip() == FLIP.CW90
                     || UnitGroupMaster.getFlip() == FLIP.CCW90) {
                        c.setX(width - c.x);
                    } else {

                        c.setY(height - c.y);
                    }
                    // TODO c.setX(width - c.x);

                }

                if (offset != null) {
                    c.setX(c.x + offset.x);
                    c.setY(c.y + offset.y);
                    if (!DC_Game.game.getRules().getStackingRule().canBeMovedOnto(type, c)) {
                        // TODO tactics?
                        c = Positioner.adjustCoordinate(type, c, FacingMaster.getRandomFacing()); // direction
                        // preference?
                    }

                }
                if (mapBlockMode) {
                    if (excludedCoordinates.contains(c)) {
                        continue;
                    }
                    if (type.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
                        if (!game.isSimulation()) {
                            game.getArenaManager().getSpawnManager().addDungeonEncounter(c_dungeon,
                             block, c, type);
                        }
                        continue;
                    }

                    if (!CoreEngine.isLevelEditor()
                     && C_OBJ_TYPE.UNITS_CHARS.equals(type.getOBJ_TYPE_ENUM())) {
                        owner = game.getPlayer(false);
                    } else {
                        owner = DC_Player.NEUTRAL;
                    }
                    if (C_OBJ_TYPE.ITEMS.equals(type.getOBJ_TYPE_ENUM())) {
                        // TODO 'treasure'?
                    }
                }
                if (excludeCoordinate) {
                    excludedCoordinates.add(c);
                }

                if (type == null) {
                    continue;
                }
                if (data != null) {
                    data.addType(type, owner.isMe());
                }

                if (game.isDebugMode()) {
                    if (owner.isMe()) {
                        try {
                            TestMasterContent.addTestItems(type, last);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                last = false;
                //todo optimize create unit func it too slow
                BattleFieldObject unit = (BattleFieldObject) game.createUnit(type, c, owner);


                if (!game.isOffline()) {
                    if (!game.isHost()) {
                        unit.setFacing(unit.getGame().getArenaManager().getSpawnManager()
                         .getMultiplayerFacingForUnit(unit));
                    }
                } else if (FAST_DC.isRunning()) {
                    if (!owner.isMe()) {
                        creeps = true;
                    } else {
                        try {
                            Unit hero = (Unit) unit;
                            if (first) {
                                PartyManager.newParty(hero);
                            } else {
                                PartyManager.addMember(hero);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                first = false;
                if (map.containsKey(c)) {
                    ZCoordinates coordinates = new ZCoordinates(c.x, c.y, new Random().nextInt());
                    map.put(coordinates, unit);
                } else {
                    map.put(c, unit);
                }

                if (!CoreEngine.isLevelEditor() && unit.getOBJ_TYPE_ENUM() != null) {
                    if (unit.getOBJ_TYPE_ENUM() == DC_TYPE.UNITS) {
                        // if (!owner.isMe() || game.isDebugMode()) TODO why
                        // not?
                        //todo optimize train func it too slow
                        UnitTrainingMaster.train((Unit) unit);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // if (creeps)
        // PartyManager.addCreepParty(DataManager.convertToTypeList(list));
        return map;
    }

    public static List<MicroObj> processObjData(Player player, String data) {
        return processObjData(player, data, null);
    }

    public static List<MicroObj> processObjData(Player player, String data,
                                                Coordinates offset_coordinate) {
        offset = offset_coordinate;
        List<MicroObj> list = null;
        try {
            list = processUnitDataString(player, data, DC_Game.game);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            offset = null;
        }
        return list;

    }

    private static String getObjSeparator(boolean alt) {
        if (alt) {
            return StringMaster.SEPARATOR;
        }
        return OBJ_SEPARATOR;
    }

    public static void initializePartyPositions(String playerPartyData,
                                                Collection<? extends Obj> units) {
        List<String> items = Arrays.asList(playerPartyData.split(OBJ_SEPARATOR));

        for (String item : items) {
            Coordinates c = getCoordinatesFromObjString(item);
            String typeName = item.split(COORDINATES_OBJ_SEPARATOR)[1];
            for (Obj unit : units) {
                if (unit.getType().getName().equals(typeName)) {

                    if (!DC_Game.game.getRules().getStackingRule().canBeMovedOnto(
                            (Unit) unit, c)) {
                        // TODO tactics?
                        c = Positioner.adjustCoordinate(c, FacingMaster.getRandomFacing()); // direction
                        // preference?
                    }
                    unit.setCoordinates(c);
                }
            }

        }

    }

    public static void initFlipMap(int z, Map<String, FLIP> flipMap) {
        if (flipMap != null) {
            for (String data : flipMap.keySet()) {
                Coordinates c = getCoordinatesFromObjString(data);
                FLIP d = flipMap.get(data);
                for (Unit obj : DC_Game.game.getObjectsOnCoordinate(z, c, null, false, false)) {
                    String name = getNameFromObjString(data);
                    if (name.contains(MULTI_DIRECTION_SUFFIX)) {
                        name = name.split(MULTI_DIRECTION_SUFFIX)[0];
                    }
                    if (!name.equals(obj.getName())) {
                        continue;
                    }
                    Map<Unit, FLIP> map = obj.getGame().getFlipMap().get(c);
                    if (map == null) {
                        map = new HashMap<>();
                        obj.getGame().getFlipMap().put(c, map);
                    }
                    map.put(obj, d);
                }
            }
        }
    }

    public static void initDirectionMap(int z, Map<String, DIRECTION> directionMap) {
        for (String data : directionMap.keySet()) {
            Coordinates c = getCoordinatesFromObjString(data);
            DIRECTION d = directionMap.get(data);
            for (Unit obj : DC_Game.game.getObjectsOnCoordinate(z, c, null, false, false)) {

                String name = getNameFromObjString(data);
                if (name.contains(MULTI_DIRECTION_SUFFIX)) {
                    name = name.split(MULTI_DIRECTION_SUFFIX)[0];
                }
                if (!name.equals(obj.getName())) {
                    continue;
                }
                Map<Unit, DIRECTION> map = obj.getGame().getDirectionMap().get(c);
                if (map == null) {
                    map = new HashMap<>();
                    obj.getGame().getDirectionMap().put(c, map);
                }
                map.put(obj, d);

            }
        }
    }

}
