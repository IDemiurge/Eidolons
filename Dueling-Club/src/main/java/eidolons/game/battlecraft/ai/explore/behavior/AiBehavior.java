package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 9/23/2017.
 */
public abstract class AiBehavior {

    protected LevelBlock block;
    protected AiMaster master;
    protected UnitAI ai;
    protected BEHAVIOR_STATUS status;
    protected float timer;
    protected Coordinates preferredPosition;
    DC_Obj target;
    BEHAVIOR_METHOD method;


    public AiBehavior(AiMaster master, UnitAI ai) {
        this.master = master;
        this.ai = ai;
        try {
            block = master.getGame().getDungeonMaster().getDungeonLevel().getBlockForCoordinate(
             ai.getUnit().getCoordinates());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    //    getConstraints() {
    //    }

    public void act(float delta) {
        timer += delta;
    }

    public boolean update() {
        if (!checkNeedsUpdate()) {
            timer = 0;
            status = BEHAVIOR_STATUS.WAITING;
            return false;
        }
        target = updateTarget();
        method = updateMethod();
        preferredPosition = updatePreferredPosition();
        boolean lost = checkLost();
        if (lost)
            if (checkCanTeleport()) {
                teleportToLeader();
            }

        status = BEHAVIOR_STATUS.RUNNING;
        initOrders();
        return true;
    }

    protected boolean checkNeedsUpdate() {
        if (isFollowOrAvoid() != isNearby()) {
            return true;
        }
        return isCustomActionRequired();
    }

    protected boolean isCustomActionRequired() {
        if (isFacingChangeRequired())
            return true;
        return false;
    }

    protected boolean isFacingChangeRequired() {
        if (getRequiredFacing() == null) {
            return false;
        }
        return getRequiredFacing() != ai.getUnit().getFacing();
    }

    protected FACING_DIRECTION getRequiredFacing() {
        return null;
    }

    protected abstract boolean isFollowOrAvoid();

    protected BEHAVIOR_METHOD updateMethod() {
        return method;
    }

    protected DC_Obj updateTarget() {
        if (target == null) {
            target = ai.getGroupAI().getLeader();
        }
        return target;
    }

    protected void initOrders() {
        ai.setStandingOrders(getOrders());
    }

    public ActionSequence getOrders() {
        //check facing change required

        ActionSequence orders = getCustomOrders();

        if (orders != null) {
            return orders;
        }

        List<Coordinates> targetCells = null;

        if (target instanceof BattleFieldObject) {
            targetCells = new ArrayList<>(
             target.getCoordinates().getAdjacentCoordinates());
        }
        orders = getMoveOrders(targetCells);
        return orders;
    }

    protected ActionSequence getCustomOrders() {
        ActionSequence orders = getFacingChangeOrders();

        if (orders != null) {
            return orders;
        }
        orders = getMethodOrders();
        return orders;
    }

    protected ActionSequence getFacingChangeOrders() {
        return
         new ActionSequence(master.getTurnSequenceConstructor().getTurnSequence(ai.getUnit(),
          ai.getUnit().getCoordinates().getAdjacentCoordinate(getRequiredFacing().getDirection())),
          ai);
    }

    protected ActionSequence getMethodOrders() {
        return null;
    }

    public ActionSequence getMoveOrders(List<Coordinates> validCells) {
        ActionPath path = master.getPathBuilder().getPathByPriority(validCells);
        ActionSequence sequence = master.getActionSequenceConstructor().getSequenceFromPath(path, ai);

        //        Order order= new Order();
        ai.setStandingOrders(sequence);
        return sequence;
    }

    public void teleportToLeader() {
        //yeah...
        ai.getUnit().setCoordinates(preferredPosition);
    }

    protected boolean isNearby() {
        double dst = PositionMaster.getExactDistance(ai.getUnit(), target);
        return dst <= getDistanceForNearby();

    }

    protected double getDistanceForNearby() {
        return 1;
    }

    protected boolean checkCanTeleport() {
        double dst = PositionMaster.getExactDistance(ai.getUnit(), Eidolons.getMainHero());
        if (dst < Eidolons.getMainHero().
         getMaxVisionDistanceTowards(ai.getUnit().getCoordinates())) {
            return false;
        }
        return true;
    }

    public boolean isPositionValid(Coordinates c) {
        return c.dst_(preferredPosition) <= getDistanceForNearby();
    }

    public Coordinates updatePreferredPosition() {
        if (target instanceof Unit) {
            DIRECTION d = ((Unit) target).getFacing().flip().getDirection();
            return target.getCoordinates().getAdjacentCoordinate(d);

        }
        return target.getCoordinates();
    }

    protected boolean checkLost() {
        return timer > getTimeBeforeFail();
    }

    protected float getTimeBeforeFail() {
        return 50;
    }

    protected AiMaster getMaster(UnitAI ai) {
        return ai.getUnit().getGame().getAiManager();
    }


    public enum BEHAVIOR_METHOD {
        WANDER,
        MARCH,
        SPRINT,
        SNEAK,
        SEARCH,

    }

    public enum BEHAVIOR_STATUS {
        WAITING,
        RUNNING,
        BLOCKED,

    }
}
