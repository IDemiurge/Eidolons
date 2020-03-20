package main.level_editor.backend.handlers.operation.obj;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_ObjHandler extends LE_Handler {
    public LE_ObjHandler(LE_Manager manager) {
        super(manager);
    }


    public void move(BattleFieldObject bfObj, Coordinates c) {
        bfObj.setCoordinates(c);
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);
    }

    public void remove(BattleFieldObject bfObj) {
        getGame().softRemove(bfObj);
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);

    }

    public void addSelectedObj(int gridX, int gridY) {
        operation(Operation.LE_OPERATION.ADD_OBJ, getModel().getPaletteSelection().getObjType(), Coordinates.get(gridX,gridY));
    }

    public BattleFieldObject addObj(ObjType objType, int gridX, int gridY) {
      return    getGame().createUnit(objType, gridX, gridY, DC_Player.NEUTRAL);
        //TODO Player!!!


    }

    public void clear(Coordinates coordinates) {
        for (BattleFieldObject battleFieldObject : getGame().getObjectsAt(coordinates)) {
            remove(battleFieldObject);
        }
    }
}
