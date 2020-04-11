package main.level_editor.backend.handlers.operation;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.xml.XML_Converter;
import main.data.xml.XmlStringBuilder;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.brush.LE_BrushType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class LE_ObjHandler extends LE_Handler {
    Map<Integer, DIRECTION> overlayDirectionMap = new LinkedHashMap<>();

    private static final String DEFAULT_TYPE = "Bone Wall";

    public LE_ObjHandler(LE_Manager manager) {
        super(manager);
    }


    public void move(BattleFieldObject bfObj, Coordinates c) {
        bfObj.setCoordinates(c);
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);
        //TODO overlays!
    }

    public void removeIgnoreWrap(BattleFieldObject bfObj) {
        remove(bfObj);
    }
    protected void remove(BattleFieldObject bfObj) {
        getGame().softRemove(bfObj);
        getAiHandler().removed(bfObj);
        if (EntityCheckMaster.isEntrance(bfObj)) {
            getTransitHandler().entranceRemoved(bfObj);
        }
        if (bfObj.isOverlaying()) {
            GuiEventManager.trigger(GuiEventType.REMOVE_OVERLAY_VIEW, bfObj);
        } else
            GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);

    }

    public void addFromPalette(int gridX, int gridY) {
        addFromPalette(Coordinates.get(gridX, gridY));
    }

    public void addFromPalette(Coordinates c) {
        if (getModel().isBrushMode() && getModel().getBrush().getBrushType() != LE_BrushType.none)
        {
            ObjType type = getPaletteHandler().getFiller(
                    getModel().getBrush().getBrushType());
            operation(Operation.LE_OPERATION.ADD_OBJ,  type,
                    c);
            getStructureHandler().initWall(c);
//            getCoordinatesForShape(PositionMaster.SHAPES.STAR)
        }
        else
        operation(Operation.LE_OPERATION.ADD_OBJ, getModel().getPaletteSelection().getObjType(),
                c);
    }

    public BattleFieldObject addObjIgnoreWrap(ObjType objType, int gridX, int gridY) {
        return  addObj(objType, gridX, gridY) ;
    }
        protected BattleFieldObject addObj(ObjType objType, int gridX, int gridY) {
        BattleFieldObject bfObj = getGame().createObject(objType, gridX, gridY, DC_Player.NEUTRAL);
        getAiHandler().objectAdded(bfObj);
        if (EntityCheckMaster.isEntrance(bfObj)) {
            getTransitHandler().entranceAdded(bfObj);
        }
        return bfObj;
        //TODO Player!!!
    }

    public BattleFieldObject addOverlay(DIRECTION d, ObjType objType, int gridX, int gridY) {
        BattleFieldObject object = getGame().createObject(objType, gridX, gridY, DC_Player.NEUTRAL);
        object.setDirection(d);
        overlayDirectionMap.put(getIdManager().getId(object), d);
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
            operation(Operation.LE_OPERATION.REMOVE_OBJ, getIdManager().getObjectById(id));
        }

    }

    @Override
    public String getXml(Function<Integer, Boolean> idFilter) {
        XmlStringBuilder builder = new XmlStringBuilder();
        for (Integer integer : overlayDirectionMap.keySet()) {
            builder.append(integer).append("=").append(overlayDirectionMap.get(integer).toString()).append(";");
        }
        return
                XML_Converter.wrap(FloorLoader.OVERLAY_DIRECTIONS, builder.toString());
    }


    public ObjType getDefaultWallType() {
//         getModel().getModule().getDefaultWallType();
        if (getModel()  != null)
        if (getModel().getBlock() != null) {
            return DataManager.getType(getModel().getBlock().getWallType(), DC_TYPE.BF_OBJ);
        }
        return DataManager.getType(DEFAULT_TYPE, DC_TYPE.BF_OBJ);
    }
}
