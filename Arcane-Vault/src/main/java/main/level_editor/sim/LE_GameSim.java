package main.level_editor.sim;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.DC_BattleFieldManager;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.game.DC_BattleFieldGrid;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.game.ScenarioGame;
import eidolons.game.core.master.ObjCreator;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.level_editor.metadata.object.LE_IdManager;
import main.level_editor.sim.impl.LE_DungeonMaster;

import java.util.Map;

public class LE_GameSim extends ScenarioGame {
    private final LE_IdManager simIdManager;
    private Unit dummyPC;

    public LE_GameSim(LE_MetaMaster metaMaster) {
        super(metaMaster);
        simIdManager = new LE_IdManager();
        setSimulation(true);
    }

    @Override
    protected DC_GameManager createGameManager() {
        return new DC_GameManager(getState(), this) {
            @Override
            public Unit getActiveObj() {
                return dummyPC;
            }

            @Override
            public void init() {
                super.init();
                setObjCreator(new ObjCreator(getGame()){
                    @Override
                    public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
                        MicroObj obj = super.createUnit(type, x, y, owner, ref);
                        Integer id = simIdManager.objectCreated(obj);
                        getMetaMaster().getDungeonMaster().getLayerManager().addToCurrent(id , obj);
                        return obj;
                    }
                });
            }
        };
    }

    public Integer getId(BattleFieldObject bfObj) {
        return simIdManager.getId(bfObj);
    }

    public void setObjIdMap(Map<Integer, Obj> objIdMap) {
        simIdManager.setObjIds(objIdMap);
    }

    @Override
    public void removed(Obj obj) {
        Integer id = simIdManager.objectRemoved(obj);
        getMetaMaster().getDungeonMaster().getLayerManager().removeFromCurrent(id , obj);
    }

    @Override
    public void softRemove(BattleFieldObject obj) {
        super.softRemove(obj);
        removed(obj);
    }


    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public void battleInit() {
        dungeonMaster.init();
        grid = new DC_BattleFieldGrid(getDungeon());
        battleFieldManager = new DC_BattleFieldManager(this);
//        dummyPC = createUnit()
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public void start(boolean first) {

        dungeonMaster.gameStarted();

        try {
            getManager().reset();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    @Override
    protected DungeonMaster createDungeonMaster() {
        return new LE_DungeonMaster(this);
    }

    public LE_IdManager getSimIdManager() {
        return simIdManager;
    }

}
