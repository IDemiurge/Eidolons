package main.game.battlecraft.logic.dungeon;

import main.game.battlecraft.logic.battle.*;
import main.game.battlecraft.logic.dungeon.location.building.DungeonMapGenerator;
import main.game.core.game.DC_Game;
import main.system.GuiEventManager;
import main.system.OnDemandEventCallBack;
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
    protected  Positioner<E> positioner;
    protected Spawner<E> spawner;
    protected FacingAdjuster<E> facingAdjuster;
    protected DungeonMapGenerator<E> mapGenerator;
    public DungeonMaster(DC_Game game) {
        this.game = game;
        initializer= createInitializer();
        spawner= createSpawner();
        positioner= createPositioner();
        facingAdjuster= createFacingAdjuster();
        builder =createBuilder();
    }

    protected DungeonBuilder<E> createBuilder() {
        return new DungeonBuilder<E>(this);
    }

    public void gameStarted() {
        spawner.spawn();
    }
    public void init(){
        dungeonWrapper = initializer.initDungeon();
        //TODO remove this!
        GuiManager.setCurrentLevelCellsX(dungeonWrapper.getWidth());
        GuiManager.setCurrentLevelCellsY(dungeonWrapper.getHeight());
        if (!ImageManager.isImage(dungeonWrapper.getMapBackground())) {
            LogMaster.log(1,
                    dungeonWrapper.getMapBackground() + " is not a valid image! >> " + dungeonWrapper);
            return;
        }
        GuiEventManager.trigger(UPDATE_DUNGEON_BACKGROUND,
                new OnDemandEventCallBack<>(dungeonWrapper.getMapBackground()));

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
        return  game.getBattleMaster();
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
}
