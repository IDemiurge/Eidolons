package eidolons.libgdx.bf.boss;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import main.content.enums.rules.VisionEnums;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.battle.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * facing
 * coordinates
 * reach
 *
 */
public class BossUnit extends Unit {
    int left=1;
    int right=1;
    int top=1 ;
    int bot=1;
    List<Coordinates> zone;
    private float height;
    private float width;


    public BossUnit(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
        height= top+bot+1;
        width= left+right+1;
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
        zone =CoordinatesMaster.getCoordinatesWithin(c.x-left, c.x + right, c.y - top, c.y + bot, true);

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
} //TRUE_SIGHT
