package main.level_editor.backend.handlers.operation;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_ObjHandler extends LE_Handler {
    public LE_ObjHandler(LE_Manager manager) {
        super(manager);
    }


    public void move(BattleFieldObject bfObj, Coordinates c) {
        bfObj.setCoordinates(c);
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);
        //TODO overlays!
    }

    protected void remove(BattleFieldObject bfObj) {
        getGame().softRemove(bfObj);
        getAiHandler().removed(bfObj);
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);

    }

    public void addFromPalette(int gridX, int gridY) {
        operation(Operation.LE_OPERATION.ADD_OBJ, getModel().getPaletteSelection().getObjType(), Coordinates.get(gridX, gridY));
    }

    protected BattleFieldObject addObj(ObjType objType, int gridX, int gridY) {
        BattleFieldObject bfObj = getGame().createObject(objType, gridX, gridY, DC_Player.NEUTRAL);
        getAiHandler().objectAdded(bfObj);
        return bfObj;
        //TODO Player!!!
    }

    public BattleFieldObject addOverlay(DIRECTION d, ObjType objType, int gridX, int gridY) {
        BattleFieldObject object = getGame().createObject(objType, gridX, gridY, DC_Player.NEUTRAL);
        object.setDirection(d);
        //TODO Player!!!
        return object;
    }

    public void clear(Coordinates coordinates) {
        for (BattleFieldObject battleFieldObject : getGame().getObjectsAt(coordinates)) {
            operation(Operation.LE_OPERATION.REMOVE_OBJ, battleFieldObject);
        }
    }

    public void removeSelected() {
        operation(Operation.LE_OPERATION.CLEAR_START);

        for (Integer id : getSelectionHandler().getSelection().getIds()) {
            removeById(id);
        }

        operation(Operation.LE_OPERATION.CLEAR_END);
    }

    private void removeById(Integer id) {
        if (getIdManager().getObjectById(id) instanceof BattleFieldObject) {
            operation(Operation.LE_OPERATION.REMOVE_OBJ,  getIdManager().getObjectById(id));
        }

    }
}
