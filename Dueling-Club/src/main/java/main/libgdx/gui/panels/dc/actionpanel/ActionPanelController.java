package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import main.libgdx.gui.panels.dc.actionpanel.datasource.QuickSlotsDataSource;
import main.system.GuiEventManager;

import static main.system.GuiEventType.UPDATE_QUICK_SLOT_PANEL;

public class ActionPanelController extends Group {
    private QuickSlotPanel quickSlotPanel;

    public ActionPanelController() {
        quickSlotPanel = new QuickSlotPanel();
        quickSlotPanel.setPosition(70, -quickSlotPanel.getHeight());
        addActor(quickSlotPanel);
        initListeners();
    }

    private void initListeners() {
        GuiEventManager.bind(UPDATE_QUICK_SLOT_PANEL, obj -> {
            final QuickSlotsDataSource source = (QuickSlotsDataSource) obj.get();
            if (source != null) {
                if (quickSlotPanel.getY() < 0) {
                    final MoveToAction action = new MoveToAction();
                    action.setPosition(70, 0);
                    action.setDuration(2);
                    action.setInterpolation(Interpolation.exp5);
                    quickSlotPanel.addAction(action);
                }
                quickSlotPanel.setUserObject(source);
            } else {
                final MoveToAction action = new MoveToAction();
                action.setPosition(70, -quickSlotPanel.getHeight());
                action.setDuration(2);
                action.setInterpolation(Interpolation.exp5);
                quickSlotPanel.addAction(action);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
