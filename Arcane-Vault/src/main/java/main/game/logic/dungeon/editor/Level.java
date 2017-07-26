package main.game.logic.dungeon.editor;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.enums.system.MetaEnums;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Converter;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.battlefield.map.DC_Map;
import main.game.battlecraft.logic.battlefield.map.DungeonMapGenerator;
import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.module.dungeoncrawl.dungeon.DungeonLevelMaster;
import main.game.module.dungeoncrawl.dungeon.Entrance;
import main.game.battlecraft.logic.dungeon.building.BuildHelper.BuildParameters;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder;
import main.game.battlecraft.logic.dungeon.building.DungeonPlan;
import main.game.battlecraft.logic.dungeon.building.MapBlock;
import main.game.battlecraft.logic.dungeon.building.MapZone;
import main.game.logic.dungeon.editor.gui.LE_MapViewComp;
import main.game.logic.dungeon.editor.logic.AiGroupData;
import main.game.module.dungeoncrawl.dungeon.minimap.MiniGrid;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Level {
    boolean initialized;
    // Dungeon!
    // private DequeImpl<DC_Obj> mapObjects;
    // private DequeImpl<DC_Obj> wallObjects;
    // DequeImpl<Wave> encounters;
    // map to Blocks/coordinates
    private Dungeon dungeon;
    private Mission mission;
    // private Set<Coordinates> coordinates;
    // private DequeImpl<DC_Cell> cells;
    // private Map<Coordinates, DC_Obj> topObjMap;
    private DC_Map map;
    private String name;
    private String path;
    private Map<ObjType, BattleFieldObject> objCache = new HashMap<>();
    private List<AiGroupData> aiGroups;
    private List<DC_Obj> wallObjects = new LinkedList<>();

    public Level(String baseDungeonType, Mission mission, String data) {
        this(baseDungeonType, mission, null, false);
    }

    public Level(Dungeon dungeon, Mission mission) {
        this.dungeon = dungeon;
        this.mission = mission;
        LevelEditor.getSimulation().getDungeonMaster().setDungeon(dungeon);
    }

    public Level(String baseDungeonType, Mission mission, String data, boolean empty) {
        this.mission = mission;
        LevelEditor.getMainPanel().setCurrentLevel(this);
        if (DataManager.getType(baseDungeonType, DC_TYPE.DUNGEONS) != null) {
            ObjType type = new ObjType(DataManager.getType(baseDungeonType, DC_TYPE.DUNGEONS));
            LevelEditor.getSimulation().addType(type);
            this.dungeon = new Dungeon(type);
            LevelEditor.getSimulation().getDungeonMaster().setDungeon(dungeon);
            int z = 0;
            if (LevelEditor.getCurrentLevel() != null) {
                z = LevelEditor.getCurrentLevel().getDungeon().getZ() - 1;
            }
            // DungeonLevelMaster.is
            dungeon.setZ(z);
        }
        if (data == null) {
            // if (true) ??
            BuildParameters params = LE_MapMaster.initBuildParams(empty, dungeon);
            setName(dungeon.getName());
            dungeon.generateSublevels(); // TODO ? ? ?
            map = new DungeonMapGenerator(params).generateMap(dungeon);

            getDungeon().setProperty(G_PROPS.WORKSPACE_GROUP, getDefaultWorkspaceGroup(), true);
        } else {
            DungeonPlan plan = null;
            try {
                // TODO BUILD PARAMS!
                plan = new DungeonBuilder().loadDungeonMap(data);
                this.dungeon = plan.getDungeon();
                LevelEditor.getSimulation().addType(dungeon.getType());
                LevelEditor.getSimulation().getDungeonMaster().setDungeon(dungeon);
                map = plan.getMap();
                dungeon.setPlan(plan);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // initObjects();
            // for (MapBlock b : plan.getBlocks())
            // getMapObjects().addAllCast(b.getObjects());
            // dungeon.setPlan(plan);
            // for (DC_Obj obj : getMapObjects()) {
            // obj.setZ(dungeon.getZ());
            // }
            // wallObjects = new DequeImpl<>();
            // getWallObjects().addAllCast(plan.getWallObjects());
            // for (DC_Obj obj : getWallObjects()) {
            // obj.setZ(dungeon.getZ());
            // }

            List<BattleFieldObject> fullObjectList = new LinkedList<>();
            for (MapBlock b : plan.getBlocks()) {
                LinkedList<Obj> objects = new LinkedList<>(b.getObjects());
                for (Obj obj : objects) {
                    fullObjectList.add((BattleFieldObject) obj);
                    // TODO of course - the issue was that I added an object to
                    // block too! ... init?
                    BattleFieldObject unit = (BattleFieldObject) obj;
                    unit.setZ(dungeon.getZ());
                    addObj(unit, true);
                }
            }
            for (MapZone zone : plan.getZones()) {
                ObjType type1 = DataManager.getType(zone.getFillerType(), DC_TYPE.BF_OBJ);
                if (type1 == null) {
                    continue;
                }
                List<Coordinates> list = zone.getCoordinates();
                for (MapBlock b : zone.getBlocks()) {
                    list.removeAll(b.getCoordinates());
                }
                LevelEditor.getObjMaster().addObj(type1, true,
                        list.toArray(new Coordinates[list.size()]));
            }

            for (Obj obj : plan.getWallObjects()) {
                BattleFieldObject unit = (BattleFieldObject) obj;
                fullObjectList.add(unit);
                unit.setZ(dungeon.getZ());
                addObj(unit, true);
            }
            if (plan.getDirectionMap() != null) {
                try {
                    DC_ObjInitializer.initDirectionMap(dungeon.getZ(), plan.getDirectionMap());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // for (Coordinates c : getCoordinates()){
            // //fill
            // }

        }
    }

    private String getDefaultWorkspaceGroup() {
        return MetaEnums.WORKSPACE_GROUP.IMPLEMENT.toString();
    }

    public void init() {
        // for (Coordinates c : getCoordinates()) {
        // DC_Cell cell = new DC_Cell(c.x, c.y, LevelEditor.getSimulation(), new
        // Ref(),
        // getDungeon());
        // addCell(cell);
        // } IN SIMULATION.getObj...()
        if (map != null) {
            for (Coordinates c : map.getMapObjects().keySet()) {

                if (map.getMapObjects().get(c) != null) {
                    LevelEditor.getObjMaster().addObj(map.getMapObjects().get(c), true, c);
                }
                // DC_HeroObj obj = new DC_HeroObj(map.getMapObjects().getOrCreate(c),
                // c.x, c.y,
                // DC_Player.NEUTRAL, LevelEditor.getSimulation(), new Ref());
                // getMapObjects().add(obj);
                // getTopObjMap().put(obj.getCoordinates(), obj);
            }
        }
        // initialized = true;
    }

    public void save() {
        // as *dungeon template* more likely, and dungeon types have a pool of
        // templates available...

        dungeon.setProperty(PROPS.DUNGEON_PLAN, getDungeon().getPlan().getStringData());
        // dungeon.setProperty(PROPS.MAP_OBJECTS, mapObjData); TODO custom type
        // values?
        dungeon.setProperty(PROPS.MAP_OBJECTS, getDungeon().getPlan().getStringData());

        getDungeon().getType().cloneMaps(getDungeon());

    }

    public Level getCopy() {
        // TODO use xml? no, it must be fast...
        Dungeon dungeon = new Dungeon(this.dungeon.getType());
        dungeon.setPlan(this.dungeon.getPlan().getCopy());
        Level copy = new Level(dungeon, mission);
        // copy.setMapObjects(mapObjects);

        // plan, blocks, zones
        return copy;
    }

    public void copyFrom(Level prev) {
        // TODO update grid and simulation!
        // replace the game-Simulation!!! Grid will reflect changes...
        // overlaying may be trouble
        // prev.getMapObjects()
        LE_Simulation c_game = (LE_Simulation) dungeon.getGame();
        LogMaster.log(1, c_game.toString());
        LinkedList<Unit> unitsCache = c_game.getUnitsCache();
        dungeon.setPlan(prev.getDungeon().getPlan().getCopy());
        LE_Simulation game = (LE_Simulation) prev.getDungeon().getGame();
        dungeon.setGame(game);
//        LogMaster.log(1, game.toString());
//        game.getUnitsCache(unitsCache);
        LogMaster.log(1, game.getUnitsCache().size() + " vs " + unitsCache.size());
    }

    public String getXml() {
        String xml = XML_Converter.openXmlFormatted("Level");

        xml += XML_Converter.openXmlFormatted("Custom Props");
        for (PROPERTY prop : dungeon.getPropMap().keySet()) {
            String value = dungeon.getProperty(prop);
            if (prop == G_PROPS.WORKSPACE_GROUP || prop == PROPS.ENEMY_SPAWN_COORDINATES
                    || prop == PROPS.PARTY_SPAWN_COORDINATES
                    || !value.equals(dungeon.getType().getType().getProperty(prop)))
            // dungeon.getType().getType() - original type
            {
                xml += XML_Converter.wrapLeaf(prop.getName(), value);
            }
        }
        xml += XML_Converter.closeXmlFormatted("Custom Props");
        xml += XML_Converter.openXmlFormatted("Custom Params");
        for (PARAMETER param : dungeon.getParamMap().keySet()) {
            String value = dungeon.getParam(param);
            if (!value.equals(dungeon.getType().getType().getParam(param))) {
                xml += XML_Converter.wrapLeaf(param.getName(), value);
            }
        }
        xml += XML_Converter.closeXmlFormatted("Custom Params");

        String wallObjData = getWallObjData();
        xml += XML_Converter.wrapLeaf(DungeonBuilder.WALL_OBJ_DATA_NODE, wallObjData);

        String facingMapData = getFacingMapData();
        if (!facingMapData.isEmpty()) {
            xml += XML_Converter.wrapLeaf(DungeonBuilder.DIRECTION_MAP_NODE, facingMapData);
        }

        String aiGroupData = getAiGroupData();
        if (!aiGroupData.isEmpty()) {
            xml += XML_Converter.wrapLeaf(DungeonBuilder.AI_GROUPS_NODE, aiGroupData);
        }

        xml += dungeon.getPlan().getXml();
        xml += XML_Converter.closeXmlFormatted("Level");
        return xml;
    }

    private String getAiGroupData() {
        String string = "";
        for (AiGroupData ai : getAiGroups()) {
            string += ai.toString() + StringMaster.AND_PROPERTY_SEPARATOR;
        }
        return string;
    }

    private String getFacingMapData() {
        String facingMapData = "";
        Map<Coordinates, List<BattleFieldObject>> multiMap = new HashMap<>();

        for (Coordinates c : getDirectionMap().keySet())

        {
            Map<Unit, DIRECTION> map = getDirectionMap().get(c);
            for (DC_Obj obj : map.keySet()) {
                if (obj instanceof BattleFieldObject) {
                    BattleFieldObject u = (BattleFieldObject) obj;
                    DIRECTION facing = map.get(u);
                    if (facing != null) {
                        u.setCoordinates(c);
                        String string = DC_ObjInitializer.getObjString(u);

                        List<BattleFieldObject> list = multiMap.get(c);
                        if (list == null) {
                            list = new LinkedList<>();
                            multiMap.put(c, list);
                        }

                        list.add(u);
                        if (list.size() > 1) {
                            string += list.size() + 1;
                        }

                        facingMapData += RandomWizard
                                .getWeightStringItem(string, facing.toString());

                    }
                }
            }
        }
        return facingMapData;
    }

    private String getWallObjData() {
        String wallObjData = "";
        for (DC_Obj obj : getWallObjects()) {
            String objString = DC_ObjInitializer.getObjString(obj);
            Integer chance = obj.getIntParam(G_PARAMS.CHANCE);
            if (chance > 0) {
                objString += StringMaster.wrapInParenthesis(chance + "%");
            }
            wallObjData += objString + DC_ObjInitializer.OBJ_SEPARATOR;
        }
        return wallObjData;
    }

    private List<DC_Obj> getWallObjects() {
        return wallObjects;
    }

    public void setMap(DC_Map map) {
        this.map = map;
    }

    public void removeObj(Coordinates... c) {
        removeObj(null, c);
    }

    public void removeObjects(List<DC_Obj> objects) {
        if (objects.isEmpty()) {
            return;
        }
        for (DC_Obj obj : objects) {
            removeObj(obj);
        }
        // LevelEditor.getMainPanel().refresh();
    }

    public void removeObj(String objNameFilter, Coordinates... c) {
        cache();
        List<DC_Obj> list = getObjects(objNameFilter, c);
        removeObjects(list);
    }

    private List<DC_Obj> getObjects(String objNameFilter, Coordinates... c) {

        List<DC_Obj> list = new LinkedList<>();
        for (Coordinates coordinates : c) {
            for (DC_Obj obj : getObjects(coordinates)) {
                if (objNameFilter != null) {
                    if (!StringMaster.compare(objNameFilter, obj.getName())) {
                        continue;
                    }
                }
                list.add(obj);
            }
        }
        return list;
    }

    private List<BattleFieldObject> getObjects(Coordinates coordinates) {
        return LevelEditor.getSimulation().getBfObjectsOnCoordinate(coordinates);
    }

    public void removeObj(DC_Obj obj) {
        Chronos.mark("removing " + obj);
        BattleFieldObject unit = null;
        if (obj instanceof BattleFieldObject) {
            unit = (BattleFieldObject) obj;
        }
        if (obj instanceof Entrance) {
            if (dungeon.getMainEntrance() != null) {
                dungeon.setMainEntrance(null);
                dungeon.getPlan().setEntranceLayout(null);
            } else if (dungeon.getMainExit() == null) {
                dungeon.setMainExit(null);
                dungeon.getPlan().setExitLayout(null);
            }
            dungeon.getEntrances().remove(obj);
        }
        // TODO getOrCreate Top object!
        // ++ ZOrder...
        Coordinates coordinates = obj.getCoordinates();
        LevelEditor.getSimulation().remove(obj);
        // TODO ???

        // obj = dungeon.getMinimap().getGrid().getTopObj(coordinates);
        // if (obj != null)
        // if (!unit.isOverlaying())TODO

        MapBlock b = LevelEditor.getMapMaster().getBlock();

        if (b == null) {
            b = getBlockForCoordinate(coordinates, false);
        }
        if (b != null) {
            boolean result = !b.removeObject(obj, coordinates);
            if (result) {
                if (unit.isLandscape()) {
                    b.addCoordinate(obj.getCoordinates());
                }
            }
        } else {
            b = LevelEditor.getMainPanel().getPlanPanel().getSelectedBlock();
            if (b == null) {
                b = getBlockForCoordinate(coordinates, true);
            } else if (!CoordinatesMaster.isAdjacent(b.getCoordinates(), coordinates)) {
                b = getBlockForCoordinate(coordinates, true);
            }

            if (b != null) {
                if (!unit.isOverlaying()) {
                    b.addCoordinate(obj.getCoordinates());
                }
            }

        }
        if (unit != null) {
            if (!unit.isOverlaying())
            // if (LevelEditor.isMinimapMode())
            {
                if (LE_MapViewComp.isMinimapMode()) {
                    LevelEditor.getMainPanel().getMiniGrid()
                            .refreshComp(null, obj.getCoordinates());
                } else {
                    LE_MapViewComp comp = LevelEditor.getMainPanel().getMapViewComp();
                    comp.getGrid().getCompForObject(obj).refresh();
                    comp.getGrid().refresh();
                    comp.getGrid().getCompForObject(obj).refresh();
                    comp.getGrid().getPanel().repaint();
                }
            }
        }
        // TODO ADD COORDINATE!
        Chronos.logTimeElapsedForMark("removing " + obj);

    }

    public MapBlock getBlockForCoordinate(Coordinates coordinates, boolean adjacent) {
        return getBlockForCoordinate(coordinates, adjacent, null);
    }

    public MapBlock getBlockForCoordinate(Coordinates coordinates, boolean adjacent,
                                          List<MapBlock> exceptions) {
        MapBlock b = LevelEditor.getMainPanel().getPlanPanel().getActiveBlock();
        if (adjacent) {
            if (b != null) {
                if (CoordinatesMaster.isAdjacent(b.getCoordinates(), coordinates)) {
                    return b;
                }
            }
        }
        for (MapBlock bl : dungeon.getPlan().getBlocks()) {
            if (exceptions != null) {
                if (exceptions.contains(bl)) {
                    continue;
                }
            }
            if (!adjacent) {
                if (bl.getCoordinates().contains(coordinates)) {
                    return bl;
                }
            } else if (CoordinatesMaster.isAdjacent(bl.getCoordinates(), coordinates)) {
                return bl;
            }
        }
        return null;
    }



    // TODO *added*
    public void addObj(BattleFieldObject obj, boolean stack) {
        Coordinates c = obj.getCoordinates();
        addObj(obj, c, stack);
    }

    public void addObj(BattleFieldObject obj, Coordinates c, boolean stack) {
        Chronos.mark("adding " + obj);
        obj.setZ(dungeon.getZ());
        if (stack) {
            if (obj.isLandscape()) {
                stack = false;
            }
        }
        // if (!obj.isOverlaying())
        // getTopObjMap().put(c, obj);

        if (obj instanceof Entrance) {
            if (dungeon.getMainEntrance() == null) {
                dungeon.setMainEntrance((Entrance) obj);
                dungeon.getPlan().setEntranceLayout(
                        DungeonLevelMaster.getLayout(dungeon.getPlan(), c));
                LogMaster.log(1, "Main Entrance: " + obj + "; initComps = "
                        + dungeon.getPlan().getEntranceLayout());
            } else if (dungeon.getMainExit() == null) {
                dungeon.setMainExit((Entrance) obj);
                dungeon.getPlan().setExitLayout(DungeonLevelMaster.getLayout(dungeon.getPlan(), c));
                LogMaster.log(1, "Main Exit: " + obj + "; initComps = "
                        + dungeon.getPlan().getExitLayout());
            }
            dungeon.getEntrances().add((Entrance) obj);

        }
        cache();
        // overwrite
        if (!obj.isOverlaying() && !stack && initialized) {
            // for (DC_Obj o : mapObjects)
            // if (o.getCoordinates().equals(obj.getCoordinates())) {
            List<DC_Obj> objects = getObjects(null, c);
            objects.remove(obj);
            removeObjects(objects);
        }

        MapBlock b = getBlockForCoordinate(c, false);
        if (b != null) {
            if (obj.isLandscape() &&

                    obj.getType().getName().equalsIgnoreCase(b.getZone().getFillerType())) {
                b.getCoordinates().remove(c);
            } else if (initialized) {
                b.addObject(obj, c);
            }
            // TODO
        } else {
            if (!obj.isLandscape()) {
                getWallObjects().add(obj);
            }
        }

        Chronos.logTimeElapsedForMark("adding " + obj);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    private void cache() {
        // TODO Auto-generated method stub

    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MiniGrid getGrid() {
        return dungeon.getMinimap().getGrid();
    }

    public List<AiGroupData> getAiGroups() {
        aiGroups = new LinkedList<>();
        return aiGroups;
    }

    public Mission getMission() {
        return mission;
    }

    public Map<Coordinates, Map<Unit, DIRECTION>> getDirectionMap() {
        return LevelEditor.getSimulation(this).getDirectionMap();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<ObjType, BattleFieldObject> getObjCache() {
        return objCache;
    }

    public List<MapBlock> getBlocks() {
        return getDungeon().getPlan().getBlocks();
    }

}
