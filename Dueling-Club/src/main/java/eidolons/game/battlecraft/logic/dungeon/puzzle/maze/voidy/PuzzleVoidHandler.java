package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.bf.grid.cell.GridCell;
import main.entity.Ref;
import main.game.logic.event.Event;

public class PuzzleVoidHandler extends VoidHandler{

    public PuzzleVoidHandler(DC_GridPanel gridPanel) {
        super(gridPanel);
    }

    @Override
    protected void onAnimate(GridCell cell) {
        Ref ref= Eidolons.getMainHero().getRef();
        cell.getUserObject().getGame().getDungeonMaster().getPuzzleMaster().processEvent(
                //this is kind of a hack
                new Event(Event.STANDARD_EVENT_TYPE.UNIT_ACTION_COMPLETE,
                        ref
                ));
    }

    @Override
    protected boolean isVertScale() {
        return false;
    }

    @Override
    protected boolean isScaleOn() {
        return false;
    }
}
