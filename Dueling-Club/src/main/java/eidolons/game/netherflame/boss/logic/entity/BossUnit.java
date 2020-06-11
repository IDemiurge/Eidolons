package eidolons.game.netherflame.boss.logic.entity;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.logic.BossCycle;
import main.content.enums.rules.VisionEnums;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.obj.ActiveObj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.battle.player.Player;

import java.util.List;

/**
 * facing
 * coordinates
 * reach
 */
public class BossUnit extends Unit {
    BossCycle.BOSS_TYPE bossType;
    BossManager manager;
    List<Coordinates> zone;
    private final float height;
    private final float width;


    public BossUnit(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
        width= getIntParam(PARAMS.GRID_WIDTH);
        height= getIntParam(PARAMS.GRID_HEIGHT);
        // bossType= new EnumMaster<ENUM>().retrieveEnumConst(ENUM.class, string )
    }

    public void init(BossManager manager){
        this.manager = manager;
        ((BossMaster) master).setManager(manager);
        //w h

    }

    @Override
    public void newRound() {
        super.newRound();
        // manager.getRoundRules().roundStarts();
    }

    @Override
    protected EntityMaster initMaster() {
        return new BossMaster(this);
    }
    @Override
    public FACING_DIRECTION getFacingOrNull() {
        return FACING_DIRECTION.SOUTH;
    }

    @Override
    public FACING_DIRECTION getFacing() {
        return FACING_DIRECTION.SOUTH;
    }

    public boolean isBoss() {
        return true;
    }

    @Override
    public Coordinates getCoordinates() {
        // return CoordinatesMaster.getClosestTo(Eidolons.getMainHero().getCoordinates(), zone);
        return super.getCoordinates();
    }

    public void setCoordinates(Coordinates coordinates) {
        originalCoordinates = coordinates;
        Coordinates c = getOriginalCoordinates();
        super.setCoordinates(coordinates);
        // zone = CoordinatesMaster.getCoordinatesWithin(c.x - left, c.x + right, c.y - top, c.y + bot, true);
    }

    @Override
    public DC_UnitAction getAction(String name) {
        for (ActiveObj active : getActives()) {
            if (active.getName().equalsIgnoreCase(name)) {
                return (DC_UnitAction) active;
            }
        }
        return null ;
    }

    @Override
    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
        return super.kill(killer, leaveCorpse, quietly);
    }

    @Override
    public void setDead(boolean dead) {
        super.setDead(dead);
        if (dead){

        }
    }


    @Override
    public VisionEnums.VISION_MODE getVisionMode() {
        return super.getVisionMode();
    }


    @Override
    public BossMaster getMaster() {
        return (BossMaster) super.getMaster();
    }
    public List<Coordinates> getZone() {
        return zone;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    // VISION   - same AGAINST him? can always target?
    @Override
    public VisionEnums.VISIBILITY_LEVEL getVisibilityLevelForPlayer() {
        return VisionEnums.VISIBILITY_LEVEL.CLEAR_SIGHT;
    }

    @Override
    public boolean isDetectedByPlayer() {
        // return manager.getVisionRules().isDetected(this, type);
        return false;
    }

    @Override
    public VisionEnums.PLAYER_VISION getPlayerVisionStatus() {
        return VisionEnums.PLAYER_VISION.DETECTED;
    }


    @Override
    public VisionEnums.OUTLINE_TYPE getOutlineTypeForPlayer() {
        return null;
    }
//TRUE_SIGHT
}
