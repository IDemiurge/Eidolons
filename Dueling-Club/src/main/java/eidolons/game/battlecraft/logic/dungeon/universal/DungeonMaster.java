package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.game.battlecraft.logic.battle.universal.*;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.objects.*;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj.DUNGEON_OBJ_TYPE;
import eidolons.libgdx.particles.ambi.ParticleManager;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;

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
    protected DungeonMapGenerator<E> mapGenerator;
    private ExplorationMaster explorationMaster;
    private DoorMaster doorMaster;
    private LockMaster lockMaster;
    private ContainerMaster containerMaster;
    private InteractiveObjMaster interactiveMaster;
    private DungeonLevel dungeonLevel;


    public DungeonMaster(DC_Game game) {
        this.game = game;
        initializer = createInitializer();
        spawner = createSpawner();
        positioner = createPositioner();
        facingAdjuster = createFacingAdjuster();
        builder = createBuilder();
        mapGenerator = new DungeonMapGenerator<E>(this);
        explorationMaster = new ExplorationMaster(game);

        doorMaster = new DoorMaster(this);
        lockMaster = new LockMaster(this);
        containerMaster = new ContainerMaster(this);
        interactiveMaster = new InteractiveObjMaster(this);
    }

    protected DungeonBuilder<E> createBuilder() {
        return new DungeonBuilder<E>(this);
    }

    public void setExplorationMaster(ExplorationMaster explorationMaster) {
        this.explorationMaster = explorationMaster;
    }

    public void gameStarted() {
        ParticleManager.init(dungeonWrapper.getDungeon());
        GuiEventManager.trigger(UPDATE_DUNGEON_BACKGROUND, dungeonWrapper.getMapBackground());
        spawner.spawn();
    }

    public void init() {
        if (dungeonWrapper == null)
            dungeonWrapper = initDungeon();
        getBuilder().initLevel();

        //TODO remove this!

        if (dungeonWrapper == null) {
            dungeonWrapper = initDungeon();
            getBuilder().initLevel();
        }
        GuiManager.setCurrentLevelCellsX(dungeonWrapper.getWidth());
        GuiManager.setCurrentLevelCellsY(dungeonWrapper.getHeight());

    }

    protected E initDungeon() {
        try {
            return initializer.initDungeon();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
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

    public DungeonMapGenerator<E> getMapGenerator() {
        return mapGenerator;
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
        if (dungeonWrapper == null && dungeonLevel == null) {
            dungeonWrapper = initDungeon();
        }
        return dungeonLevel;
    }

    public void setDungeonLevel(DungeonLevel dungeonLevel) {
        this.dungeonLevel = dungeonLevel;
    }

    public void next() {
        dungeonWrapper = null;
    }
}
