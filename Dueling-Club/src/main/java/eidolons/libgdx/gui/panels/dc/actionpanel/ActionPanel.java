package eidolons.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import eidolons.libgdx.gui.panels.dc.actionpanel.facing.FacingPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.WeaponDataSource;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static main.system.GuiEventType.ACTION_PANEL_UPDATE;

public class ActionPanel extends Group {
    public final static int IMAGE_SIZE = 60;
    private static final String BACKGROUND = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "background.png");
    private static final float SPELL_OFFSET_Y = -6;
    private static final float OFFSET_X = 70;
    private static final float QUICK_SLOTS_OFFSET_X = 20;
    private static final float SPELL_OFFSET_X = 40;
    private static final float ORB_OFFSET_X = OFFSET_X+76;
    private static final String ORB_OVERLAY = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "overlay.png");
    private static final String BOTTOM_OVERLAY = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "bottom overlay.png");
    private final ImageContainer orbOverlay;
    private final ImageContainer bottomOverlay;
    protected OrbsPanel leftOrbPanel;
    protected OrbsPanel rigthOrbPanel;
    protected QuickSlotPanel quickSlotPanel;
    protected ModeActionsPanel modeActionsPanel;
    protected SpellPanel spellPanel;
    protected EffectsPanel effectsPanel;
    protected Image background;
    QuickWeaponPanel mainHand;
    QuickWeaponPanel offhand;
    FacingPanel facingPanel;

    public ActionPanel() {
        background = new Image(TextureCache.getOrCreateR(BACKGROUND));
        addActor(background);
        quickSlotPanel = new QuickSlotPanel(IMAGE_SIZE);

        quickSlotPanel.setPosition(OFFSET_X+QUICK_SLOTS_OFFSET_X, SPELL_OFFSET_Y);
        addActor(quickSlotPanel);

        final float actionOffset = OFFSET_X + (IMAGE_SIZE * 6) + 5;
        addActor(modeActionsPanel = new ModeActionsPanel(IMAGE_SIZE));
        modeActionsPanel.setPosition(actionOffset, 0);


        spellPanel = new SpellPanel(IMAGE_SIZE);
        final float spellOffset =SPELL_OFFSET_X+ actionOffset + (IMAGE_SIZE * 6) + 5;
        spellPanel.setPosition(spellOffset, SPELL_OFFSET_Y);
        addActor(spellPanel);


        leftOrbPanel = new OrbsPanel(PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.STAMINA);
        leftOrbPanel.setPosition(ORB_OFFSET_X, IMAGE_SIZE);
        addActor(leftOrbPanel);

        rigthOrbPanel = new OrbsPanel(PARAMS.MORALE, PARAMS.ESSENCE, PARAMS.FOCUS);
        rigthOrbPanel.setPosition(spellOffset -11
         , IMAGE_SIZE);
        addActor(rigthOrbPanel);

        addActor(facingPanel = new FacingPanel());
        addActor(mainHand = new QuickWeaponPanel(false));
        addActor(offhand = new QuickWeaponPanel(true));


        mainHand.setPosition(rigthOrbPanel.getX() - 146,
         leftOrbPanel.getY() + 12);
        offhand.setPosition(leftOrbPanel.getX() + 272,
         leftOrbPanel.getY() + 12);

        facingPanel.setPosition((mainHand.getX() + offhand.getX()) / 2 + 12,
         leftOrbPanel.getY() + 32);

        addActor(orbOverlay = new ImageContainer(ORB_OVERLAY));
        orbOverlay.setPosition(136, 56);

        addActor(bottomOverlay = new ImageContainer(BOTTOM_OVERLAY));
        bottomOverlay.setPosition(80, -12);

        effectsPanel = new EffectsPanel();
        effectsPanel.setPosition(actionOffset + 88, IMAGE_SIZE + 12);
        addActor(effectsPanel);

        setY(-IMAGE_SIZE);
        bindEvents();
        initListeners();
    }

    public ActionPanel(int x, int y) {
        this();
        setPosition(x, y);
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.ACTION_HOVERED_OFF, p -> {
            ActorMaster.addMoveToAction(bottomOverlay, bottomOverlay.getX(), -9, 0.4f);
//            spellPanel.setZIndex(1);
//            quickSlotPanel.setZIndex(1);
//            modeActionsPanel.setZIndex(1);
        });
        GuiEventManager.bind(GuiEventType.ACTION_HOVERED, p -> {
            ActorMaster.addMoveToAction(bottomOverlay, bottomOverlay.getX(), -15, 0.4f);
//            spellPanel.setZIndex(Integer.MAX_VALUE);
//            quickSlotPanel.setZIndex(Integer.MAX_VALUE);
//            modeActionsPanel.setZIndex(Integer.MAX_VALUE);
        });
        GuiEventManager.bind(GuiEventType.UPDATE_MAIN_HERO, p -> {
            Unit hero = (Unit) p.get();
            mainHand.setUserObject(new ImmutablePair<>(
             new WeaponDataSource(hero.getActiveWeapon(false)),
             new WeaponDataSource(hero.getNaturalWeapon())
            ));
            offhand.setUserObject(new ImmutablePair<>(
             new WeaponDataSource(hero.getActiveWeapon(true)),
             new WeaponDataSource(hero.getOffhandNaturalWeapon())
            ));
            GuiEventManager.trigger(ACTION_PANEL_UPDATE,
             new PanelActionsDataSource( hero));

        });
    }

    protected void initListeners() {
        GuiEventManager.bind(ACTION_PANEL_UPDATE, obj -> {
            final ActiveQuickSlotsDataSource source = (ActiveQuickSlotsDataSource) obj.get();
            if (source != null) {
                ActionValueContainer.setDarkened(false);
                if (getY() < 0) {
                    if (isMovedDownOnEnemyTurn())
                        ActorMaster.addMoveToAction(this, getX(), 0, 1);
                    //  ActorMaster.addFadeInOrOut(leftOrbPanel, 1);
                    //    ActorMaster.addFadeInOrOut(rigthOrbPanel, 1);
                }
                quickSlotPanel.setUserObject(source);
                modeActionsPanel.setUserObject(source);
                spellPanel.setUserObject(source);
                effectsPanel.setUserObject(source);
                leftOrbPanel.setUserObject(source);
                rigthOrbPanel.setUserObject(source);
            } else {
//                setY(-IMAGE_SIZE);
                if (isMovedDownOnEnemyTurn())
                    ActorMaster.addMoveToAction(this, getX(), -IMAGE_SIZE, 1);

                ActionValueContainer.setDarkened(true);
                // ActorMaster.addFadeInOrOut(leftOrbPanel, 1);
                // ActorMaster.addFadeInOrOut(rigthOrbPanel, 1);
            }
        });
    }

    private boolean isMovedDownOnEnemyTurn() {
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        mainHand.setPosition(rigthOrbPanel.getX() - 146,
         leftOrbPanel.getY() + 12);
        offhand.setPosition(leftOrbPanel.getX() + 272,
         leftOrbPanel.getY() + 12);

        facingPanel.setPosition((mainHand.getX() + offhand.getX()) / 2 + 12,
         leftOrbPanel.getY() + 32);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public QuickSlotPanel getQuickSlotPanel() {
        return quickSlotPanel;
    }

    public ModeActionsPanel getModeActionsPanel() {
        return modeActionsPanel;
    }

    public SpellPanel getSpellPanel() {
        return spellPanel;
    }

    public EffectsPanel getEffectsPanel() {
        return effectsPanel;
    }

    public void update() {
        leftOrbPanel.setUpdateRequired(true);
        rigthOrbPanel.setUpdateRequired(true);
    }
}
