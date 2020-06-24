package eidolons.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.UiImages;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.dc.actionpanel.bar.BodyParamsBar;
import eidolons.libgdx.gui.panels.dc.actionpanel.bar.SoulParamsBar;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import eidolons.libgdx.gui.panels.dc.actionpanel.facing.FacingPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.WeaponDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import main.content.enums.entity.ActionEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.ACTION_PANEL_UPDATE;

public class ActionPanel extends GroupX {
    public final static int IMAGE_SIZE = 60;

    public static final float EMPTY_OFFSET = 110;
    private static final float SPELL_OFFSET_Y = -6;
    private static final float OFFSET_X = 100 + EMPTY_OFFSET;
    private static final float QUICK_SLOTS_OFFSET_X = 20;
    private static final float SPELL_OFFSET_X = 70;
    private static final float PUZZLE_OFFSET_Y =  -60;
    private final Vector2 spellsPos = new Vector2();

    private final ImageContainer bottomOverlay;
    protected QuickSlotPanel quickSlotPanel;
    protected ModeActionsPanel modeActionsPanel;
    protected SpellPanel spellPanel;
    protected FadeImageContainer background;
    QuickWeaponPanel mainHand;
    QuickWeaponPanel offhand;
    FacingPanel facingPanel;

    SymbolButton spellbookBtn = new SymbolButton(STD_BUTTON.SPELLBOOK, () -> showSpellbook());
    SymbolButton invBtn = new SymbolButton(STD_BUTTON.INV, () -> showInventory());

    protected BuffPanelSimple buffPanelBody;
    protected BuffPanelSimple buffPanelSoul;

    private final SoulParamsBar soulParamsBar;
    private final BodyParamsBar bodyParamsBar;

    private boolean altBg;
    private Float defaultX;

    public ActionPanel() {
        addActor(background = new FadeImageContainer((UiImages.BOTTOM_PANEL_BG)));
        //        background.pack();
        setSize(background.getWidth(), background.getHeight());
        addActor(quickSlotPanel = new QuickSlotPanel(IMAGE_SIZE));

        addActor(modeActionsPanel = new ModeActionsPanel(IMAGE_SIZE));

        addActor(spellPanel = new SpellPanel(IMAGE_SIZE));

        /////////////ADDITIONAL

        soulParamsBar = new SoulParamsBar(() -> Eidolons.getMainHero());
        bodyParamsBar = new BodyParamsBar(() -> Eidolons.getMainHero());

        addActor(bodyParamsBar);
        addActor(soulParamsBar);
        addActor(facingPanel = new FacingPanel());
        addActor(mainHand = new QuickWeaponPanel(false));
        addActor(offhand = new QuickWeaponPanel(true));

        addActor(bottomOverlay = new ImageContainer(UiImages.BOTTOM_OVERLAY));

        buffPanelBody = new BuffPanelSimple(false);
        buffPanelSoul = new BuffPanelSimple(true);

        addActor(buffPanelBody);
        addActor(buffPanelSoul);

        addActor(spellbookBtn);
        addActor(invBtn);

        setY(-IMAGE_SIZE);
        bindEvents();
        initListeners();

        initResolutionScaling();
        resetPositions();
    }

    public void resetPositions() {
        quickSlotPanel.setPosition(30 + OFFSET_X + QUICK_SLOTS_OFFSET_X, SPELL_OFFSET_Y);
        final float actionOffset =17+ OFFSET_X + (IMAGE_SIZE * 6) + 5;
        modeActionsPanel.setPosition(11+actionOffset, 0);
        final float spellOffset = SPELL_OFFSET_X + actionOffset + (IMAGE_SIZE * 6)- 15;
        spellPanel.setPosition(spellOffset, SPELL_OFFSET_Y);
        spellsPos.x = spellOffset;
        spellsPos.y = SPELL_OFFSET_Y;
        bottomOverlay.setPosition(EMPTY_OFFSET + 100, -12);

        float x = quickSlotPanel.getX();

        bodyParamsBar.setPosition(x - 34, 33);
        buffPanelBody.setPosition(bodyParamsBar.getX() + 20, IMAGE_SIZE + 12);

        x = modeActionsPanel.getX();
        mainHand.setPosition(x - 23,
                bodyParamsBar.getY() + 22);
        offhand.setPosition(mainHand.getX() + 272 + 146- 112,
                mainHand.getY()  );

        facingPanel.setPosition((mainHand.getX() + offhand.getX()) / 2  ,
                bodyParamsBar.getY() + 52);


        x = spellPanel.getX();
        soulParamsBar.setPosition(x - 32
                , bodyParamsBar.getY());
        buffPanelSoul.setPosition(soulParamsBar.getX() + 35, IMAGE_SIZE + 25);

        invBtn.setPosition(bodyParamsBar.getX() + bodyParamsBar.getWidth() / 2 + 74,
                getHeight() - 90);
        spellbookBtn.setPosition(soulParamsBar.getX() + soulParamsBar.getWidth() / 2 + 74,
                invBtn.getY());

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
                Eidolons.activateMainHeroAction(ActionEnums.USE_INVENTORY);
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
            ActionMaster.addMoveToAction(this, getX(), PUZZLE_OFFSET_Y, 1.4f);
            soulParamsBar.fadeOut();
            bodyParamsBar.fadeOut();
        });
        GuiEventManager.bind(GuiEventType.PUZZLE_FINISHED, p -> {
            ActionMaster.addMoveToAction(this, getX(), 0, 1.4f);
            soulParamsBar.fadeIn();
            bodyParamsBar.fadeIn();
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
            if (hero == null) {
                hero = Eidolons.getMainHero();
            }
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

            updatePanel();

        });
    }

    protected void initListeners() {
        GuiEventManager.bind(ACTION_PANEL_UPDATE, obj -> updatePanel());
    }

    public void updatePanel() {
        final ActiveQuickSlotsDataSource source = new PanelActionsDataSource(Eidolons.getMainHero());
        if (source != null) {
            ActionContainer.setDarkened(false);
            if (getY() < 0) {
                if (isMovedDownOnEnemyTurn())
                    ActionMaster.addMoveToAction(this, getX(), 0, 1);
                //  ActorMaster.addFadeInOrOut(bodyParamsBar, 1);
                //    ActorMaster.addFadeInOrOut(soulParamsBar, 1);
            }

            //Gdx revamp - action containers re-creation!
            quickSlotPanel.setUserObject(source);
            modeActionsPanel.setUserObject(source);
            spellPanel.setUserObject(source);
            buffPanelBody.setUserObject(source);
            buffPanelSoul.setUserObject(source);
            bodyParamsBar.setUserObject(source);
            soulParamsBar.setUserObject(source);
        } else {
            //                setY(-IMAGE_SIZE);
            if (isMovedDownOnEnemyTurn())
                ActionMaster.addMoveToAction(this, getX(), -IMAGE_SIZE, 1);

            ActionContainer.setDarkened(true);
            // ActorMaster.addFadeInOrOut(bodyParamsBar, 1);
            // ActorMaster.addFadeInOrOut(soulParamsBar, 1);
        }
    }

    private boolean isMovedDownOnEnemyTurn() {
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        resetPositions();

        altBg = EidolonsGame.isAltControlPanel();
        background.setImage(altBg ? UiImages.BOTTOM_PANEL_BG_ALT : UiImages.BOTTOM_PANEL_BG);
        if (altBg) {
            if (quickSlotPanel.getColor().a == 1)
                quickSlotPanel.fadeOut();
            spellbookBtn.setVisible(false);
            invBtn.setVisible(false);
        } else {
            spellPanel.setPosition(spellsPos.x, spellsPos.y);
            if (spellPanel.getColor().a == 0)
                spellPanel.fadeIn();
            if (modeActionsPanel.getColor().a == 0)
                modeActionsPanel.fadeIn();
            if (quickSlotPanel.getColor().a == 0)
                quickSlotPanel.fadeIn();

            spellbookBtn.setVisible(true);
            invBtn.setVisible(true);

            //TODO while hovering, don't update!!!
            BaseSlotPanel.hoveredAny = quickSlotPanel.isHovered() ||
                    spellPanel.isHovered() ||
                    modeActionsPanel.isHovered();
        }


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

    public BuffPanelSimple getBuffPanelBody() {
        return buffPanelBody;
    }

    public void update() {
        bodyParamsBar.reset();
        soulParamsBar.reset();
        initResolutionScaling();
    }

    public void setAltBg(boolean altBg) {
        this.altBg = altBg;
    }
}
