package eidolons.puzzle.voidy;

import eidolons.entity.obj.GridCell;
import libgdx.bf.grid.DC_GridPanel;
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
    protected boolean checkMarked(GridCell cell) {
        return cell.getMarks().contains(CONTENT_CONSTS.MARK._void)
                || cell.getMarks().contains(CONTENT_CONSTS.MARK.togglable);
    }
}
