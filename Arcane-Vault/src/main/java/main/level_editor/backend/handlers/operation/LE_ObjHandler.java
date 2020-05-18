package main.level_editor.backend.handlers.operation;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterData;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.xml.XML_Converter;
import main.data.xml.XmlStringBuilder;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.brush.LE_BrushType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.math.PositionMaster;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LE_ObjHandler extends LE_Handler {

    private static final String DEFAULT_TYPE = "Wall Placeholder Indestructible";
    private BattleFieldObject lastAdded;
    private Coordinates lastCoordinates;
    private ObjType defaultPaletteType;

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
        Integer id = getIdManager().getId(bfObj);
        if (bfObj instanceof Entrance) {
            getTransitHandler().entranceRemoved((Entrance) bfObj);
        }

        if (bfObj.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
            getEntityHandler().encounterRemoved(id);
        }
        if (bfObj.isOverlaying()) {
            getDirectionMap().remove(id);
            GuiEventManager.trigger(GuiEventType.REMOVE_OVERLAY_VIEW, bfObj);
        } else
            GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, bfObj);
        getGame().softRemove(bfObj);
        getAiHandler().removed(bfObj);


    }

    public void addFromPalette(int gridX, int gridY) {
        addFromPalette(Coordinates.get(gridX, gridY));
    }

    public void addFromSelection(Coordinates c) {
        if (getSelectionHandler().getObject() == null) {
//            getModelManager().getCopied();
            return;
        }
        ObjType objType = getSelectionHandler().getObject().getType();
        operation(Operation.LE_OPERATION.ADD_OBJ, objType,
                c);
    }
    private void addMultiple(List<Coordinates> coordinates) {
        operation(Operation.LE_OPERATION.FILL_START);
        for (Coordinates c : coordinates) {
            addFromPalette(c);
        }
        operation(Operation.LE_OPERATION.FILL_END);
    }

    public void addFromPalette(Coordinates c) {
        if (getModel().isBrushMode() && getModel().getBrush().getBrushType() != LE_BrushType.none) {
            if (getModel().getBrush().getBrushType() ==  LE_BrushType.toggle_void) {
                operation(Operation.LE_OPERATION.VOID_TOGGLE, c);
                lastCoordinates = c;
                return;
            }
            ObjType type = getPaletteHandler().getFiller(
                    getModel().getBrush().getBrushType());
            operation(Operation.LE_OPERATION.ADD_OBJ, type,
                    c);
            getStructureHandler().initWall(c);
//            getCoordinatesForShape(PositionMaster.SHAPES.STAR)
        } else {
            ObjType objType = getModel().getPaletteSelection().getObjType();
            if (objType == null) {
                return;
            }
            if (objType.getOBJ_TYPE_ENUM() == null) {
                return;
            }
            operation(Operation.LE_OPERATION.ADD_OBJ, objType,
                    c);
            if (objType.getName().contains("Placeholder")) {
                getStructureHandler().initWall(c);
            }
        }
    }

    public BattleFieldObject addObjIgnoreWrap(ObjType objType, int gridX, int gridY) {
        return addObj(objType, gridX, gridY);
    }

    protected BattleFieldObject addObj(ObjType objType, int gridX, int gridY) {
        BattleFieldObject bfObj = getGame().createObject(objType, gridX, gridY, DC_Player.NEUTRAL);
        objAdded(bfObj);
        return bfObj;
        //TODO Player!!!
    }

    public DC_Obj createEncounter(ObjType type, Coordinates c, Integer id) {
        BattleFieldObject bfObj = getGame().createObject(type, c.x, c.y, DC_Player.NEUTRAL);

        Map<Integer, String> dataMap = getGame().getMetaMaster().getDungeonMaster().
                getDataMap(DataMap.encounters);
        if (dataMap != null) {
            String s = dataMap.get(id);
            if (dataMap == null) {
                return bfObj;
            }
            getEntityHandler().encounterAdded(id, new EncounterData(s));
        }
        return bfObj;
    }

    private void objAdded(BattleFieldObject bfObj) {
        getAiHandler().objectAdded(bfObj);

        getTransitHandler().objAdded(bfObj);

        if (bfObj.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
            Integer id = getIdManager().getId(bfObj);
            //TODO get data!
            getEntityHandler().encounterAdded(id, new EncounterData(bfObj));
        }
        lastAdded = bfObj;
        lastCoordinates = bfObj.getCoordinates();
    }

    public BattleFieldObject addOverlay(DIRECTION d, ObjType objType, int gridX, int gridY) {
        BattleFieldObject object = getGame().createObject(objType, gridX, gridY, DC_Player.NEUTRAL);
        object.setDirection(d);
        getDirectionMap().put(getIdManager().getId(object), d);
        //TODO Player!!!
        return object;
    }

    private Map<Integer, DIRECTION> getDirectionMap() {
        return getGame().getDungeonMaster().getObjInitializer().getDirectionMap();
    }

    public void clear(Coordinates coordinates) {
        for (BattleFieldObject battleFieldObject : getGame().getObjectsNoOverlaying(coordinates)) {
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
    public String getPreObjXml(Function<Integer, Boolean> idFilter, Function<Coordinates, Boolean> coordinateFilter) {
        if (getDirectionMap() == null) {
            return "";
        }
        XmlStringBuilder builder = new XmlStringBuilder();
        for (Integer integer : getDirectionMap().keySet()) {
            builder.append(integer).append("=").append(getDirectionMap().get(integer).toString()).append(";");
        }
        return
                XML_Converter.wrap(FloorLoader.OVERLAY_DIRECTIONS, builder.toString());
    }


    public void addInLine(Coordinates c) {
        if (lastCoordinates == null) {
            return;
        }
        Coordinates c1 =lastCoordinates;
        if (PositionMaster.inLine(c1, c)) {
            List<Coordinates> coordinates = CoordinatesMaster.getCoordinatesBetweenInclusive(c1, c);
            coordinates.remove(c1);
            Collections.reverse(coordinates);
            addMultiple(  coordinates);
        } else if (PositionMaster.inLineDiagonally(c1, c)) {
            List<Coordinates> coordinates = CoordinatesMaster.getCoordinatesBetweenInclusive(c1, c);
            coordinates.removeIf(coord -> {
                if (PositionMaster.inLineDiagonally(c1, coord))
                    return !PositionMaster.inLineDiagonally(c, coord);
                return true;
            });
            coordinates.remove(c1);
            addMultiple(  coordinates);
            //TODO
        }
    }

    public ObjType getDefaultPaletteType() {
        if (defaultPaletteType == null) {
            defaultPaletteType = DataManager.getType(DEFAULT_TYPE, DC_TYPE.BF_OBJ);
        }
        return defaultPaletteType;
    }

    public void copyTo(BattleFieldObject object, Coordinates c) {
        if (object.isOverlaying()) {
            operation(Operation.LE_OPERATION.ADD_OVERLAY, object.getType(), c);
        } else {
            operation(Operation.LE_OPERATION.ADD_OBJ, object.getType(), c);
        }
    }
}
