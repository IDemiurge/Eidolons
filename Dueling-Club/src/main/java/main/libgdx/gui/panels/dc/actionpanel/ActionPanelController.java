package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.libgdx.anims.ActorMaster;
import main.libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;
import main.system.GuiEventManager;

import static main.system.GuiEventType.UPDATE_QUICK_SLOT_PANEL;

public class ActionPanelController extends Group {
    private final static int IMAGE_SIZE = 60;
    private final LeftOrbPanel leftOrbPanel;
    private QuickSlotPanel quickSlotPanel;
    private ModeActionsPanel modeActionsPanel;
    private SpellPanel spellPanel;
    private EffectsPanel effectsPanel;
    private RigthOrbPanel rigthOrbPanel;

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

        leftOrbPanel = new LeftOrbPanel();
        leftOrbPanel.setPosition(quickSlotOffset + 76, IMAGE_SIZE);
        addActor(leftOrbPanel);

        rigthOrbPanel = new RigthOrbPanel();
        rigthOrbPanel.setPosition(spellOffset - 29, IMAGE_SIZE);
        addActor(rigthOrbPanel);


        setY(-IMAGE_SIZE);

        initListeners();
    }

    public ActionPanelController(int x, int y) {
        this();
        setPosition(x, y);
    }

    private void initListeners() {
        GuiEventManager.bind(UPDATE_QUICK_SLOT_PANEL, obj -> {
            final ActiveQuickSlotsDataSource source = (ActiveQuickSlotsDataSource) obj.get();
            if (source != null) {
                if (getY() < 0) {
                    ActorMaster.addMoveToAction(this, getX(), 0, 1);
                    ActorMaster.addFadeInOrOut(leftOrbPanel,1);
                    ActorMaster.addFadeInOrOut(rigthOrbPanel,1);
                }
                quickSlotPanel.setUserObject(source);
                modeActionsPanel.setUserObject(source);
                spellPanel.setUserObject(source);
                effectsPanel.setUserObject(source);
                leftOrbPanel.setUserObject(source);
                rigthOrbPanel.setUserObject(source);
            } else {
//                setY(-IMAGE_SIZE);
                ActorMaster.addMoveToAction(this, getX(), -IMAGE_SIZE, 1);
                ActorMaster.addFadeInOrOut(leftOrbPanel,1);
                ActorMaster.addFadeInOrOut(rigthOrbPanel,1);
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
