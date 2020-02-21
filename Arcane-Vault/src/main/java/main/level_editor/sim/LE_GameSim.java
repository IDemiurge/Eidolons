package main.level_editor.sim;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.ScenarioGame;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.level_editor.metadata.object.LE_IdManager;

import java.util.Map;

public class LE_GameSim extends ScenarioGame {
    private final LE_IdManager simIdManager;

    public LE_GameSim(LE_MetaMaster metaMaster) {
        super(metaMaster);
        simIdManager = new LE_IdManager();
        setSimulation(true);
    }

    public   Integer getId(BattleFieldObject bfObj) {
        return simIdManager.getId(bfObj);
    }

    public void setObjIdMap(Map<Integer, Obj> objIdMap) {
        simIdManager.setObjIds(objIdMap);
    }

    @Override
    public void removed(Obj obj) {
         simIdManager.objectRemoved(obj);
    }

    @Override
    public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
        MicroObj obj = super.createUnit(type, x, y, owner, ref);
//        floor.getManager().get
        simIdManager.objectCreated(obj);
        return obj;
    }

    public LE_IdManager getSimIdManager() {
        return simIdManager;
    }

}
