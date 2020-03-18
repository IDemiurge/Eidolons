package main.level_editor.backend.handlers.operation.obj;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.entity.obj.MicroObj;
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
        operation(Operation.LE_OPERATION.MOVE_OBJ, bfObj, bfObj.getCoordinates());
        bfObj.setCoordinates(c);
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);
    }

    public void remove(BattleFieldObject bfObj) {
        getGame().softRemove(bfObj);
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);

        operation(Operation.LE_OPERATION.ADD_OBJ, bfObj);
    }

    public void addSelectedObj(int gridX, int gridY) {
        addObj(getModel().getPaletteSelection().getObjType(), gridX, gridY);
    }

    public void addObj(ObjType objType, int gridX, int gridY) {
        MicroObj unit = getGame().createUnit(objType,
                gridX, gridY, DC_Player.NEUTRAL); //TODO Player!!!

        operation(Operation.LE_OPERATION.ADD_OBJ, unit);

    }
}
