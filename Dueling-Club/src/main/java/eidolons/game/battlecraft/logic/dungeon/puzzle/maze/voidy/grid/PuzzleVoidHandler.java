package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCell;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.system.threading.WaitMaster;

public class PuzzleVoidHandler extends VoidHandler{

    public PuzzleVoidHandler(DC_GridPanel gridPanel) {
        super(gridPanel);
    }

    @Override
    protected VoidAnimator createAnimator() {
        return new VoidAnimator(this);
    }

    @Override
    protected void onAnimate(GridCell cell) {
        WaitMaster.WAIT(500);
        Ref ref= Ref.getSelfTargetingRefCopy(Eidolons.getMainHero() );
        cell.getUserObject().getGame().getDungeonMaster().getPuzzleMaster().processEvent(
                //this is kind of a hack
                new Event(Event.STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE,
                        ref
                ));

    }

}
