package libgdx.gui.panels.dc.unitinfo.datasource;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import main.ability.AbilityObj;
import main.content.OBJ_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.entity.DataModel;
import main.entity.obj.ActiveObj;
import main.entity.obj.BfObj;
import main.game.bf.Coordinates;

import javax.swing.*;
import java.util.List;

/**
 * Created by JustMe on 11/22/2017.
 */
public class UnitDataModelSnapshot extends DataModel implements BfObj {

    protected ImageIcon customIcon;
    protected ImageIcon icon;
    private boolean unconscious;
    private boolean mine;
    private int x;
    private int y;
    private boolean playerDetected;
    private PLAYER_VISION activePlayerVisionStatus;
    private UNIT_VISION unitVisionStatus;
    private Coordinates coordinates;
    private boolean dead;


    public UnitDataModelSnapshot(BattleFieldObject obj) {
        cloneMaps(obj);
        x = obj.getX();
        y = obj.getY();
        this.playerDetected = obj.isPlayerDetected();
        this.activePlayerVisionStatus = obj.getActivePlayerVisionStatus();
//        this.unitVisionStatus = obj.getUnitVisionStatus();
        this.coordinates = obj.getCoordinates();
        mine = obj.isMine();
        dead = obj.isDead();
        if (obj instanceof Unit) {
            unconscious = ((Unit) obj).isUnconscious();

        }
    }

    public boolean isUnconscious() {
        return unconscious;
    }

    @Override
    public boolean isMine() {
        return mine;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {

    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {

    }

    @Override
    public UNIT_VISION getUnitVisionStatus() {
        return unitVisionStatus;
    }

    @Override
    public PLAYER_VISION getActivePlayerVisionStatus() {
        return activePlayerVisionStatus;
    }

    @Override
    public boolean isPlayerDetected() {
        return playerDetected;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public String getOBJ_TYPE() {
        return null;
    }

    @Override
    public OBJ_TYPE getOBJ_TYPE_ENUM() {
        return null;
    }

    @Override
    public void toBase() {

    }

    @Override
    public void init() {

    }

    @Override
    public List<ActiveObj> getActives() {
        return null;
    }

    @Override
    public List<AbilityObj> getPassives() {
        return null;
    }

    @Override
    public void construct() {

    }

    @Override
    public void run() {

    }
}
