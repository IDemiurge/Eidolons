package main.level_editor.gui.grid;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.bf.grid.OverlayView;
import eidolons.libgdx.bf.grid.UnitViewFactory;
import eidolons.libgdx.bf.grid.UnitViewOptions;
import main.level_editor.LevelEditor;

public class LE_UnitViewFactory extends UnitViewFactory {

    @Override
    public ClickListener createListener(BattleFieldObject bfObj) {
        return new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                LevelEditor.getCurrent().getManager().getMouseHandler().handleObjectClick(event,getTapCount(), bfObj);
            }
        };
    }

    @Override
    public GridUnitView create(BattleFieldObject battleFieldObject) {
        return super.create(battleFieldObject);
    }

    @Override
    public OverlayView createOverlay(BattleFieldObject battleFieldObject) {
        return super.createOverlay(battleFieldObject);
    }

    @Override
    protected void addLastSeenView(BattleFieldObject bfObj, GridUnitView view, UnitViewOptions options) {

    }

    @Override
    protected void addOutline(BattleFieldObject bfObj, GridUnitView view, UnitViewOptions options) {
    }

    @Override
    protected void addForDC(BattleFieldObject bfObj, GridUnitView view, UnitViewOptions options) {
    }

    @Override
    protected boolean isGridObjRequired(BattleFieldObject bfObj) {
        return super.isGridObjRequired(bfObj);
    }
}
