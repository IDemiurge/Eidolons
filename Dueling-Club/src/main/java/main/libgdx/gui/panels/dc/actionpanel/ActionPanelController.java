package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.libgdx.gui.panels.dc.actionpanel.datasource.QuickSlotsDataSource;
import main.system.GuiEventManager;

import static main.system.GuiEventType.UPDATE_QUICK_SLOT_PANEL;

public class ActionPanelController extends Group {
    private QuickSlotPanel quickSlotPanel;
    private ActionModPanel actionModPanel;
    private SpellPanel spellPanel;
    private EffectsPanel effectsPanel;

    public ActionPanelController() {
        quickSlotPanel = new QuickSlotPanel();
        final int quickSlotOffset = 70;
        quickSlotPanel.setPosition(quickSlotOffset, -64);
        addActor(quickSlotPanel);

        final int actionOffset = quickSlotOffset + (64 * 6) + 5;
        actionModPanel = new ActionModPanel();
        actionModPanel.setPosition(actionOffset, -64);
        addActor(actionModPanel);

        spellPanel = new SpellPanel();
        final int spellOffset = actionOffset + (64 * 6) + 5;
        spellPanel.setPosition(spellOffset, -64);
        addActor(spellPanel);

        effectsPanel = new EffectsPanel();
        effectsPanel.setPosition(actionOffset, 0);
        addActor(effectsPanel);

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

                if (actionModPanel.getY() < 0) {
                    actionModPanel.setY(0);
                }
                actionModPanel.setUserObject(source);

                if (spellPanel.getY() < 0) {
                    spellPanel.setY(0);
                }
                spellPanel.setUserObject(source);

                if (effectsPanel.getY() < 64) {
                    effectsPanel.setY(64);
                }
                effectsPanel.setUserObject(source);
            } else {
                quickSlotPanel.setY(-64);
                actionModPanel.setY(-64);
                spellPanel.setY(-64);
                effectsPanel.setY(0);
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
