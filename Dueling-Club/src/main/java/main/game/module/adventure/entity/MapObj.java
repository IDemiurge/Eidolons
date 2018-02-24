package main.game.module.adventure.entity;

import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.MACRO_PARAMS;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.MacroRef;
import main.game.module.adventure.MacroRef.MACRO_KEYS;
import main.game.module.adventure.gui.map.obj.MapObjComp;
import main.game.module.adventure.map.Area;
import main.game.module.adventure.map.MacroCoordinates;
import main.game.module.adventure.map.MapVisionMaster.MAP_OBJ_INFO_LEVEL;
import main.game.module.adventure.map.Place.PLACE_VISIBILITY_STATUS;
import main.game.module.adventure.map.Region;
import main.system.auxiliary.StringMaster;

public abstract class MapObj extends MacroObj {

    protected Coordinates coordinates;
    protected Coordinates mapRenderPoint;
    protected PLACE_VISIBILITY_STATUS visibilityStatus;
    protected MapObjComp comp;
    protected Area area;
    private boolean detected;
    private boolean hidden;
    private MAP_OBJ_INFO_LEVEL infoLevel;

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
        setX(x);setY(y);
        setParam(MACRO_PARAMS.MAP_POS_X, x, true);
        setParam(MACRO_PARAMS.MAP_POS_Y, y, true);
        setMapRenderPoint(new MacroCoordinates(x, y));

    }
    public void setRegion(Region region) {
        getRef().setMacroId(MACRO_KEYS.REGION, region.getId());
        this.region = region;
    }
    @Override
    public String getNameAndCoordinate() {
        return getName() + StringMaster.wrapInParenthesis(getX() + "," + getY());
    }
    public abstract int getDefaultSize();

    public Coordinates getDefaultMapRenderPoint() {
        return mapRenderPoint;
    }

    public void setMapRenderPoint(Coordinates mapRenderPoint) {
        this.mapRenderPoint = mapRenderPoint;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }

    public boolean isDetected() {
        return detected;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setInfoLevel(MAP_OBJ_INFO_LEVEL infoLevel) {
        this.infoLevel = infoLevel;
    }

    public MAP_OBJ_INFO_LEVEL getInfoLevel() {
        return infoLevel;
    }
}
