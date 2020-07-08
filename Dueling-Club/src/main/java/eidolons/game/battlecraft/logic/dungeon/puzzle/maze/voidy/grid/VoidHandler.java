package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid;

import com.google.inject.internal.util.ImmutableList;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCell;
import main.content.CONTENT_CONSTS;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;

import java.util.*;
import java.util.stream.Collectors;

/*
cases:
1) raise N cells from X direction with S speed factor
> what to do with shards?
IDEA: perhaps we can have a flag that a cell is ‘movable’?
OR we can just fade that stuff... and create a map in case we revert
> void overlay?
2) Raise cells upon approach

3) Collapse cells
- in this case, shards and stuff become even less obvious
we could scale/fade of course but that’s not much of animation

IDEA: could have a flag on object that will tell us to raise cells when they are
adjacent, but otherwise such cells will again be collapsed

usage:
script function for this
unit flag

toggle cells on button

Ideas:
1) Reverse mechanic: cells in front of you just collapse! Void Field could be a thing then
but we will need something massive to happen there too, then. Mass collapse of cells...
Don't overdo it of course, it's just 2d squares

2) Squares behind you will collapse after 1s. If your square collapses, you’re dead
+ you must FIND a path, not make any that you wish. some paths will lead you to BAD places, but you will have no choice but to go there...

Animations
- overlay with some simple sprite, and that’s it. Fire, crack, hitAnim...
Micro-shake ...
Accelerating collapse



 */
public abstract class VoidHandler {

    public static boolean TEST_MODE = false;
    protected  GridPanel gridPanel;
    protected  Set<BattleFieldObject> autoRaise = new LinkedHashSet<>();
    protected final Map<GridCell, DIRECTION> raised = new LinkedHashMap<>();
    protected final Map<GridCell, DIRECTION> collapsed = new LinkedHashMap<>();
    protected float collapsePeriod;
    protected float collapseDelay;
    protected boolean canDropHero;
    protected boolean unmark;
    protected boolean raiseOneMax;
    protected final boolean collapseDown = true;
    protected VoidAnimator animator;


    public VoidHandler(DC_GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        animator = createAnimator();
    }

    protected abstract VoidAnimator createAnimator();

    public void toggleAuto() {
        toggleAuto(Eidolons.getMainHero());
    }

    public void toggleAutoOff(Unit obj) {
        autoRaise.remove(obj);
    }

    public void toggleAutoOn(Unit obj) {
        autoRaise.add(obj);
    }

    public void toggleAuto(Unit obj) {
        if (autoRaise.contains(obj)) {
            autoRaise.remove(obj);
        } else
            autoRaise.add(obj);

    }

    public void act(float delta) {
        if (isCollapsing()) {
            if (!raised.isEmpty()) {
                collapseDelay += delta;

                if (collapseDelay >= collapsePeriod) {
                    collapseDelay = 0;
                    GridCell gridCell = raised.keySet().iterator().next();
                    raised.keySet().remove(gridCell);
                    DC_Cell cell = gridCell.getUserObject();
                    if (canDropHero || !Eidolons.getPlayerCoordinates().equals(cell.getCoordinates())) {

                        Coordinates c = cell.getCoordinates();
                        toggle(false, c, ImmutableList.of(c), 1f, raised.get(cell));
                        if (unmark) {
                            cell.getMarks().remove(CONTENT_CONSTS.MARK.togglable);
                        }
                    }
                }
            }
        }
        if (TEST_MODE) {
            autoRaiseFor(Eidolons.getMainHero());
        } else
            for (BattleFieldObject object : autoRaise) {
                autoRaiseFor(object);
            }
    }

    public boolean isCollapsing() {
        return collapsePeriod != 0;
    }

    protected void autoRaiseFor(BattleFieldObject object) {
        DIRECTION direction = null;
        if (object instanceof Unit) {
            FACING_DIRECTION facing = object.getFacing();
            direction = facing.getDirection();
        }
        //TODO check diag adjacent
        if (!raiseOneMax) {
            checkRaise(object, direction);
            boolean clockwise = RandomWizard.random();
            checkRaise(object, direction.rotate45(clockwise));
            checkRaise(object, direction.rotate90(!clockwise));
            return;
        }
        if (!checkRaise(object, direction)) {
            boolean clockwise = RandomWizard.random();
            if (!checkRaise(object, direction.rotate45(clockwise))) {
                checkRaise(object, direction.rotate90(!clockwise));
            }
        }

    }

    protected boolean checkRaise(BattleFieldObject object, DIRECTION direction) {
        List<Coordinates> list = new ArrayList<>();
        Coordinates coordinate = object.getCoordinates().getAdjacentCoordinate(direction);
        if (coordinate == null) {
            return false;
        }
        DC_Cell cell = object.getGame().getCellByCoordinate(coordinate);
        if (TEST_MODE || checkMarked(cell))
            if (cell.isVOID()) {
                list.add(coordinate);
                toggle(true, object.getCoordinates(), list, 1f);
                return true;
            }
        return false;
    }


    protected boolean checkMarked(DC_Cell cell) {
        return cell.getMarks().contains(CONTENT_CONSTS.MARK.togglable);
    }

    public void toggle(boolean raiseOrCollapse, Coordinates origin,
                       List<Coordinates> coordinates, float speed, DIRECTION... forced) {
        //split into lines! diff speed
        for (DIRECTION direction : forced) {
            if (direction == null) {
                continue;
            }
            toggle(raiseOrCollapse, coordinates, speed, direction);
            return;
        }
        for (DIRECTION direction : DIRECTION.clockwise) {
            List<Coordinates> list = coordinates.stream().filter(c ->
                    isInLine(direction, origin, c)).sorted().collect(Collectors.toList());
            // coordinates.removeAll()
            toggle(raiseOrCollapse, list, speed, direction);
        }
    }

    protected boolean isInLine(DIRECTION direction, Coordinates origin, Coordinates c) {
        return DirectionMaster.getRelativeDirection(origin, c) == direction;
    }

    public void toggle(boolean raiseOrCollapse, List<Coordinates> coordinates, float speed, DIRECTION from) {
        // Collections.sort(coordinates, getComparator(origin, from, ));
        //IDEA - micro move action on a tangent? circle interpolation
        int period = 0;
        for (Coordinates coordinate : coordinates) {
            //ensure they are sorted!
            GridCell cell = gridPanel.getGridCell(coordinate);
            if (cell.getActions().size > 0) {
                continue; //TODO smarter check!
            }
            cell.getUserObject().setObjectsModified(true);
            period +=animator.getDelayBetweenAnims();
            // WaitMaster.WAIT(period);
            if (GdxMaster.isLwjglThread()) {
                animator.animate(period, raiseOrCollapse, cell, speed, from);
                Eidolons.onNonGdxThread(() -> onAnimate(cell));
            } else {
                int finalPeriod = period;
                Eidolons.onGdxThread(() ->  animator.animate(finalPeriod, raiseOrCollapse, cell, speed, from));
                onAnimate(cell);
            }
        }

    }

    protected abstract void onAnimate(GridCell cell);


    public void setCollapsePeriod(float collapsePeriod) {
        this.collapsePeriod = collapsePeriod;
    }

    public void setCanDropHero(boolean canDropHero) {
        this.canDropHero = canDropHero;
    }

    public void setUnmark(boolean unmark) {
        this.unmark = unmark;
    }

    public void setRaiseOneMax(boolean raiseOneMax) {
        this.raiseOneMax = raiseOneMax;
    }

    public void collapseAll() {
        LinkedHashMap<DIRECTION, List<Coordinates>> map = new LinkedHashMap<>();
        for (GridCell gridCell : raised.keySet()) {
            MapMaster.addToListMap(map, raised.get(gridCell), gridCell.getUserObject().getCoordinates());
        }
        for (DIRECTION direction : map.keySet()) {
            toggle(false, map.get(direction),
                    2f, direction);
        }
    }

    public GridPanel getGridPanel() {
        return gridPanel;
    }

    public boolean isLogged() {
        return false;
    }

    public abstract void cleanUp();
}
