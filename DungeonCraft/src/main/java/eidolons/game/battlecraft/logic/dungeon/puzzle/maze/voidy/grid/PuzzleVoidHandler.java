package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid;

import eidolons.game.core.Eidolons;
import libgdx.bf.grid.DC_GridPanel;
import libgdx.bf.grid.cell.GridCell;
import main.entity.Ref;
import main.game.logic.event.Event;

public class PuzzleVoidHandler extends VoidHandler {

    public PuzzleVoidHandler(DC_GridPanel gridPanel) {
        super(gridPanel);
    }


    @Override
    protected void onAnimate(GridCell cell) {
        // WaitMaster.WAIT(200);
        Ref ref = Ref.getSelfTargetingRefCopy(Eidolons.getMainHero());
        cell.getUserObject().getGame().getDungeonMaster().getPuzzleMaster().processEvent(
                //this is kind of a hack
                new Event(Event.STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE,
                        ref
                ));

    }

    @Override
    public void cleanUp() {
        collapseAll();
        setCollapsePeriod(0);
        animator.cleanUp();
    }
}
