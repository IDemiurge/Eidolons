package eidolons.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;
import eidolons.libgdx.gui.panels.dc.actionpanel.facing.FacingPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.WeaponDataSource;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static main.system.GuiEventType.BOTTOM_PANEL_UPDATE;

public class ActionPanel extends Group {
    public final static int IMAGE_SIZE = 60;
    private static final String BACKGROUND = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "background.png");
    private static final float SPELL_OFFSET_Y = -12;
    private static final String ORB_OVERLAY = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "overlay.png");;
    private final ImageContainer orbOverlay;
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
        final int quickSlotOffset = 70;
        quickSlotPanel.setPosition(quickSlotOffset, SPELL_OFFSET_Y);
        addActor(quickSlotPanel);

        final int actionOffset = quickSlotOffset + (IMAGE_SIZE * 6) + 5;
        addActor(modeActionsPanel = new ModeActionsPanel(IMAGE_SIZE));
        modeActionsPanel.setPosition(actionOffset,  0);


        spellPanel = new SpellPanel(IMAGE_SIZE);
        final int spellOffset = actionOffset + (IMAGE_SIZE * 6) + 5;
        spellPanel.setPosition(spellOffset,  SPELL_OFFSET_Y);
        addActor(spellPanel);

        effectsPanel = new EffectsPanel();
        effectsPanel.setPosition(actionOffset+88, IMAGE_SIZE+12);
        addActor(effectsPanel);

        leftOrbPanel = new OrbsPanel(PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.STAMINA);
        leftOrbPanel.setPosition(quickSlotOffset + 76, IMAGE_SIZE);
        addActor(leftOrbPanel);

        rigthOrbPanel = new OrbsPanel(PARAMS.MORALE, PARAMS.ESSENCE, PARAMS.FOCUS);
        rigthOrbPanel.setPosition(spellOffset + 29
         , IMAGE_SIZE);
        addActor(rigthOrbPanel);

        addActor(mainHand = new QuickWeaponPanel(false));
        addActor(offhand = new QuickWeaponPanel(true));
        addActor(facingPanel = new FacingPanel());


        mainHand.setPosition(rigthOrbPanel.getX() - 146,
         leftOrbPanel.getY()+12);
        offhand.setPosition(leftOrbPanel.getX() + 272,
         leftOrbPanel.getY()+12);

        facingPanel.setPosition((mainHand.getX() + offhand.getX()) / 2 + 12,
         leftOrbPanel.getY() + 32);

        addActor(orbOverlay = new ImageContainer(ORB_OVERLAY));
        orbOverlay.setPosition(136, 56);

        setY(-IMAGE_SIZE);
        bindEvents();
        initListeners();
    }

    public ActionPanel(int x, int y) {
        this();
        setPosition(x, y);
    }

    private void bindEvents() {
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
        });
    }

    protected void initListeners() {
        GuiEventManager.bind(BOTTOM_PANEL_UPDATE, obj -> {
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
         leftOrbPanel.getY()+12);
        offhand.setPosition(leftOrbPanel.getX() + 272,
         leftOrbPanel.getY()+12);

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
