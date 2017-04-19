package main.libgdx.bf.mouse;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.entity.obj.BattleFieldObject;
import main.libgdx.anims.phased.PhaseAnimator;
import main.libgdx.bf.BaseView;
import main.libgdx.bf.GridCell;
import main.libgdx.bf.GridPanel;

import java.util.Map;

/**
 * Created by JustMe on 1/7/2017.
 */
public class GridMouseListener extends ClickListener {
    private GridPanel gridPanel;
    private GridCell[][] cells;
    private Map<BattleFieldObject, BaseView> unitViewMap;

    public GridMouseListener(GridPanel gridPanel, GridCell[][] cells, Map<BattleFieldObject, BaseView> unitViewMap) {
        this.gridPanel = gridPanel;
        this.cells = cells;
        this.unitViewMap = unitViewMap;
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        gridPanel.getStage().setScrollFocus(gridPanel);
        return false;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        Actor a;

        if (PhaseAnimator.getInstance().checkAnimClicked(x, y, pointer, button)) {
            return true;
        }
      /*  a = gridPanel.hitChildren(x, y, true);
        if (a != null && a instanceof GridCell) {
            GridCell cell = (GridCell) a;
            if (gridPanel.getCellBorderManager().isBlueBorderActive()
                    && event.getButton() == Input.Buttons.LEFT) {
                Borderable b = cell;
                if (cell.getInnerDrawable() != null) {
                    Actor unit = cell.getInnerDrawable().hit(x, y, true);
                    if (unit != null && unit instanceof Borderable) {
                        b = (Borderable) unit;
                    }
                }
                boolean selected = gridPanel.getCellBorderManager().hitAndCall(b);
                if (!selected) {
                    DC_Cell cellObj = Eidolons.game.getCellByCoordinate(new Coordinates(cell.getGridX(), cell.getGridY()));
                    cellObj.invokeClicked();
                    // selection cancel works this way, but....
                    //TODO  RADIAL SELECTIVE-NODE MUST ACTIVATE()
                    // ACTION IS NOT BEING ACTIVATED HERE YET!
//                  WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_BF_OBJ, cellObj.getId());
//                    cellObj.getGame().getManager().setSelecting(true);
//                    cellObj.getGame().getManager().objClicked(cellObj);
//
                }
            }
            event.stop();
            return true;
        }*/
        return false;
    }

}
