package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid;

import eidolons.entity.obj.DC_Cell;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import main.content.CONTENT_CONSTS;

public class DefVoidHandler extends VoidHandler{

    public DefVoidHandler(DC_GridPanel gridPanel) {
        super(gridPanel);
    }

    @Override
    protected boolean isMainHeroMode() {
        return true;
    }

    @Override
    protected boolean checkMarked(DC_Cell cell) {
        return cell.getMarks().contains(CONTENT_CONSTS.MARK._void)
                || cell.getMarks().contains(CONTENT_CONSTS.MARK.togglable);
    }
}
