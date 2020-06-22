package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import com.google.inject.internal.util.ImmutableList;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCell;
import eidolons.system.audio.DC_SoundMaster;
import main.content.CONTENT_CONSTS;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;
import main.system.sound.SoundMaster;

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
    GridPanel gridPanel;
    Set<BattleFieldObject> autoRaise = new LinkedHashSet<>();
    private final Map<GridCell, DIRECTION> raised = new LinkedHashMap<>();
    private final Map<GridCell, DIRECTION> collapsed = new LinkedHashMap<>();
    private float collapsePeriod;
    private float collapseDelay;
    private boolean canDropHero;
    private boolean unmark;
    private boolean raiseOneMax;
    private final boolean collapseDown = true;

    public VoidHandler(DC_GridPanel gridPanel) {
        this.gridPanel = gridPanel;
    }

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
        if (collapsePeriod != 0) {
            if (!raised.isEmpty()) {
                collapseDelay -= delta;

                if (collapseDelay <= 0) {
                    collapseDelay = collapsePeriod;
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

    private void autoRaiseFor(BattleFieldObject object) {
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

    private boolean checkRaise(BattleFieldObject object, DIRECTION direction) {
        List<Coordinates> list = new ArrayList<>();
        Coordinates coordinate = object.getCoordinates().getAdjacentCoordinate(direction);
        DC_Cell cell = object.getGame().getCellByCoordinate(coordinate);
        if (TEST_MODE || checkMarked(cell))
            if (cell.isVOID()) {
                list.add(coordinate);
                toggle(true, object.getCoordinates(), list, 1f);
                return true;
            }
        return false;
    }


    private boolean checkMarked(DC_Cell cell) {
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

    private boolean isInLine(DIRECTION direction, Coordinates origin, Coordinates c) {
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
            period += getDelayBetweenAnims();
            // WaitMaster.WAIT(period);
            animate(period, raiseOrCollapse, cell, speed, from);
            Eidolons.onNonGdxThread(() -> onAnimate(cell));
        }

    }

    private float getDelayBetweenAnims() {
        return 0.2f;
    }

    protected abstract void onAnimate(GridCell cell);

    private void animateRaise(float waitPeriod, GridCell cell, float speed, DIRECTION from) {
        animate(waitPeriod, true, cell, speed, from);
    }

    private void animateCollapse(float waitPeriod, GridCell cell, float speed, DIRECTION from) {
        animate(waitPeriod, false, cell, speed, from);
    }

    private void animate(float waitPeriod, boolean raiseOrCollapse, GridCell cell, float speed, DIRECTION from) {
        if (isDisableGhostsAfterAnim())
            cell.setVoidAnimHappened(true);
        //TODO fade void shadecell overlay!
        float dur = 1 * speed;
        float x = cell.getGridX() * 128;
        float y =
                gridPanel.getGdxY_ForModule(cell.getGridY()) * 128;
        // float x1 = x - (scale - actor.getScaleX()) * actor.getWidth() / 2;
        // float y1 = y - (scale - actor.getScaleY()) * actor.getHeight() / 2;
        //TODO IDEA: Maybe scaling from SLICE could look nice? basically, scaling only on one axis. Depending on facing...
        float offsetX = 0;
        float offsetY = 0;
        if (!raiseOrCollapse) {
            from = from.flip();
            if (collapseDown) {
                if (from.growX == null) {
                    from = DIRECTION.DOWN;
                } else {
                    from = from.growX ? DIRECTION.DOWN_RIGHT : DIRECTION.DOWN_LEFT;
                }
            }
        }
        //cell will move in a way to 'arrive' at where we are raising 'from'
        if (from.growY != null) {
            offsetY = from.growY ? -64 : 64;
        }
        if (from.growX != null) {
            offsetX = from.growX ? -64 : 64;
        }

        float scale = 1f;
        float scaleX;
        float scaleY;
        if (isScaleOn()) {
            scale = raiseOrCollapse ? 0.01f : 1;
            scaleX = isVertScale() ? 0.01f : scale;
            scaleY = scale;
            cell.setScale(scaleX, scaleY);
        }

        if (raiseOrCollapse) {
            cell.setPosition(x + offsetX, y + offsetY);
        } else
            cell.setPosition(x, y);
        cell.getBackImage().setVisible(true);
        if (raiseOrCollapse) {
            cell.getColor().a = 0;
        }

        ActionMaster.addWaitAction(cell, waitPeriod);
        ActionMaster.addCustomAction(cell, () -> playAnimSound(raiseOrCollapse));

        ActionMaster.addAlphaAction(cell, dur, !raiseOrCollapse);

        if (isScaleOn()) {
            scale = raiseOrCollapse ? 1f : 0.01f;
            scaleX =  isVertScale() ? 100*scale : scale;
            scaleY = scale;
            ActionMaster.addScaleActionCentered(cell.getBackImage(), scaleX, scaleY, dur + waitPeriod);
        }
        if (raiseOrCollapse) {
            ActionMaster.addMoveToAction(cell, x, y, dur);
        } else
            ActionMaster.addMoveToAction(cell, x + offsetX, y + offsetY, dur);

        DIRECTION direction = from;
        ActionMaster.addAfter(cell, () -> {

            cell.getUserObject().setVOID(!raiseOrCollapse);
            if (raiseOrCollapse) {
                raised.put(cell, direction);
                collapsed.remove(cell);
            } else {
                collapsed.put(cell, direction);
                raised.remove(cell);
                cell.setPosition(x, y);
            }
        });
    }

    protected abstract boolean isVertScale();

    protected abstract boolean isScaleOn();

    protected void playAnimSound(boolean raiseOrCollapse) {
        DC_SoundMaster.playStandardSound(raiseOrCollapse ?
                SoundMaster.STD_SOUNDS.NEW__TAB
                : SoundMaster.STD_SOUNDS.CHAIN);
    }

    protected boolean isDisableGhostsAfterAnim() {
        return false;
    }

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
}
