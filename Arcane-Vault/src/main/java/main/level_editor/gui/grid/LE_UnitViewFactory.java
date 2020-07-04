package main.level_editor.gui.grid;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.grid.cell.OverlayView;
import eidolons.libgdx.bf.grid.cell.UnitGridView;
import eidolons.libgdx.bf.grid.cell.UnitViewFactory;
import eidolons.libgdx.bf.grid.cell.UnitViewOptions;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.level_editor.LevelEditor;

public class LE_UnitViewFactory extends UnitViewFactory {

    private static LE_UnitViewFactory instance;

    public static LE_UnitViewFactory getInstance() {
        if (instance == null) {
            instance = new LE_UnitViewFactory();
        }
        return instance;
    }

    @Override
    public ClickListener createListener(BattleFieldObject bfObj) {
        return new ClickListener(-1) {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                InputEvent e = new InputEvent();
                e.setButton(event.getButton());
                Eidolons.onNonGdxThread(() -> LevelEditor.getCurrent().
                        getManager().getMouseHandler().handleObjectClick(e, getTapCount(), bfObj));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                 super.touchDown(event, x, y, pointer, button);
                 event.stop();
                 return true;
            }
        };
    }

    @Override
    public void addOverlayingListener(OverlayView view, BattleFieldObject bfObj) {
        view.addListener(createListener(bfObj));
    }

    public static UnitGridView doCreate(BattleFieldObject battleFieldObject) {
        return getInstance().create(battleFieldObject);
    }

    @Override
    public UnitGridView create(BattleFieldObject battleFieldObject) {
        return super.create(battleFieldObject);
    }


    protected UnitGridView createView(BattleFieldObject bfObj, UnitViewOptions options) {
        return
                        new LE_UnitView(bfObj, options);
    }
    @Override
    protected void addLastSeenView(BattleFieldObject bfObj, UnitGridView view, UnitViewOptions options) {

    }

    @Override
    protected void addOutline(BattleFieldObject bfObj, UnitGridView view, UnitViewOptions options) {
    }

    @Override
    protected void addForDC(BattleFieldObject bfObj, UnitGridView view, UnitViewOptions options) {
        view.setToolTip(new ValueTooltip(bfObj.getName()));
    }

    @Override
    protected boolean isGridObjRequired(BattleFieldObject bfObj) {
        return super.isGridObjRequired(bfObj);
    }
}
