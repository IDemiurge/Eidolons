package main.level_editor.backend.model;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.functions.selection.PaletteSelection;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_ModelManager extends LE_Handler {

    private static final String DEFAULT_TYPE = "Bone Wall";
    LE_DataModel model;

    public LE_ModelManager(LE_Manager manager) {
        super(manager);
        model = new LE_DataModel();
//model.setCoordinateSelection(CoordinatesMaster.getCenterCoordinate(getModule().getCoordinates()));
        model.setPaletteSelection(new PaletteSelection(DataManager.getType(DEFAULT_TYPE, DC_TYPE.BF_OBJ)));
    }

    public void undo() {
    }

    public void toDefault() {

    }

    public LE_DataModel getModel() {
        return model;
    }

    public void paletteSelection(Entity entity) {
        PaletteSelection selection=new PaletteSelection((ObjType) entity, false);
        getModel().setPaletteSelection(selection);
    }

    public ObjType getDefaultWallType() {
      return   getModel().getDefaultWallType();
    }

    public void remove(BattleFieldObject bfObj) {
        getGame().softRemove(bfObj);
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);
    }

    public void addSelectedObj(int gridX, int gridY) {
        addObj(getModel().getPaletteSelection().getObjType(), gridX, gridY);
    }
    public void addObj(ObjType objType, int gridX, int gridY) {
        getGame().createUnit(objType,
                gridX, gridY, DC_Player.NEUTRAL);

    }
}
