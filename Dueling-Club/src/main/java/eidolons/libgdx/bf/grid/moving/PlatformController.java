package eidolons.libgdx.bf.grid.moving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.GridMaster;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.data.DataUnit;
import main.system.launch.CoreEngine;

import java.util.List;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

public class PlatformController extends MoveController {

    private static final boolean TEST = true;
    List<PlatformCell> cells;
    PlatformCell tip;
    Runnable onCycle; //script
    private float destX, destY, originX, originY, period, time;

    public PlatformController(PlatformData data, List<PlatformCell> cells) {
        super(data);
        this.cells = cells;
        init();
        for (PlatformCell cell : cells) {
            cell.setController(this);
        }
    }

    Interpolation interpolation = Interpolation.sine;


    @Override
    protected void init() {
        origin = CoordinatesMaster.getCenterCoordinate(cells.stream().map(cell
                -> cell.getUserObject().getCoordinates()).collect(Collectors.toSet()));
        super.init();
        coordinates = origin;
        tip = cells.get(0);

        Vector2 v = GridMaster.getVectorForCoordinate(destination);
        Vector2 v1 = GridMaster.getVectorForCoordinate(origin);
        destX = v.x;
        destY = v.y;
        originX = v1.x;
        originY = v1.y;
        period = data.getFloatValue(PlatformData.PLATFORM_VALUE.time);
    }

    public boolean act(float delta) {
        if (TEST) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                powered = !powered;
                pendulum=!pendulum;
                waiting=false;
            }
        }
        //if we know distance we can calc point of slowdown
        // but then BLOCK... ah, it will just set dest.
        if (!super.act(delta))
            return false;

        time += delta;
        log(1, this + " tip at " + tip.getX() + " " + tip.getY()+ " ;time= " + time);
        for (PlatformCell cell : cells) {
            if (isInterpolated()) {
                if (reverse) {
                    float x = interpolation.apply(destX, originX, time / period);
                    cell.setX(x);
                    float y = interpolation.apply(destY, originY, time / period);
                    cell.setY(y);
                } else {
                    float x = interpolation.apply(originX, destX, time / period);
                    cell.setX(x);
                    float y = interpolation.apply(originY, destY, time / period);
                    cell.setY(y);
                }

                return true;
            }
            cell.setX(cell.getX() + speedX * delta);
            cell.setY(cell.getY() + speedY * delta);

        }

        return true;
    }

    protected void arrived() {
        log(1, this + " arrived! Cur: " + coordinates);
        if (isInterpolated()) {
            time = 0;
        }
        if (pendulum) {
            waitTimer = 0;
            waiting = true;
            reverse = !reverse;
            speedY = 0;
            speedX = 0;

        }

    }

    @Override
    protected boolean canArrive() {
        return true;
    }

    @Override
    protected Coordinates calcCurrent() {
        return GridMaster.getClosestCoordinate(tip.getX(), tip.getY());
    }

    @Override
    protected void coordinatesChanged() {
        log(1, getName() + " is at " + coordinates);
        for (PlatformCell cell : cells) {
            Coordinates offset = Coordinates.get(
                    cell.getX() - tip.getX(),
                    cell.getY() - tip.getY());
            DC_Cell dc_cell = DC_Game.game.getCellByCoordinate(coordinates.getOffset(offset));
            cell.setUserObject(dc_cell);
        }
    }

    public void left(BattleFieldObject userObject) {
        if (CoreEngine.TEST_LAUNCH) {
            powered=false;
        }
        main.system.auxiliary.log.LogMaster.log(1, userObject + " left " + this);
        GuiEventManager.trigger(GuiEventType.CAMERA_FOLLOW_OFF);
    }

    public void entered(BattleFieldObject userObject) {
        main.system.auxiliary.log.LogMaster.log(1, userObject + " entered " + this);
        powered = true;
        waitTimer= 0.3f;
        GuiEventManager.trigger(GuiEventType.CAMERA_FOLLOW_MAIN);
    }


    @Override
    protected boolean isArrived() {
        if (isInterpolated())
            return time >= period;

        int targetX = destination.x;
        int targetY = destination.y;
        if (reverse) {
            targetX = origin.x;
            targetY = origin.y;
        }
        //TODO check possible SKIP OVER!
        return tip.getGridX() == targetX && tip.getGridY() == targetY;
    }

    @Override
    protected boolean isInterpolated() {
        return true;
    }

    public DataUnit getData() {
        return data;
    }

    public boolean canEnter(Entity unit) {
        return true;
    }

    @Override
    public String toString() {
        return name + " [from " + origin +
                " to " + destination+
                "] in " + period + "sec.";
    }

    public PlatformCell getTip() {
        return tip;
    }
}
