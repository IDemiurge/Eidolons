package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.TransitHandler;
import eidolons.game.battlecraft.logic.dungeon.location.layer.LayerManager;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.location.struct.PlaceholderResolver;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureMaster;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.module.ModuleLoader;
import eidolons.game.battlecraft.logic.dungeon.module.PortalMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import eidolons.game.battlecraft.logic.mission.universal.*;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.objects.*;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj.DUNGEON_OBJ_TYPE;
import eidolons.libgdx.particles.ambi.ParticleManager;
import main.game.bf.Coordinates;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.launch.CoreEngine;

import java.util.Map;
import java.util.Set;

import static main.system.GuiEventType.UPDATE_DUNGEON_BACKGROUND;

/*
 *
 */
public abstract class DungeonMaster {
    protected DC_Game game;
    protected Location location;
    protected DungeonInitializer initializer;
    protected DungeonBuilder  builder;
    protected Positioner positioner;
    protected Spawner spawner;
    protected FacingAdjuster facingAdjuster;
    private ExplorationMaster explorationMaster;
    private DoorMaster doorMaster;
    private LockMaster lockMaster;
    private ContainerMaster containerMaster;
    private InteractiveObjMaster interactiveMaster;
    private TrapMaster trapMaster;
    private PuzzleMaster puzzleMaster;
    private PortalMaster portalMaster;
    private LayerManager layerManager;
    private StructureMaster structureMaster;
    private FloorLoader floorLoader;

    private Map<DataMap, Map<Integer, String>> dataMaps;
    private DC_ObjInitializer objInitializer;
    private StructureBuilder structureBuilder;
    private ModuleLoader moduleLoader;
    private PlaceholderResolver placeholderResolver;
    private TransitHandler transitHandler;


    public DungeonMaster(DC_Game game) {
        this.game = game;
        trapMaster = new TrapMaster(this);
        portalMaster = new PortalMaster(this);
        initializer = createInitializer();
        spawner = createSpawner();
        layerManager = createLayerManager();
        positioner = createPositioner();
        facingAdjuster = createFacingAdjuster();
        builder = createBuilder();
        structureMaster = new StructureMaster(this);
        objInitializer = new DC_ObjInitializer(this);
        structureBuilder = new StructureBuilder(this);
        floorLoader = createFloorLoader();
        if (CoreEngine.isCombatGame()) {
            explorationMaster = new ExplorationMaster(game);
            doorMaster = new DoorMaster(this);
            lockMaster = new LockMaster(this);
            containerMaster = new ContainerMaster(this);
            interactiveMaster = new InteractiveObjMaster(this);
            puzzleMaster = new PuzzleMaster(this);
        }
        moduleLoader = new ModuleLoader(this);
        placeholderResolver = new PlaceholderResolver(this);
        transitHandler = new TransitHandler(this);
    }
    protected FloorLoader createFloorLoader() {
        return new FloorLoader(this);
    }
    protected LayerManager createLayerManager() {
        return null;
    }

    protected DungeonBuilder  createBuilder() {
        return new DungeonBuilder(this);
    }

    public void setExplorationMaster(ExplorationMaster explorationMaster) {
        this.explorationMaster = explorationMaster;
    }

    public void gameStarted() {
        if (isPuzzlesOn())
            try {
                puzzleMaster.initPuzzles(getDungeon() );
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }
        ParticleManager.init(location.getDungeon());
        GuiEventManager.trigger(UPDATE_DUNGEON_BACKGROUND, location.getMapBackground());
        spawner.spawn();
    }

    protected boolean isPuzzlesOn() {
        return true;
    }

    public void init() {
        if (location == null)
            location = initDungeon();
        //TODO remove this!

        if (location == null) {
            location = initDungeon();
        }
        if (!CoreEngine.isCombatGame()) {
            return;
        }
//TODO dc init fix
//        getBattleMaster().getScriptManager().parseDungeonScripts(dungeonWrapper.getDungeon());
//        trapMaster.initTraps(getDungeon());

        Coordinates.setFloorWidth(location.getWidth());
        Coordinates.setFloorHeight(location.getHeight());

    }

    public void setLocation(Location location) {
        this.location = location;
    }

    protected void processCoordinateMap(String data, DataMap type) {
        switch (type) {

        }
//        getDungeonLevel().initUnitFacingMap(dataMap);
//        getDungeonLevel().initCellTypeMap(dataMap);

//    TODO     for (String coordinate : dataMap.keySet()) {
//            String data = dataMap.get(coordinate);
//            data = BridgeMaster.processMetaData(data);
//
//            if (portalMaster.addPortal(coordinate, data)) {
//                continue;
//            }
//            if (KeyMaster.addCustomKey(coordinate, data)) {
//                continue;
//            }
            //anything else?
//        }
    }

    protected Location initDungeon() {
        try {
            return initializer.initDungeon();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return location;
    }

    protected abstract FacingAdjuster createFacingAdjuster();

    protected abstract Positioner createPositioner();

    protected abstract Spawner createSpawner();

    protected abstract DungeonInitializer createInitializer();

    public DC_Game getGame() {
        return game;
    }

    public StructureMaster getStructureMaster() {
        return structureMaster;
    }

    public DungeonInitializer getInitializer() {
        return initializer;
    }

    public FacingAdjuster getFacingAdjuster() {
        return facingAdjuster;
    }

    public DungeonBuilder  getBuilder() {
        return builder;
    }

    public Positioner getPositioner() {
        return positioner;
    }

    public Spawner getSpawner() {
        return spawner;
    }

    public Location getLocation() {
        return location;
    }


    public MissionMaster getBattleMaster() {
        return game.getMissionMaster();
    }

    public PlayerManager getPlayerManager() {
        return getBattleMaster().getPlayerManager();
    }

    public PuzzleMaster getPuzzleMaster() {
        return puzzleMaster;
    }

    public MissionOptionManager getOptionManager() {
        return getBattleMaster().getOptionManager();
    }

    public MissionStatManager getStatManager() {
        return getBattleMaster().getStatManager();
    }

    public MissionConstructor getConstructor() {
        return getBattleMaster().getConstructor();
    }

    public MissionOutcomeManager getOutcomeManager() {
        return getBattleMaster().getOutcomeManager();
    }

    public DungeonSequence getBattle() {
        return getBattleMaster().getMission();
    }

    public Dungeon getDungeon() {
        return location.getDungeon();
    }

    public ExplorationMaster getExplorationMaster() {
        return explorationMaster;
    }


    public DungeonObjMaster getDungeonObjMaster(DUNGEON_OBJ_TYPE type) {
        switch (type) {
            case DOOR:
                return doorMaster;
            case LOCK:
                return lockMaster;
            case CONTAINER:
                return containerMaster;
            case INTERACTIVE:
                return interactiveMaster;
        }
        return null;
    }

    public void next() {
        location = null;
    }

    public TrapMaster getTrapMaster() {
        return trapMaster;
    }

    public PortalMaster getPortalMaster() {
        return portalMaster;
    }

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public Map<Integer, String> getDataMap(DataMap type) {
        if (dataMaps == null) {
            return null;
        }
        return dataMaps.get(type);
    }

    public void setDataMaps(Map<DataMap, Map<Integer, String>> dataMaps) {
        this.dataMaps = dataMaps;
    }

    public Module getModule() {
        return null;
    }

    public DC_ObjInitializer getObjInitializer() {
        return objInitializer;
    }

    public StructureBuilder getStructureBuilder() {
        return structureBuilder;
    }

    public FloorLoader getFloorLoader() {
        return floorLoader;
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public PlaceholderResolver getPlaceholderResolver() {
        return placeholderResolver;
    }

    public BattleFieldObject getObjByOriginalModuleId(Integer id) {
        for (Module module : getModules()) {
            BattleFieldObject object = module.getObjIdMap().get(id);
            if (object != null) {
                return object;
            }
        }
        return null;
    }

    public Set<Module> getModules() {
        return getGame().getMetaMaster().getModuleMaster().getModules();
    }

    public TransitHandler getTransitHandler() {
        return transitHandler;
    }

    public boolean isModuleSizeBased() {
        return true;
    }
}
