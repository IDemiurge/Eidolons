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
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.universal.DungeonBuilder;
import main.game.battlecraft.logic.dungeon.universal.DungeonWrapper;
import main.game.battlecraft.logic.dungeon.location.Location;
import main.game.battlecraft.logic.dungeon.location.LocationBuilder;
import main.game.battlecraft.logic.dungeon.location.LocationMaster;
import main.game.battlecraft.logic.dungeon.location.building.BuildHelper.BuildParameters;
import main.game.battlecraft.logic.dungeon.location.building.*;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.logic.dungeon.editor.gui.LE_MapViewComp;
import main.game.logic.dungeon.editor.logic.AiGroupData;
import main.game.module.dungeoncrawl.dungeon.DungeonLevelMaster;
import main.game.module.dungeoncrawl.dungeon.Entrance;
import main.game.module.dungeoncrawl.dungeon.minimap.MiniGrid;
import main.game.module.dungeoncrawl.dungeon.minimap.Minimap;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Level extends DungeonWrapper<Location> {

    boolean initialized;
    private Location location;
    private Minimap minimap;
    private Mission mission;
    private String name;
    private String path;
    private Map<ObjType, Unit> objCache = new HashMap<>();
    private List<AiGroupData> aiGroups;
    private List<DC_Obj> wallObjects = new LinkedList<>();

    public Level(String baseDungeonType, Mission mission, String data) {
        this(baseDungeonType, mission, null, false);
    }

    public Level(Dungeon dungeon, Mission mission) {
        super(dungeon , null );
        setDungeonMaster( LevelEditor.getSimulation().getDungeonMaster());
        this.mission = mission;
    }
    public Level(String baseDungeonType, Mission mission, String data, boolean empty) {
        super(null, null);
        setDungeonMaster(LevelEditor.getSimulation().getDungeonMaster());
        this.mission = mission;
        LevelEditor.getMainPanel().setCurrentLevel(this);
        if (DataManager.getType(baseDungeonType, DC_TYPE.DUNGEONS) != null) {
            ObjType type = new ObjType(DataManager.getType(baseDungeonType, DC_TYPE.DUNGEONS));
            LevelEditor.getSimulation().addType(type);
//            new Dungeon(type);
//            LevelEditor.getSimulation().getDungeonMaster().setDungeon(dungeon);
            int z = 0;
            if (LevelEditor.getCurrentLevel() != null) {
                z = LevelEditor.getCurrentLevel().getLocation().getZ() - 1;
            }
            // DungeonLevelMaster.is
            location.setZ(z);
        }
        if (data == null) {
            // if (true) ??
            BuildParameters params = LE_MapMaster.initBuildParams(empty, getLocation());
            setName(location.getName());
//            dungeon.generateSublevels(); // TODO ? ? ?

            getLocation().setProperty(G_PROPS.WORKSPACE_GROUP, getDefaultWorkspaceGroup(), true);
        } else {
            DungeonPlan plan = null;
            try {
                // TODO BUILD PARAMS!
                plan = new LocationBuilder().loadDungeonMap(data);
                this.dungeon = plan.getDungeon();
                LevelEditor.getSimulation().addType(location.getType());
//                LevelEditor.getSimulation().getDungeonMaster().setDungeon(dungeon);
                master.getDungeonWrapper().setPlan(plan);
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

            List<Unit> fullObjectList = new LinkedList<>();
            for (MapBlock b : plan.getBlocks()) {
                LinkedList<Obj> objects = new LinkedList<>(b.getObjects());
                for (Obj obj : objects) {
                    fullObjectList.add((Unit) obj);
                    // TODO of course - the issue was that I added an object to
                    // block too! ... init?
                    Unit unit = (Unit) obj;
                    unit.setZ(location.getZ());
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
                Unit unit = (Unit) obj;
                fullObjectList.add(unit);
                unit.setZ(location.getZ());
                addObj(unit, true);
            }
            if (plan.getDirectionMap() != null) {
                try {
                    DC_ObjInitializer.initDirectionMap(location.getZ(), plan.getDirectionMap());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // for (Coordinates c : getCoordinates()){
            // //fill
            // }

        }
    }

    public void setDungeonMaster(LocationMaster master) {
        this.master = master;
        this.location = this.master.getDungeonWrapper();
        setDungeon(master.getInitializer().initDungeon().getDungeon());
    }

    public void setDungeon(Dungeon d) {
        this.dungeon = d;
        this.minimap = new Minimap(true, dungeon);
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
        // initialized = true;
    }

    public void save() {
        // as *dungeon template* more likely, and dungeon types have a pool of
        // templates available...

        location.setProperty(PROPS.DUNGEON_PLAN, getLocation().getPlan().getStringData());
        // dungeon.setProperty(PROPS.MAP_OBJECTS, mapObjData); TODO custom type
        // values?
        location.setProperty(PROPS.MAP_OBJECTS, getLocation().getPlan().getStringData());

        getLocation().getType().cloneMaps(getDungeon());

    }

    public Level getCopy() {
        // TODO use xml? no, it must be fast...
        Dungeon dungeon = new Dungeon(this.location.getType());
        location.setPlan(this.location.getPlan().getCopy());
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
        LE_Simulation c_game = (LE_Simulation) location.getGame();
        LogMaster.log(1, c_game.toString());
        LinkedList<Unit> unitsCache = c_game.getUnitsCache();
        location.setPlan(prev.getLocation().getPlan().getCopy());
        LE_Simulation game = (LE_Simulation) prev.getLocation().getGame();
        location.setGame(game);
        LogMaster.log(1, game.toString());
        game.setUnits(unitsCache);
        LogMaster.log(1, game.getUnits().size() + " vs " + unitsCache.size());
    }

    public String getXml() {
        String xml = XML_Converter.openXmlFormatted("Level");

        xml += XML_Converter.openXmlFormatted("Custom Props");
        for (PROPERTY prop : location.getPropMap().keySet()) {
            String value = location.getProperty(prop);
            if (prop == G_PROPS.WORKSPACE_GROUP || prop == PROPS.ENEMY_SPAWN_COORDINATES
                    || prop == PROPS.PARTY_SPAWN_COORDINATES
                    || !value.equals(location.getType().getType().getProperty(prop)))
            // dungeon.getType().getType() - original type
            {
                xml += XML_Converter.wrapLeaf(prop.getName(), value);
            }
        }
        xml += XML_Converter.closeXmlFormatted("Custom Props");
        xml += XML_Converter.openXmlFormatted("Custom Params");
        for (PARAMETER param : location.getParamMap().keySet()) {
            String value = location.getParam(param);
            if (!value.equals(location.getType().getType().getParam(param))) {
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
            xml += XML_Converter.wrapLeaf(LocationBuilder.AI_GROUPS_NODE, aiGroupData);
        }

        xml += location.getPlan().getXml();
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
        Map<Coordinates, List<Unit>> multiMap = new HashMap<>();

        for (Coordinates c : getDirectionMap().keySet())

        {
            Map<Unit, DIRECTION> map = getDirectionMap().get(c);
            for (DC_Obj obj : map.keySet()) {
                if (obj instanceof Unit) {
                    Unit u = (Unit) obj;
                    DIRECTION facing = map.get(u);
                    if (facing != null) {
                        u.setCoordinates(c);
                        String string = DC_ObjInitializer.getObjString(u);

                        List<Unit> list = multiMap.get(c);
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

    private List<Unit> getObjects(Coordinates coordinates) {
        return LevelEditor.getSimulation().getObjectsOnCoordinate(coordinates);
    }

    public void removeObj(DC_Obj obj) {
        Chronos.mark("removing " + obj);
        Unit unit = null;
        if (obj instanceof Unit) {
            unit = (Unit) obj;
        }
        if (obj instanceof Entrance) {
            if (location.getMainEntrance() != null) {
                location.setMainEntrance(null);
                location.getPlan().setEntranceLayout(null);
            } else if (location.getMainExit() == null) {
                location.setMainExit(null);
                location.getPlan().setExitLayout(null);
            }
            location.getEntrances().remove(obj);
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
        for (MapBlock bl : location.getPlan().getBlocks()) {
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

    public void stackObj(Unit obj) {
        addObj(obj, true);
    }

    public void addObj(Unit obj) {
        addObj(obj, false);
    }

    // TODO *added*
    public void addObj(Unit obj, boolean stack) {
        Coordinates c = obj.getCoordinates();
        addObj(obj, c, stack);
    }

    public void addObj(Unit obj, Coordinates c, boolean stack) {
        Chronos.mark("adding " + obj);
        obj.setZ(location.getZ());
        if (stack) {
            if (obj.isLandscape()) {
                stack = false;
            }
        }
        // if (!obj.isOverlaying())
        // getTopObjMap().put(c, obj);

        if (obj instanceof Entrance) {
            if (location.getMainEntrance() == null) {
                location.setMainEntrance((Entrance) obj);
                location.getPlan().setEntranceLayout(
                        DungeonLevelMaster.getLayout(location.getPlan(), c));
                LogMaster.log(1, "Main Entrance: " + obj + "; initComps = "
                        + location.getPlan().getEntranceLayout());
            } else if (location.getMainExit() == null) {
                location.setMainExit((Entrance) obj);
                location.getPlan().setExitLayout(DungeonLevelMaster.getLayout(location.getPlan(), c));
                LogMaster.log(1, "Main Exit: " + obj + "; initComps = "
                        + location.getPlan().getExitLayout());
            }
            location.getEntrances().add((Entrance) obj);

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

    public Location getLocation() {
        return location;
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
        return getMinimap().getGrid();
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

    public Map<ObjType, Unit> getObjCache() {
        return objCache;
    }

    public List<MapBlock> getBlocks() {
        return location.getPlan().getBlocks();
    }


    public Minimap getMinimap() {
        return minimap;
    }

    public void setMinimap(Minimap minimap) {
        this.minimap = minimap;
    }
}
