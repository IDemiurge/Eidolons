package main.game.logic.macro.entity;

import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.MACRO_PARAMS;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroRef;
import main.game.logic.macro.gui.map.obj.MapObjComp;
import main.game.logic.macro.map.Area;
import main.game.logic.macro.map.Place.PLACE_VISIBILITY_STATUS;
import main.game.logic.macro.travel.MacroCoordinates;

public abstract class MapObj extends MacroObj {

    protected Coordinates coordinates;
    protected Coordinates mapRenderPoint;
    protected PLACE_VISIBILITY_STATUS visibilityStatus;
    protected MapObjComp comp;
    protected Area area;

    public MapObj(ObjType type, MacroRef ref) {
        super(type, ref);
    }

    public MapObj(MacroGame game, ObjType type, MacroRef ref) {
        super(game, type, ref);
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        setParam(G_PARAMS.POS_X, coordinates.x);
        setParam(G_PARAMS.POS_Y, coordinates.y);
        int x = this.coordinates.x - getDefaultSize() / 2;
        int y = this.coordinates.y - getDefaultSize() / 2;
        setParam(MACRO_PARAMS.MAP_POS_X, x, true);
        setParam(MACRO_PARAMS.MAP_POS_Y, y, true);
        setMapRenderPoint(new MacroCoordinates(x, y));

    }

    public abstract int getDefaultSize();

    public Coordinates getDefaultMapRenderPoint() {
        return mapRenderPoint;
    }

    public void setMapRenderPoint(Coordinates mapRenderPoint) {
        this.mapRenderPoint = mapRenderPoint;
    }

}
