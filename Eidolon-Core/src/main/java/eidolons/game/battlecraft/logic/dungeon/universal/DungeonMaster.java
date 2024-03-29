package eidolons.game.battlecraft.logic.dungeon.universal;

import com.badlogic.gdx.graphics.Color;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.DC_ObjInitializer;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.LightConsts;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.TransitHandler;
import eidolons.game.battlecraft.logic.dungeon.location.layer.LayerManager;
import eidolons.game.battlecraft.logic.dungeon.location.struct.*;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.module.ModuleLoader;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Awakener;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.battlecraft.logic.mission.universal.*;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeon.objects.*;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.game.exploration.dungeon.objects.DungeonObj.DUNGEON_OBJ_TYPE;
import eidolons.content.consts.libgdx.GdxColorMaster;
import main.content.enums.DungeonEnums;
import main.game.bf.Coordinates;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;
import main.system.math.MathMaster;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static main.system.GuiEventType.UPDATE_DUNGEON_BACKGROUND;

/*
 *
 */
public abstract class DungeonMaster {
    protected DC_Game game;
    protected Location floorWrapper;
    protected DungeonInitializer initializer;
    protected DungeonBuilder builder;
    protected Positioner positioner;
    protected Spawner spawner;
    private ExplorationMaster explorationMaster;
    private DoorMaster doorMaster;
    private LockMaster lockMaster;
    private ContainerMaster containerMaster;
    private InteractiveObjMaster interactiveMaster;
    private PuzzleMaster puzzleMaster;
    private IPortalMaster portalMaster;
    private final LayerManager layerManager;
    private final StructMaster structMaster;
    private final FloorLoader floorLoader;
    private Awakener awakener;

    private Map<DungeonEnums.DataMap, Map<Integer, String>> dataMaps;
    private final DC_ObjInitializer objInitializer;
    private final StructureBuilder structureBuilder;
    private final ModuleLoader moduleLoader;
    private final PlaceholderResolver placeholderResolver;
    private final TransitHandler transitHandler;
    private ColorMapDataSource colorMapDataSource;


    public DungeonMaster(DC_Game game) {
        this.game = game;
        initializer = createInitializer();
        spawner = createSpawner();
        layerManager = createLayerManager();
        positioner = createPositioner();
        builder = createBuilder();
        structMaster = new StructMaster(this);
        objInitializer = createObjInitializer();
        structureBuilder = new StructureBuilder(this);
        floorLoader = createFloorLoader();

        if (Flags.isCombatGame()) {
            awakener = new Awakener(game);
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

    protected DC_ObjInitializer createObjInitializer() {
        return new DC_ObjInitializer(this);
    }

    protected FloorLoader createFloorLoader() {
        return new FloorLoader(this);
    }

    protected LayerManager createLayerManager() {
        return null;
    }

    protected DungeonBuilder createBuilder() {
        return new DungeonBuilder(this);
    }

    public void setExplorationMaster(ExplorationMaster explorationMaster) {
        this.explorationMaster = explorationMaster;
    }

    public void gameStarted() {
        GuiEventManager.trigger(UPDATE_DUNGEON_BACKGROUND, floorWrapper.getMapBackground());
        spawner.spawn();
    }

    protected boolean isPuzzlesOn() {
        return true;
    }

    public void init() {
        portalMaster = game.getMetaMaster().getGdxBeans().createPortalMaster(this);
        if (floorWrapper == null)
            floorWrapper = initDungeon();
        //TODO remove this!

        if (floorWrapper == null) {
            floorWrapper = initDungeon();
        }
        if (!Flags.isCombatGame()) {
            return;
        }
        //TODO dc init fix
        //        getBattleMaster().getScriptManager().parseDungeonScripts(dungeonWrapper.getDungeon());
        //        trapMaster.initTraps(getDungeon());

        Coordinates.setFloorWidth(floorWrapper.getWidth());
        Coordinates.setFloorHeight(floorWrapper.getHeight());

    }

    public void resetColorMap(Set<Coordinates> set) {
        Map<Coordinates, Color> map = new HashMap<>(set.size() + 10, 0.991f);
        //TODO do it per struct instead !
        for (Coordinates coordinate : set) {
            //ambient light here
            LevelStruct struct = getStructMaster().getLowestStruct(coordinate);
            int light =LightConsts.MIN_AMBIENT_LIGHT;
            Coordinates center = struct.getCenterCoordinate();
            if (struct.getIlluminationValue()> light) {
                light =  struct.getIlluminationValue();
            }
            int size = struct.getCoordinatesSet().size();
            float a=light / 100f;
            Color c;
            Color c2 = GdxColorMaster.getColorForTheme(struct.getAltColorTheme());
            if (c2 == null) {
                c2=GdxColorMaster.LIGHT_GREY;
            }
            if (struct.getColorTheme() != null) {
                c = GdxColorMaster.getColorForTheme(struct.getColorTheme());
                //lerp based on how far from center
                float lerp=getLerp(size, center.dst(coordinate));
                c= new Color(c2.r, c2.g, c2.b, a).lerp(c, lerp);
            } else {
                c= GdxColorMaster.LIGHT_GREY;
            }
            if (a>0.9f) {
                map.put(coordinate, new Color(c.r, c.g, c.b, a));
            }
            map.put(coordinate, new Color(c.r, c.g, c.b, a));
        }
        colorMapDataSource = new ColorMapDataSource(map);
        GuiEventManager.trigger(GuiEventType.COLORMAP_RESET, colorMapDataSource);
    }

    private float getLerp(int size, int dst) {
        //max dst from center is about 1/3 of size for rectangular
        // Math.sqrt()
        return MathMaster.minMax((float) size /(dst+4)/10, 0.12f, 0.8f);
    }

    public void setFloorWrapper(Location floorWrapper) {
        this.floorWrapper = floorWrapper;
    }

    protected Location initDungeon() {
        try {
            return initializer.initDungeon();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
            return floorWrapper;
        }
    }

    protected abstract Positioner createPositioner();

    protected abstract Spawner createSpawner();

    protected abstract DungeonInitializer createInitializer();

    public DC_Game getGame() {
        return game;
    }

    public StructMaster getStructMaster() {
        return structMaster;
    }

    public DungeonInitializer getInitializer() {
        return initializer;
    }

    public DungeonBuilder getBuilder() {
        return builder;
    }

    public Positioner getPositioner() {
        return positioner;
    }

    public Spawner getSpawner() {
        return spawner;
    }

    public Location getFloorWrapper() {
        return floorWrapper;
    }


    public MissionMaster getMissionMaster() {
        return game.getMissionMaster();
    }

    public PlayerManager getPlayerManager() {
        return getMissionMaster().getPlayerManager();
    }

    public PuzzleMaster getPuzzleMaster() {
        return puzzleMaster;
    }

    public MissionOptionManager getOptionManager() {
        return getMissionMaster().getOptionManager();
    }

    public MissionStatManager getStatManager() {
        return getMissionMaster().getStatManager();
    }

    public MissionConstructor getConstructor() {
        return getMissionMaster().getConstructor();
    }

    public DungeonSequence getMission() {
        return getMissionMaster().getMission();
    }

    public Floor getDungeon() {
        return floorWrapper.getFloor();
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
        floorWrapper = null;
    }

    public IPortalMaster getPortalMaster() {
        return portalMaster;
    }

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public Map<Integer, String> getDataMap(DungeonEnums.DataMap type) {
        if (dataMaps == null) {
            return null;
        }
        return dataMaps.get(type);
    }

    public void setDataMaps(Map<DungeonEnums.DataMap, Map<Integer, String>> dataMaps) {
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

    public void reinit() {
        getBuilder().initLocationSize(getFloorWrapper());
    }

    public void initPuzzles(Map<Coordinates, CellScriptData> textDataMap) {
        if (isPuzzlesOn()) {
            getPuzzleMaster().init(textDataMap);
        }
    }

    public Awakener getAwakener() {
        return awakener;
    }

    public ColorMapDataSource getColorMap() {
        return colorMapDataSource;
    }

    public void loadingDone() {
        getFloorLoader().loadingDone();
        resetColorMap(game.getModule().getCoordinatesSet());
    }
}
