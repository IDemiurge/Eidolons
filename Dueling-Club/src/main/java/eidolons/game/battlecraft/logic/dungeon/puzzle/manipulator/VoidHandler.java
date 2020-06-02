package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCell;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;

import java.util.List;
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
 */
public class VoidHandler {

    GridPanel gridPanel;

    public VoidHandler(GridPanel gridPanel) {
        this.gridPanel = gridPanel;
    }

    public void toggle(boolean raiseOrCollapse, Coordinates origin,
                       List<Coordinates> coordinates, float speed) {
        //split into lines! diff speed

        for (DIRECTION direction : DIRECTION.clockwise) {
            List<Coordinates> list = coordinates.stream().filter(c ->
                    isInLine(direction, origin, c)).sorted().collect(Collectors.toList());
            toggle(raiseOrCollapse, list, speed, direction);
        }
    }

    private boolean isInLine(DIRECTION direction, Coordinates origin, Coordinates c) {
        return DirectionMaster.getRelativeDirection(origin, c) == direction;
    }

    public void toggle(boolean raiseOrCollapse,List<Coordinates> coordinates, float speed, DIRECTION from) {
        // Collections.sort(coordinates, getComparator(origin, from, ));
        //IDEA - micro move action on a tangent? circle interpolation
        int period = 0;
        for (Coordinates coordinate : coordinates) {
            //ensure they are sorted!
            GridCell cell = gridPanel.getGridCell(coordinate);
              period += 0.2f;
            // WaitMaster.WAIT(period);
            animate(period, raiseOrCollapse, cell, speed, from);
        }

    }

    private void animateRaise(float waitPeriod, GridCell cell, float speed, DIRECTION from) {
        animate(waitPeriod, true, cell, speed, from);
    }

    private void animateCollapse(float waitPeriod, GridCell cell, float speed, DIRECTION from) {
        animate(waitPeriod, false, cell, speed, from);
    }

    private void animate(float waitPeriod, boolean raiseOrCollapse, GridCell cell, float speed, DIRECTION from) {
        float dur = 1 * speed;
        float x = cell.getX();
        float y = cell.getY();

        // float x1 = x - (scale - actor.getScaleX()) * actor.getWidth() / 2;
        // float y1 = y - (scale - actor.getScaleY()) * actor.getHeight() / 2;
        //TODO scale centered
        float offsetX = 0;
        float offsetY = 0;
        // if (!raiseOrCollapse)
        //     from = from.flip();
        //cell will move in a way to 'arrive' at where we are raising 'from'
        if (from.growY != null) {
            offsetY = from.growY ? -64 : 64;
        }
        if (from.growX != null) {
            offsetX = from.growX ? -64 : 64;
        }

        float scale = raiseOrCollapse ? 0.1f : 1f;
        if (raiseOrCollapse) {
            cell.setPosition(x + offsetX, y + offsetY);
        } else
            cell.setPosition(x, y);
        cell.setScale(scale);

        ActionMaster.addWaitAction(cell, waitPeriod);
        ActionMaster.addAlphaAction(cell, dur, !raiseOrCollapse);
        scale = raiseOrCollapse ? 1f : 0.1f;
        ActionMaster.addScaleAction(cell, scale, dur);
        if (raiseOrCollapse) {
            ActionMaster.addMoveToAction(cell, x, y, dur);
        } else
            ActionMaster.addMoveToAction(cell, x + offsetX, y + offsetY, dur);

        ActionMaster.addAfter(cell, () -> cell.getUserObject().setVOID(!raiseOrCollapse));
    }
}
