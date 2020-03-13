package eidolons.game.netherflame.boss_.logic.entity;

import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
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
    int left = 1;
    int right = 1;
    int top = 1;
    int bot = 1;
    List<Coordinates> zone;
    private float height;
    private float width;


    public BossUnit(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
        height = top + bot + 1;
        width = left + right + 1;
    }

    @Override
    public FACING_DIRECTION getFacingOrNull() {
        return super.getFacingOrNull();
    }

    @Override
    public FACING_DIRECTION getFacing() {
        return FACING_DIRECTION.NONE;
    }

    public boolean isBoss() {
        return true;
    }

    @Override
    public Coordinates getCoordinates() {
        return CoordinatesMaster.getClosestTo(Eidolons.getMainHero().getCoordinates(), zone);
    }

    public void setCoordinates(Coordinates coordinates) {
        originalCoordinates = coordinates;
        Coordinates c = getOriginalCoordinates();
        zone = CoordinatesMaster.getCoordinatesWithin(c.x - left, c.x + right, c.y - top, c.y + bot, true);

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
    }

    @Override
    protected EntityMaster initMaster() {
        return new BossMaster(this);
    }

    @Override
    public VisionEnums.VISION_MODE getVisionMode() {
        return super.getVisionMode();
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getTop() {
        return top;
    }

    public int getBot() {
        return bot;
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
        return true;
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
