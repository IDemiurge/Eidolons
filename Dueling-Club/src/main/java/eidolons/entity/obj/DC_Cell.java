package eidolons.entity.obj;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.decor.wall.WallMaster;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.Cell;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

import java.util.HashSet;
import java.util.Set;

import static main.content.CONTENT_CONSTS.COLOR_THEME;
import static main.content.CONTENT_CONSTS.MARK;

public class DC_Cell extends DC_Obj implements Cell {

    private static ObjType EMPTY_CELL_TYPE;
    private final Floor floor;
    private boolean playerHasSeen;
    private boolean VOID;

    private DungeonEnums.CELL_SET cellType;
    private int cellVariant;
    private  COLOR_THEME colorTheme;

    private float overlayRotation;
    private String overlayData;

    BattleFieldObject[] overlayingObjects;
    BattleFieldObject[] objects;
    BattleFieldObject[] nonOverlaying;
    private boolean objectsModified;

    private Set<MARK> marks;


    @Override
    protected void preInit(Game game, ObjType type, Player owner, Ref ref) {
        this.game = game;
        getId(); // new id if null
        this.TYPE_ENUM = type.getOBJ_TYPE_ENUM();
        this.type = (type); // no cloning by default
        type.checkBuild();
        this.owner = owner;
        this.setOriginalOwner(owner);
        getPropMap().put(G_PROPS.NAME, type.getName());
        setOriginalName(type.getName());

        master = initMaster();
        setRef(ref); //create ref branch
    }


    public DC_Cell(ObjType t, int i, int j, DC_Game game, Ref ref, Floor floor) {
        super(t, Player.NEUTRAL, game, ref);
        this.x = i;
        this.y = j;
        this.floor = floor;
        this.coordinates = Coordinates.get(x, y);
        addDynamicValues();
        resetCell(false);
    }

    public String getDefaultImgPath() {
        return WallMaster.getCellImage(getCoordinates(), getCellVariant());
    }

    public void resetCell() {
        resetCell(true);
    }
    public void resetCell(boolean gdx) {
        cellVariant = (floor.getCellVariant(x, y));
        cellType = (floor.getCellType(x, y));
        setImage(getDefaultImgPath());
        if (gdx){
            GuiEventManager.trigger(GuiEventType.CELL_RESET, this);
        }

    }
    public void setCellType(DungeonEnums.CELL_SET cellType) {
        this.cellType = cellType;
    }

    public DC_Cell(int i, int j, DC_Game game, Ref ref, Floor floor) {
        this(getEMPTY_CELL_TYPE(), i, j, game, ref, floor);
    }

    public DC_Cell(int x, int y, DC_Game game) {
        this(x, y, game, new Ref(game), game.getDungeon());
    }

    public DC_Cell(Coordinates c, DC_Game game) {
        this(c.x, c.y, game, new Ref(game), game.getDungeon());
    }

    public static ObjType getEMPTY_CELL_TYPE() {
        if (EMPTY_CELL_TYPE == null)
            EMPTY_CELL_TYPE = DataManager.getType(StringMaster.STD_TYPE_NAMES.Cell.toString(),
                    DC_TYPE.TERRAIN);
        return EMPTY_CELL_TYPE;
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        //do not remove
    }


    public DungeonEnums.CELL_SET getCellType() {
        return cellType;
    }

    public int getCellVariant() {
        return cellVariant;
    }

    public float getOverlayRotation() {
        return overlayRotation;
    }

    public void setOverlayRotation(float overlayRotation) {
        this.overlayRotation = overlayRotation;
    }

    public COLOR_THEME getColorTheme() {
        return colorTheme;
    }


    public void setCellVariant(int cellVariant) {
        this.cellVariant = cellVariant;
    }

    public void setColorTheme(COLOR_THEME colorTheme) {
        this.colorTheme = colorTheme;
    }

    @Override
    public String getDisplayedName() {
        if (!getProperty(PROPS.TERRAIN_TYPE).isEmpty()) {
            return getProperty(PROPS.TERRAIN_TYPE) + " Cell";
        }
        return getGame().getDungeon().getName() + " Cell";
    }

    public String toString() {
        return super.toString() + " at " + getCoordinates();
    }

    //could be useful for Pillars or such
    public DIRECTION getBorderSide() {
        if (getX() + 1 == Coordinates.getFloorWidth()) {
            if (getY() + 1 == Coordinates.getFloorHeight()) {
                return DIRECTION.DOWN_RIGHT;
            }
            if (getY() == 0) {
                return DIRECTION.UP_RIGHT;
            }
            return DIRECTION.RIGHT;
        }
        if (getY() + 1 == Coordinates.getFloorHeight()) {
            if (getX() == 0) {
                return DIRECTION.DOWN_LEFT;
            }
            if (getX() + 1 == Coordinates.getFloorWidth()) {
                return DIRECTION.DOWN_RIGHT;
            }
            return DIRECTION.DOWN;
        }
        if (getY() == 0) {
            if (getX() == 0) {
                return DIRECTION.UP_LEFT;
            }
            if (getX() + 1 == Coordinates.getFloorWidth()) {
                return DIRECTION.UP_RIGHT;
            }
            return DIRECTION.UP;
        }
        if (getX() == 0) {
            if (getY() + 1 == Coordinates.getFloorHeight()) {
                return DIRECTION.DOWN_LEFT;
            }
            if (getY() == 0) {
                return DIRECTION.UP_LEFT;
            }
            return DIRECTION.LEFT;
        }
        return null;
    }
    public Set<MARK> getMarks() {
        if (marks == null) {
            marks = new HashSet<>();
        }
        return marks;
    }

    public boolean isBorderCell() {
        return getBorderSide() != null;
    }

    public void setProperty(PROPERTY prop, String value) {
        super.setProperty(prop, value);
    }

    public void toBase() {
//        super.toBase();
        name = getProp("Name")
                + StringMaster.wrapInParenthesis(StringMaster
                .format(getProperty(PROPS.VISIBILITY_STATUS)));
    }

    public String getToolTip() {
        String text = "";
        if (getIntParam(PARAMS.LIGHT_EMISSION) != 0) {
            text += StringMaster.format("LIGHT_EMISSION - ")
                    + getParam(PARAMS.LIGHT_EMISSION);
        }
        if (getIntParam(PARAMS.ILLUMINATION) != 0) {
            text += StringMaster.format(", ILLUMINATION - ")
                    + getParam(PARAMS.ILLUMINATION);
        }
        if (getIntParam(PARAMS.CONCEALMENT) != 0) {
            text += StringMaster.format(", CONCEALMENT - ")
                    + getParam(PARAMS.CONCEALMENT);
        }

        if (!VisionHelper.checkDetected(this)) {
            return "?";
        }

        if (getGame().getGraveyardManager().checkForCorpses(this)) {
            return getGame().getGraveyardManager().getRipString(this)
                    + StringMaster.wrapInParenthesis(text);

        }
        return super.getToolTip() + StringMaster.wrapInParenthesis(text);
    }

    public void setRef(Ref ref) {
        super.setRef(ref);
        this.ref.setSource(id);
    }

    protected void addDynamicValues() {
        setParam(G_PARAMS.POS_X, x);
        setParam(G_PARAMS.POS_Y, y);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isPlayerDetected() {
        return isDetectedByPlayer();
    }

    public void setUnitVisionStatus(UNIT_VISION status, BattleFieldObject observer) {
        super.setUnitVisionStatus(status, observer);
        if (status == UNIT_VISION.IN_PLAIN_SIGHT) {
            playerHasSeen = true;
        }
    }

    @Override
    public Integer getGamma() {
        if (playerHasSeen)
            if (super.getGamma() == null)
                return null;
            else
                return super.getGamma() * 3 / 2 + 15;
        return super.getGamma();
    }

    public boolean isDetectedByPlayer() {
        boolean result = super.isDetectedByPlayer();
        if (result)
            playerHasSeen = true;
        return result;
    }

    public boolean isPlayerHasSeen() {
        return playerHasSeen;
    }

    public void setPlayerHasSeen(boolean playerHasSeen) {
        this.playerHasSeen = playerHasSeen;
    }

    public boolean isVOID() {
        return VOID;
    }

    public void setVOID(boolean VOID) {
        this.VOID = VOID;
    }

    public String getOverlayData() {
        return overlayData;
    }

    public void setOverlayData(String overlayData) {
        this.overlayData = overlayData;
    }

    public BattleFieldObject[] getObjects(Boolean overlayingIncluded_not_only) {
        if (overlayingIncluded_not_only == null) {
            return overlayingObjects;
        }
        return overlayingIncluded_not_only ? objects : nonOverlaying;
    }

    public void setObjects(BattleFieldObject[] objects,
                                          Boolean overlayingIncluded_not_only) {
        if (overlayingIncluded_not_only == null) {
           setOverlayingObjects(objects);
        } else
        if (overlayingIncluded_not_only) {
            setObjects(objects);
        } else {
            setNonOverlaying(objects);
        }
    }
    public void setOverlayingObjects(BattleFieldObject[] overlayingObjects) {
        this.overlayingObjects = overlayingObjects;
    }

    public void setObjects(BattleFieldObject[] objects) {
        this.objects = objects;
    }

    public void setNonOverlaying(BattleFieldObject[] nonOverlaying) {
        this.nonOverlaying = nonOverlaying;
    }

    public void resetObjectArrays() {
        setObjects(null);
        setOverlayingObjects(null);
        setNonOverlaying(null);
    }

    public boolean isObjectsModified() {
        return objectsModified;
    }

    public void setObjectsModified(boolean objectsModified) {
        this.objectsModified = objectsModified;
    }

    public boolean hasMark(MARK mark) {
        if (marks == null) {
            return false;
        }
        return marks.contains(mark);
    }
}
