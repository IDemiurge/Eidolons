package eidolons.entity.obj;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.game.DC_Game;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.Cell;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.StringMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;

public class DC_Cell extends DC_Obj implements Cell {

    private static ObjType EMPTY_CELL_TYPE;
    private boolean playerHasSeen;

    public DC_Cell(ObjType t, int i, int j, DC_Game game, Ref ref, Dungeon dungeon) {
        super(t, Player.NEUTRAL, game, ref);
        this.x = i;
        this.y = j;

        if (dungeon != null) {
            setZ(dungeon.getZ());
        }
        addDynamicValues();
        setImage(ImageManager.getEmptyCellPath(GuiManager.getBfCellsVersion()));
    }

    public DC_Cell(int i, int j, DC_Game game, Ref ref, Dungeon dungeon) {
        this(getEMPTY_CELL_TYPE(), i, j, game, ref, dungeon);
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
             "terrain");
        return EMPTY_CELL_TYPE;
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

    public DIRECTION getBorderSide() {
        if (getX() + 1 == GuiManager.getCurrentLevelCellsX()) {
            if (getY() + 1 == GuiManager.getCurrentLevelCellsY()) {
                return DIRECTION.DOWN_RIGHT;
            }
            if (getY() == 0) {
                return DIRECTION.UP_RIGHT;
            }
            return DIRECTION.RIGHT;
        }
        if (getY() + 1 == GuiManager.getCurrentLevelCellsY()) {
            if (getX() == 0) {
                return DIRECTION.DOWN_LEFT;
            }
            if (getX() + 1 == GuiManager.getCurrentLevelCellsX()) {
                return DIRECTION.DOWN_RIGHT;
            }
            return DIRECTION.DOWN;
        }
        if (getY() == 0) {
            if (getX() == 0) {
                return DIRECTION.UP_LEFT;
            }
            if (getX() + 1 == GuiManager.getCurrentLevelCellsX()) {
                return DIRECTION.UP_RIGHT;
            }
            return DIRECTION.UP;
        }
        if (getX() == 0) {
            if (getY() + 1 == GuiManager.getCurrentLevelCellsY()) {
                return DIRECTION.DOWN_LEFT;
            }
            if (getY() == 0) {
                return DIRECTION.UP_LEFT;
            }
            return DIRECTION.LEFT;
        }
        return null;
    }

    public boolean isBorderCell() {
        return getBorderSide() != null;
    }

    public void setProperty(PROPERTY prop, String value) {
        super.setProperty(prop, value);
    }

    public void toBase() {
        super.toBase();
        name = getProp("Name")
         + StringMaster.wrapInParenthesis(StringMaster
         .getWellFormattedString(getProperty(PROPS.VISIBILITY_STATUS)));
    }

    public String getToolTip() {
        String text = "";
        if (getIntParam(PARAMS.LIGHT_EMISSION) != 0) {
            text += StringMaster.getWellFormattedString("LIGHT_EMISSION - ")
             + getParam(PARAMS.LIGHT_EMISSION);
        }
        if (getIntParam(PARAMS.ILLUMINATION) != 0) {
            text += StringMaster.getWellFormattedString(", ILLUMINATION - ")
             + getParam(PARAMS.ILLUMINATION);
        }
        if (getIntParam(PARAMS.CONCEALMENT) != 0) {
            text += StringMaster.getWellFormattedString(", CONCEALMENT - ")
             + getParam(PARAMS.CONCEALMENT);
        }

        if (!VisionManager.checkDetected(this)) {
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
}
