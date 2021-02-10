package eidolons.libgdx.bf.grid.moving;

import main.game.bf.Coordinates;
import main.system.data.DataUnit;
import main.system.math.PositionMaster;

import static main.system.auxiliary.log.LogMaster.log;

public abstract class MoveController {
    float maxSpeed;
    float minSpeed;
    float acceleration;
    Coordinates origin;
    Coordinates coordinates;
    Coordinates destination; //what about BLOCKS?

    boolean pendulum = true;
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
    String name;
    DataUnit data;

    public MoveController(DataUnit data) {
        this.data = data;
    }

    protected void init() {
        name = data.getValue("name");
        maxSpeed = data.getIntValue("max_speed");
        minSpeed = data.getIntValue("min_speed");
        acceleration = data.getIntValue("acceleration");
        waitPeriod = data.getIntValue("waitPeriod");
        destination = Coordinates.get(data.getValue("destination"));
        angle = PositionMaster.getAngle(origin, destination);
        accelerationX = Math.round((float) (acceleration * Math.cos(Math.toRadians(angle))));
        accelerationY = Math.round((float) (acceleration * Math.sin(Math.toRadians(angle))));


    }

    public boolean act(float delta) {
        if (paused || !powered) {
            return false;
        }
        if (waiting) {
            waitTimer += delta;
            if (waitTimer >= waitPeriod) {
                waiting = false;
                resumed();
            }
            return false;
        }
        //reverse acceleration when past middle? or will interpolation rule after all?
        if (!isInterpolated())
            if (reverse) {
                speedY -= accelerationY * delta;
                speedX -= accelerationX * delta;
            } else {
                speedY += accelerationY * delta;
                speedX += accelerationX * delta;
            }

        if (!canArrive()) {
            return true;
        }
        if (isArrived()) {
            log(1, getName() + " arrived at " + destination);
            arrived();
            return false;
        }
        Coordinates c = calcCurrent();
        if (!c.equals(coordinates)) {
            coordinates = c;
            coordinatesChanged();

        }
        return true;
    }

    protected void resumed() {

    }

    public String getName() {
        return name;
    }

    protected abstract boolean canArrive();

    protected abstract Coordinates calcCurrent();

    protected abstract void coordinatesChanged();

    protected abstract void arrived();

    protected abstract boolean isArrived();

    protected abstract boolean isInterpolated();

    public Coordinates getOrigin() {
        return origin;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Coordinates getDestination() {
        return destination;
    }

    public DataUnit getData() {
        return data;
    }
}
