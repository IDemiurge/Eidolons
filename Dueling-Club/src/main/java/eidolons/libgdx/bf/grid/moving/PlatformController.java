package eidolons.libgdx.bf.grid.moving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.grid.moving.PlatformData.PLATFORM_VALUE;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.math.PositionMaster;

import java.util.List;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

public class PlatformController {

    private static final boolean TEST = true;
    List<PlatformCell> cells;
    PlatformCell tip;
    String name; //find in scripts
    PlatformData data;
    Runnable onCycle; //script

    float maxSpeed;
    float minSpeed;
    float acceleration;
    Coordinates origin;
    Coordinates coordinates;
    Coordinates destination; //what about BLOCKS?

    boolean pendulum=true;
    boolean waiting;
    float waitPeriod;
    float waitTimer;
    boolean paused;
    boolean powered;

    float speedX;
    float accelerationX;
    float speedY;
    float accelerationY;
    float angle;
    boolean reverse;


    public PlatformController(PlatformData data, List<PlatformCell> cells) {
        this.data = data;
        this.cells = cells;
        init();
    }

    private void init() {
        name = data.getValue(PLATFORM_VALUE.name);
        maxSpeed = data.getIntValue(PLATFORM_VALUE.max_speed);
        minSpeed = data.getIntValue(PLATFORM_VALUE.min_speed);
        acceleration = data.getIntValue(PLATFORM_VALUE.acceleration);
        waitPeriod = data.getIntValue(PLATFORM_VALUE.waitPeriod);
        origin = CoordinatesMaster.getCenterCoordinate(cells.stream().map(cell
                -> cell.getUserObject().getCoordinates()).collect(Collectors.toSet()));
        coordinates = origin;
        destination = Coordinates.get(data.getValue(PLATFORM_VALUE.destination));
        angle = PositionMaster.getAngle(origin, destination);
        accelerationX = Math.round((float) (acceleration * Math.cos(Math.toRadians(angle))));
        accelerationY = Math.round((float) (acceleration * Math.sin(Math.toRadians(angle))));
        tip = cells.get(0);
    }

    public void act(float delta) {
        if (TEST) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                powered = !powered;
            }
        }
        if (paused || !powered) {
            return;
        }
        if (waiting) {
            waitTimer += delta;
            if (waitTimer >= waitPeriod) {
                waiting = false;
            }
            return;
        }
        //reverse acceleration when past middle? or will interpolation rule after all?

        if (reverse) {
            speedY -= accelerationY * delta;
            speedX -= accelerationX * delta;
        } else {
            speedY += accelerationY * delta;
            speedX += accelerationX * delta;
        }
        //if we know distance we can calc point of slowdown
        // but then BLOCK... ah, it will just set dest.

        for (PlatformCell cell : cells) {
            cell.setX(cell.getX() + speedX * delta);
            cell.setY(cell.getY() + speedY * delta);
            // cell.updatePlatform();

        }
        Coordinates c = calcCurrent(tip.getX(), tip.getY());
        if (!c.equals(coordinates)) {
            coordinates=c;
            log(1, getName() + " is at " + c);
            for (PlatformCell cell : cells) {
                Coordinates offset = Coordinates.get(
                        cell.getX() - tip.getX(),
                        cell.getY() - tip.getY());
                DC_Cell dc_cell = DC_Game.game.getCellByCoordinate(coordinates.getOffset(offset));
                cell.setUserObject(dc_cell);
            }

            //single check for all!
            int targetX = destination.x;
            int targetY = destination.y;
            if (reverse){
                targetX = origin.x;
                targetY = origin.y;
            }
            if (tip.getGridX() == targetX && tip.getGridY() == targetY) {
                log(1, getName() + " arrived at " + destination);
                arrived();
            }
        }
    }

    private Coordinates calcCurrent(float x, float y) {
        return GridMaster.getClosestCoordinate(x, y);
    }

    public void left(BattleFieldObject userObject) {

    }

    public void entered(BattleFieldObject userObject) {
        powered = true;
    }

    private void arrived() {
        if (pendulum) {
            waitTimer = waitPeriod;
            waiting = true;
            reverse = !reverse;
            speedY=0;
            speedX=0;
        }

    }

    public String getName() {
        return name;
    }

    public PlatformData getData() {
        return data;
    }

    public boolean canEnter(Entity unit) {
        return true;
    }
}
