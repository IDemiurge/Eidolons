package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battle.universal.*;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.dungeon.location.layer.LayerManager;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.location.struct.PlaceholderResolver;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureMaster;
import eidolons.game.battlecraft.logic.dungeon.module.BridgeMaster;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.module.ModuleLoader;
import eidolons.game.battlecraft.logic.dungeon.module.PortalMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.objects.*;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj.DUNGEON_OBJ_TYPE;
import eidolons.libgdx.particles.ambi.ParticleManager;
import main.entity.type.ObjType;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;

import java.util.LinkedHashMap;
import java.util.Map;

import static main.system.GuiEventType.UPDATE_DUNGEON_BACKGROUND;

/*
 *
 */
public abstract class DungeonMaster<E extends DungeonWrapper> {
    protected DC_Game game;
    protected E dungeonWrapper;
    protected DungeonInitializer<E> initializer;
    protected DungeonBuilder<E> builder;
    protected Positioner<E> positioner;
    protected Spawner<E> spawner;
    protected FacingAdjuster<E> facingAdjuster;
    private ExplorationMaster explorationMaster;
    private DoorMaster doorMaster;
    private LockMaster lockMaster;
    private ContainerMaster containerMaster;
    private InteractiveObjMaster interactiveMaster;
    private DungeonLevel dungeonLevel;
    private TrapMaster trapMaster;
    private PuzzleMaster puzzleMaster;
    private PortalMaster portalMaster;
    private LayerManager layerManager;
    private StructureMaster structureMaster;
    private FloorLoader floorLoader;

    private Map<Integer, BattleFieldObject> objIdMap = new LinkedHashMap<>();
    private Map<Integer, ObjType> idTypeMap;
    private Map<DataMap, Map<Integer, String>> dataMaps;
    private DC_ObjInitializer objInitializer;
    private StructureBuilder structureBuilder;
    private ModuleLoader moduleLoader;
    private PlaceholderResolver placeholderResolver;


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
    }
    protected FloorLoader createFloorLoader() {
        return new FloorLoader(this);
    }
    protected LayerManager createLayerManager() {
        return null;
    }

    protected DungeonBuilder<E> createBuilder() {
        return new DungeonBuilder<E>(this);
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
        ParticleManager.init(dungeonWrapper.getDungeon());
        GuiEventManager.trigger(UPDATE_DUNGEON_BACKGROUND, dungeonWrapper.getMapBackground());
        spawner.spawn();
    }

    protected boolean isPuzzlesOn() {
        return true;
    }

    public void init() {
        if (dungeonWrapper == null)
            dungeonWrapper = initDungeon();
        //TODO remove this!

        if (dungeonWrapper == null) {
            dungeonWrapper = initDungeon();
        }
        if (!CoreEngine.isCombatGame()) {
            return;
        }
//TODO dc init fix
//        getBattleMaster().getScriptManager().parseDungeonScripts(dungeonWrapper.getDungeon());
//        trapMaster.initTraps(getDungeon());

        GuiManager.setCurrentLevelCellsX(dungeonWrapper.getWidth());
        GuiManager.setCurrentLevelCellsY(dungeonWrapper.getHeight());

    }

    public void setDungeonWrapper(E dungeonWrapper) {
        this.dungeonWrapper = dungeonWrapper;
    }

    protected void processMetaDataMap(Map<String, String> dataMap) {
        getDungeonLevel().initUnitFacingMap(dataMap);
        getDungeonLevel().initCellTypeMap(dataMap);


        for (String coordinate : dataMap.keySet()) {
            String data = dataMap.get(coordinate);
            data = BridgeMaster.processMetaData(data);

            if (portalMaster.addPortal(coordinate, data)) {
                continue;
            }
            if (KeyMaster.addCustomKey(coordinate, data)) {
                continue;
            }
            //anything else?
        }
    }

    protected E initDungeon() {
        try {
            return initializer.initDungeon();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return dungeonWrapper;
    }

    protected abstract FacingAdjuster<E> createFacingAdjuster();

    protected abstract Positioner<E> createPositioner();

    protected abstract Spawner<E> createSpawner();

    protected abstract DungeonInitializer<E> createInitializer();

    public DC_Game getGame() {
        return game;
    }

    public StructureMaster getStructureMaster() {
        return structureMaster;
    }

    public DungeonInitializer<E> getInitializer() {
        return initializer;
    }

    public FacingAdjuster<E> getFacingAdjuster() {
        return facingAdjuster;
    }

    public DungeonBuilder<E> getBuilder() {
        return builder;
    }

    public Positioner<E> getPositioner() {
        return positioner;
    }

    public Spawner<E> getSpawner() {
        return spawner;
    }

    public E getDungeonWrapper() {
        return dungeonWrapper;
    }


    public BattleMaster getBattleMaster() {
        return game.getBattleMaster();
    }

    public PlayerManager getPlayerManager() {
        return getBattleMaster().getPlayerManager();
    }

    public PuzzleMaster getPuzzleMaster() {
        return puzzleMaster;
    }

    public BattleOptionManager getOptionManager() {
        return getBattleMaster().getOptionManager();
    }

    public BattleStatManager getStatManager() {
        return getBattleMaster().getStatManager();
    }

    public BattleConstructor getConstructor() {
        return getBattleMaster().getConstructor();
    }

    public BattleOutcomeManager getOutcomeManager() {
        return getBattleMaster().getOutcomeManager();
    }

    public Battle getBattle() {
        return getBattleMaster().getBattle();
    }

    public Dungeon getDungeon() {
        return dungeonWrapper.getDungeon();
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

    public DungeonLevel getDungeonLevel() {
        if (EidolonsGame.TOWN)
            if (dungeonWrapper == null && dungeonLevel == null) {
//            dungeonWrapper = initDungeon();
                init();
            }
        return dungeonLevel;
    }

    public void setDungeonLevel(DungeonLevel dungeonLevel) {
        this.dungeonLevel = dungeonLevel;
    }

    public void next() {
        dungeonWrapper = null;
    }

    public TrapMaster getTrapMaster() {
        return trapMaster;
    }

    public PortalMaster getPortalMaster() {
        return portalMaster;
    }

    public String getDefaultEntranceType() {
        return getDungeonLevel().getEntranceType();
    }

    public String getDefaultExitType() {
        return getDungeonLevel().getExitType();
    }

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public Map<Integer, ObjType> getIdTypeMap() {
        return idTypeMap;
    }

    public void setIdTypeMap(Map<Integer, ObjType> idTypeMap) {
        this.idTypeMap = idTypeMap;
    }

    public Map<Integer, BattleFieldObject> getObjIdMap() {
        return objIdMap;
    }

    public void setObjIdMap(Map<Integer, BattleFieldObject> objIdMap) {
        this.objIdMap = objIdMap;
    }

    public Map<Integer, String> getDataMap(DataMap type) {
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

}
