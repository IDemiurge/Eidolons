package eidolons.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import eidolons.libgdx.gui.panels.dc.actionpanel.facing.FacingPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.WeaponDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.launch.CoreEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.ACTION_PANEL_UPDATE;

public class ActionPanel extends GroupX {
    public final static int IMAGE_SIZE = 60;
    public static final float EMPTY_OFFSET = 110;
    private static final String BACKGROUND_PATH = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "background.png");
    private static final String BACKGROUND_PATH_ALT = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "background alt.png");
    private static final float SPELL_OFFSET_Y = -6;
    private static final float OFFSET_X = 70 + EMPTY_OFFSET;
    private static final float QUICK_SLOTS_OFFSET_X = 20;
    private static final float SPELL_OFFSET_X = 40;
    private static final float ORB_OFFSET_X = OFFSET_X + 76;
    private static final String ORB_OVERLAY = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "overlay.png");
    private static final String BOTTOM_OVERLAY = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "bottom overlay.png");
    private final ImageContainer orbOverlay;
    private final ImageContainer bottomOverlay;
    private final Vector2 spellsPos;
    protected OrbsPanel leftOrbPanel;
    protected OrbsPanel rigthOrbPanel;
    protected QuickSlotPanel quickSlotPanel;
    protected ModeActionsPanel modeActionsPanel;
    protected SpellPanel spellPanel;
    protected BuffPanelSimple buffPanelSimple;
    protected FadeImageContainer background;
    QuickWeaponPanel mainHand;
    QuickWeaponPanel offhand;
    FacingPanel facingPanel;

    SymbolButton spellbookBtn = new SymbolButton(STD_BUTTON.SPELLBOOK, () -> showSpellbook());
    SymbolButton invBtn = new SymbolButton(STD_BUTTON.INV, () -> showInventory());
    private boolean altBg;
    private Float defaultX;

    public ActionPanel() {
        background = new FadeImageContainer((BACKGROUND_PATH));
//        background.pack();
        setSize(background.getWidth(), background.getHeight());
        addActor(background);
        quickSlotPanel = new QuickSlotPanel(IMAGE_SIZE);

        quickSlotPanel.setPosition(OFFSET_X + QUICK_SLOTS_OFFSET_X, SPELL_OFFSET_Y);
        addActor(quickSlotPanel);

        final float actionOffset = OFFSET_X + (IMAGE_SIZE * 6) + 5;
        addActor(modeActionsPanel = new ModeActionsPanel(IMAGE_SIZE));
        modeActionsPanel.setPosition(actionOffset, 0);


        spellPanel = new SpellPanel(IMAGE_SIZE);
        final float spellOffset = SPELL_OFFSET_X + actionOffset + (IMAGE_SIZE * 6) + 5;
        spellPanel.setPosition(spellOffset, SPELL_OFFSET_Y);
        spellsPos = new Vector2(spellPanel.getX(), spellPanel.getY());
        addActor(spellPanel);


        leftOrbPanel = new OrbsPanel(PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.STAMINA);
        leftOrbPanel.setPosition(ORB_OFFSET_X, IMAGE_SIZE);
        addActor(leftOrbPanel);

        rigthOrbPanel = new OrbsPanel(PARAMS.MORALE, PARAMS.ESSENCE, PARAMS.FOCUS);
        rigthOrbPanel.setPosition(spellOffset - 11
                , IMAGE_SIZE);
        addActor(rigthOrbPanel);

        addActor(facingPanel = new FacingPanel());
        addActor(mainHand = new QuickWeaponPanel(false));
        addActor(offhand = new QuickWeaponPanel(true));


        mainHand.setPosition(rigthOrbPanel.getX() - 146,
                leftOrbPanel.getY() + 12);
        offhand.setPosition(leftOrbPanel.getX() + 272,
                leftOrbPanel.getY() + 12);

        facingPanel.setPosition((mainHand.getX() + offhand.getX()) / 2 + 7,
                leftOrbPanel.getY() + 32);

        addActor(orbOverlay = new ImageContainer(ORB_OVERLAY));
        orbOverlay.setPosition(EMPTY_OFFSET + 136, 56);

        addActor(bottomOverlay = new ImageContainer(BOTTOM_OVERLAY));
        bottomOverlay.setPosition(EMPTY_OFFSET + 80, -12);

        buffPanelSimple = new BuffPanelSimple();
        buffPanelSimple.setPosition(actionOffset + 88, IMAGE_SIZE + 12);
        addActor(buffPanelSimple);

        addActor(spellbookBtn);
        addActor(invBtn);
        spellbookBtn.setPosition(modeActionsPanel.getX() + IMAGE_SIZE * 6 - 12,
                2);
        invBtn.setPosition(modeActionsPanel.getX() - 58,
                2);

        setY(-IMAGE_SIZE);
        bindEvents();
        initListeners();

        initResolutionScaling();

    }

    public ActionPanel(int x, int y) {
        this();
        setPosition(x, y);
    }

    @Override
    public float getWidth() {
        if (background == null)
            return super.getWidth();
        return background.getWidth();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
    }

    private void showSpellbook() {
        if (DC_Game.game != null)
            if (DC_Game.game.isStarted()) {
                HqMaster.openHqPanel();
                HqMaster.tab("Spells");
            }
    }

    private void showInventory() {

        if (DC_Game.game != null)
            if (DC_Game.game.isStarted())
                Eidolons.activateMainHeroAction(DC_ActionManager.USE_INVENTORY);
    }

    @Override
    public void setX(float x) {
        if (defaultX == null) {
            defaultX = x;
        }
        super.setX(x);
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.LOG_ROLLED_IN, p -> {
            ActionMaster.addMoveToAction(this, GdxMaster.centerWidth(this), getY(), 1.4f);
        });
        GuiEventManager.bind(GuiEventType.LOG_ROLLED_OUT, p -> {
            ActionMaster.addMoveToAction(this, defaultX, getY(), 1.4f);
        });

        GuiEventManager.bind(GuiEventType.PUZZLE_STARTED, p -> {
            ActionMaster.addMoveToAction(this, getX(), -64, 1.4f);
        });
        GuiEventManager.bind(GuiEventType.PUZZLE_FINISHED, p -> {
            ActionMaster.addMoveToAction(this, getX(), 0, 1.4f);
        });

        GuiEventManager.bind(GuiEventType.ACTION_HOVERED_OFF, p -> {
            ActionMaster.addMoveToAction(bottomOverlay, bottomOverlay.getX(), -9, 0.4f);
        });
        GuiEventManager.bind(GuiEventType.ACTION_HOVERED, p -> {
            ActionMaster.addMoveToAction(bottomOverlay, bottomOverlay.getX(), -15, 0.4f);
        });
        GuiEventManager.bind(GuiEventType.UPDATE_MAIN_HERO, p -> {
            Unit hero = (Unit) p.get();
            // dirty flag?
            if (hero.isDead()) {
                return;
            }
            Pair<WeaponDataSource, WeaponDataSource> pair = new ImmutablePair<>(
                    new WeaponDataSource(hero.getActiveWeapon(false)),
                    new WeaponDataSource(hero.getNaturalWeapon()));
            mainHand.setUserObject(pair);
            pair = new ImmutablePair<>(
                    new WeaponDataSource(hero.getActiveWeapon(true)),
                    new WeaponDataSource(hero.getOffhandNaturalWeapon()));
            offhand.setUserObject(pair);

            GuiEventManager.trigger(ACTION_PANEL_UPDATE,
                    new PanelActionsDataSource(hero));

        });
    }

    private void nullifyUi() {

    }

    protected void initListeners() {
        GuiEventManager.bind(ACTION_PANEL_UPDATE, obj -> {
            final ActiveQuickSlotsDataSource source = (ActiveQuickSlotsDataSource) obj.get();
            if (source != null) {
                ActionValueContainer.setDarkened(false);
                if (getY() < 0) {
                    if (isMovedDownOnEnemyTurn())
                        ActionMaster.addMoveToAction(this, getX(), 0, 1);
                    //  ActorMaster.addFadeInOrOut(leftOrbPanel, 1);
                    //    ActorMaster.addFadeInOrOut(rigthOrbPanel, 1);
                }


                quickSlotPanel.setUserObject(source);
                modeActionsPanel.setUserObject(source);
                spellPanel.setUserObject(source);
                buffPanelSimple.setUserObject(source);
                leftOrbPanel.setUserObject(source);
                rigthOrbPanel.setUserObject(source);
            } else {
                //                setY(-IMAGE_SIZE);
                if (isMovedDownOnEnemyTurn())
                    ActionMaster.addMoveToAction(this, getX(), -IMAGE_SIZE, 1);

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
        altBg = EidolonsGame.isAltControlPanel();
        background.setImage(altBg ? BACKGROUND_PATH_ALT : BACKGROUND_PATH);
        if (altBg) {
            if (!EidolonsGame.isSpellsEnabled()) {
                if (spellPanel.getColor().a == 1)
                    spellPanel.fadeOut();
            } else {
                spellPanel.setPosition(modeActionsPanel.getX(), modeActionsPanel.getY());
            }


            if (modeActionsPanel.getColor().a == 1)
                modeActionsPanel.fadeOut();
            if (quickSlotPanel.getColor().a == 1)
                quickSlotPanel.fadeOut();
            if (orbOverlay.getColor().a == 1)
                orbOverlay.fadeOut();
            spellbookBtn.setVisible(false);
            invBtn.setVisible(false);
        } else {
                spellPanel.setPosition(spellsPos.x , spellsPos.y);
            if (spellPanel.getColor().a == 0)
                spellPanel.fadeIn();
            if (modeActionsPanel.getColor().a == 0)
                modeActionsPanel.fadeIn();
            if (quickSlotPanel.getColor().a == 0)
                quickSlotPanel.fadeIn();
            if (orbOverlay.getColor().a == 0)
                orbOverlay.fadeIn();

            spellbookBtn.setVisible(true);
            invBtn.setVisible(true);

            if (quickSlotPanel.isHovered() ||
                    spellPanel.isHovered() ||
                    modeActionsPanel.isHovered()
            ) {
                //TODO while hovering, don't update!!!
                BaseSlotPanel.hoveredAny = true;
            } else {
                BaseSlotPanel.hoveredAny = false;
            }
        }
        spellbookBtn.setPosition(modeActionsPanel.getX() + IMAGE_SIZE * 6 - 12,
                2);
        invBtn.setPosition(modeActionsPanel.getX() - 55,
                2);


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

    public BuffPanelSimple getBuffPanelSimple() {
        return buffPanelSimple;
    }

    public void update() {
        leftOrbPanel.setUpdateRequired(true);
        rigthOrbPanel.setUpdateRequired(true);
        initResolutionScaling();
    }

    public void setAltBg(boolean altBg) {
        this.altBg = altBg;
    }
}
