package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.libgdx.gui.panels.dc.actionpanel.datasource.QuickSlotsDataSource;
import main.system.GuiEventManager;

import static main.system.GuiEventType.UPDATE_QUICK_SLOT_PANEL;

public class ActionPanelController extends Group {
    private final static int IMAGE_SIZE = 60;
    private QuickSlotPanel quickSlotPanel;
    private ModeActionsPanel modeActionsPanel;
    private SpellPanel spellPanel;
    private EffectsPanel effectsPanel;

    public ActionPanelController() {
        quickSlotPanel = new QuickSlotPanel(IMAGE_SIZE);
        final int quickSlotOffset = 70;
        quickSlotPanel.setPosition(quickSlotOffset, 0);
        addActor(quickSlotPanel);

        final int actionOffset = quickSlotOffset + (IMAGE_SIZE * 6) + 5;
        modeActionsPanel = new ModeActionsPanel(IMAGE_SIZE);
        modeActionsPanel.setPosition(actionOffset, 0);
        addActor(modeActionsPanel);

        spellPanel = new SpellPanel(IMAGE_SIZE);
        final int spellOffset = actionOffset + (IMAGE_SIZE * 6) + 5;
        spellPanel.setPosition(spellOffset, 0);
        addActor(spellPanel);

        effectsPanel = new EffectsPanel();
        effectsPanel.setPosition(actionOffset, IMAGE_SIZE);
        addActor(effectsPanel);

        setY(-IMAGE_SIZE);

        initListeners();
    }

    private void initListeners() {
        GuiEventManager.bind(UPDATE_QUICK_SLOT_PANEL, obj -> {
            final QuickSlotsDataSource source = (QuickSlotsDataSource) obj.get();
            if (source != null) {
                if (getY() < 0) {
                    setY(0);
                }
                quickSlotPanel.setUserObject(source);
                modeActionsPanel.setUserObject(source);
                spellPanel.setUserObject(source);
                effectsPanel.setUserObject(source);
            } else {
                setY(-IMAGE_SIZE);
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