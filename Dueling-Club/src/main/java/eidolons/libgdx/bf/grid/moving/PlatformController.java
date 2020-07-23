package eidolons.libgdx.bf.grid.moving;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.GridMaster;
import eidolons.system.math.DC_PositionMaster;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.data.DataUnit;
import main.system.launch.CoreEngine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static main.system.auxiliary.log.LogMaster.log;

public class PlatformController extends MoveController {

    private static final boolean TEST = true;
    private final PlatformDecor visuals;
    List<PlatformCell> cells;
    PlatformCell tip;
    Runnable onCycle; //script
    private float destX, destY, originX, originY, period, time;
    Interpolation interpolation = Interpolation.sine;
    private Coordinates block;
    private boolean blocked;

    public PlatformController(PlatformData data, List<PlatformCell> cells, PlatformDecor visuals) {
        super(data);
        this.cells = cells;
        this.visuals = visuals;
        init();
        for (PlatformCell cell : cells) {
            cell.setController(this);
        }
    }


    @Override
    protected void init() {
        Set<Coordinates> coordinatesSet = cells.stream().map(cell
                -> cell.getUserObject().getCoordinates()).collect(Collectors.toSet());
        Map<Coordinates, PlatformCell> map = new LinkedHashMap<>();
        for (PlatformCell cell : cells) {
            map.put(cell.getUserObject().getCoordinates(), cell);
        }
        origin = CoordinatesMaster.getCenterCoordinate(coordinatesSet);
        super.init();
        coordinates = origin;
        //TODO gotta be the farthest one OUT!
        initDestination();
        Vector2 v1 = GridMaster.getVectorForCoordinate(origin);
        originX = v1.x;
        originY = v1.y;
        period = data.getFloatValue(PlatformData.PLATFORM_VALUE.time);

        if (cells.size() == 1) {
            tip = cells.get(0);
        } else {
            DIRECTION d = DirectionMaster.getRelativeDirection(origin, destination);
            tip = map.get(CoordinatesMaster.getCorner(d, coordinatesSet));
        }
        visuals.setPosition(tip.getX(), tip.getY());
        if (CoreEngine.TEST_LAUNCH) {
            period/=2;
        }
    }


    public boolean act(float delta) {
        if (!super.act(delta))
            return false;

        time += delta;
        // log(LOG_CHANNEL.PLATFORM, this + " tip at " + tip.getX() + " " + tip.getY() + " ;time= " + time);
        for (PlatformCell cell : cells) {
            if (isInterpolated()) {
                int offsetX = cell.getOriginalX() - tip.getOriginalX();
                int offsetY = cell.getOriginalY() - tip.getOriginalY();
                if (reverse) {
                    float x = interpolation.apply(destX, originX, time / period);
                    cell.setX(x + offsetX * 128);
                    float y = interpolation.apply(destY, originY, time / period);
                    cell.setY(y + offsetY * 128);
                } else {
                    float x = interpolation.apply(originX, destX, time / period);
                    cell.setX(x + offsetX * 128);
                    float y = interpolation.apply(originY, destY, time / period);
                    cell.setY(y + offsetY * 128);
                }

            } else {
                cell.setX(cell.getX() + speedX * delta);
                cell.setY(cell.getY() + speedY * delta);
            }
            visuals.setX(tip.getX());
            visuals.setY(tip.getY());
        }

        return true;
    }

    public void initDestination(Coordinates destination) {
        Vector2 v = GridMaster.getVectorForCoordinate(destination);
        destX = v.x;
        destY = v.y;
    }

    public void initDestination() {
        List<Coordinates> line = DC_PositionMaster.getLine(origin, destination);

        for (Coordinates c : line) {
            if (isBlocked(c)) {
                block = c;
                initDestination(c);
                blocked = true;
                interpolation = Interpolation.bounce;
                return;
            }
        }
        interpolation = Interpolation.sine;
        initDestination(destination);
    }

    private boolean isBlocked(Coordinates c) {
        CellScriptData cellScriptData = DC_Game.game.getDungeonMaster().getFloorWrapper().getTextDataMap().get(c);
        if (cellScriptData != null) {
            return !cellScriptData.getValue(CellScriptData.CELL_SCRIPT_VALUE.platform_block).isEmpty();
        }
        return false;
    }

    protected void arrived() {
        log(LOG_CHANNEL.PLATFORM,this + " arrived! Cur: " + coordinates);
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

        visuals.arrived();
    }

    public void setDestination(Coordinates dest) {
        destination = dest;
        initDestination();

    }


    public void left(BattleFieldObject userObject) {
        if (CoreEngine.TEST_LAUNCH) {
            powered = false;
        }
        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.PLATFORM,userObject + " left " + this);
        GuiEventManager.trigger(GuiEventType.CAMERA_FOLLOW_OFF);
        visuals.left();
    }

    public void entered(BattleFieldObject userObject) {
        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.PLATFORM,userObject + " entered " + this);
        powered = true;
        waitTimer = 0.3f;
        GuiEventManager.trigger(GuiEventType.CAMERA_FOLLOW_MAIN);
        visuals.entered();
    }

    @Override
    protected void resumed() {
        visuals. resumed();
    }

    @Override
    protected Coordinates calcCurrent() {
        return GridMaster.getClosestCoordinate(tip.getX(), tip.getY());
    }

    @Override
    protected void coordinatesChanged() {
        log(LOG_CHANNEL.PLATFORM,getName() + " is at " + coordinates);
        for (PlatformCell cell : cells) {
            DC_Cell dc_cell = DC_Game.game.getCellByCoordinate(coordinates.getOffset(
                    cell.getOriginalX() - tip.getOriginalX(),
                    cell.getOriginalY() - tip.getOriginalY()));
            cell.setUserObject(dc_cell);
        }
    }

    @Override
    protected boolean canArrive() {
        return true;
    }

    @Override
    protected boolean isArrived() {
        return time >= period;
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
                " to " + destination +
                "] in " + period + "sec.";
    }

    public PlatformCell getTip() {
        return tip;
    }

    public boolean contains(Coordinates c) {
        for (PlatformCell cell : cells) {
            if (cell.getUserObject().getCoordinates().equals(c)) {
                return true;
            }
        }
        return false;
    }

    public void toggle() {
        powered = !powered;
    }
}
