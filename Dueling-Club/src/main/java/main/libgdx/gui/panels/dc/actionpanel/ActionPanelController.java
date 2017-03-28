package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.libgdx.gui.panels.dc.actionpanel.datasource.QuickSlotsDataSource;
import main.system.GuiEventManager;

import static main.system.GuiEventType.UPDATE_QUICK_SLOT_PANEL;

public class ActionPanelController extends Group {
    private QuickSlotPanel quickSlotPanel;

    public ActionPanelController() {
        quickSlotPanel = new QuickSlotPanel();
        quickSlotPanel.setPosition(70, -64);
        addActor(quickSlotPanel);
        initListeners();
    }

    private void initListeners() {
        GuiEventManager.bind(UPDATE_QUICK_SLOT_PANEL, obj -> {
            final QuickSlotsDataSource source = (QuickSlotsDataSource) obj.get();
            if (source != null) {
                if (quickSlotPanel.getY() < 0) {
                    quickSlotPanel.setY(0);
                }
                quickSlotPanel.setUserObject(source);
            } else {
                quickSlotPanel.setY(-64);
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
