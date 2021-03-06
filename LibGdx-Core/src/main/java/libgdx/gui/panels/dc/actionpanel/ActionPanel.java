package libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.panels.dc.actionpanel.bar.BodyParamsBar;
import libgdx.gui.panels.dc.actionpanel.bar.SoulParamsBar;
import libgdx.gui.panels.dc.actionpanel.datasource.ActiveQuickSlotsDataSource;
import libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import libgdx.gui.panels.dc.actionpanel.facing.FacingPanel;
import libgdx.gui.panels.dc.actionpanel.weapon.QuickWeaponPanel;
import libgdx.gui.panels.dc.actionpanel.weapon.WeaponDataSource;
import libgdx.gui.panels.headquarters.HqMaster;
import libgdx.texture.Textures;
import libgdx.gui.generic.btn.ButtonStyled;
import main.content.enums.entity.ActionEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static main.system.GuiEventType.ACTION_PANEL_UPDATE;
import static main.system.auxiliary.log.LogMaster.log;

public class ActionPanel extends GroupX {
    public final static int IMAGE_SIZE = 60;

    public static final float EMPTY_OFFSET = 110;
    private static final float ACTIVE_SPACE_OFFSET_Y = -6;
    private static final float OFFSET_X = 100 + EMPTY_OFFSET;
    private static final float QUICK_SLOTS_OFFSET_X = 20;
    private static final float ACTIVE_SPACE_OFFSET_X = 70;
    private static final float PUZZLE_OFFSET_Y =  -88;
    private final Vector2 spellsPos = new Vector2();

    private final ImageContainer bottomOverlay;
    protected QuickSlotPanel quickSlotPanel;
    protected ModeActionsPanel modeActionsPanel;
    protected ActiveSpacePanel activeSpacePanel;
    protected FadeImageContainer background;
    QuickWeaponPanel mainHand;
    QuickWeaponPanel offhand;
    FacingPanel facingPanel;

    SymbolButton spellbookBtn = new SymbolButton(ButtonStyled.STD_BUTTON.SPELLBOOK, this::showSpellbook);
    SymbolButton invBtn = new SymbolButton(ButtonStyled.STD_BUTTON.INV, this::showInventory);

    protected BuffPanelSimple buffPanelBody;
    protected BuffPanelSimple buffPanelSoul;

    private final SoulParamsBar soulParamsBar;
    private final BodyParamsBar bodyParamsBar;

    private Float defaultX;

    ExtraPtsComp movePts = new ExtraPtsComp(false);
    ExtraPtsComp atkPts = new ExtraPtsComp(true);

    public ActionPanel() {
        addActor(background = new FadeImageContainer((Textures.BOTTOM_PANEL_BG)));
        //        background.pack();
        setSize(background.getWidth(), background.getHeight());
        addActor(quickSlotPanel = new QuickSlotPanel(IMAGE_SIZE));

        addActor(modeActionsPanel = new ModeActionsPanel(IMAGE_SIZE));

        addActor(activeSpacePanel = new ActiveSpacePanel(IMAGE_SIZE));

        /////////////ADDITIONAL

        soulParamsBar = new SoulParamsBar(Eidolons::getMainHero);
        bodyParamsBar = new BodyParamsBar(Eidolons::getMainHero);

        addActor(bodyParamsBar);
        addActor(soulParamsBar);
        addActor(facingPanel = new FacingPanel());

        addActor(movePts);
        addActor(atkPts);

        addActor(mainHand = new QuickWeaponPanel(false));
        addActor(offhand = new QuickWeaponPanel(true));

        addActor(bottomOverlay = new ImageContainer(Textures.BOTTOM_OVERLAY));

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
        quickSlotPanel.setPosition(-32 + OFFSET_X + QUICK_SLOTS_OFFSET_X, ACTIVE_SPACE_OFFSET_Y);
        final float actionOffset =17+ OFFSET_X + (IMAGE_SIZE * 6) + 5;
        modeActionsPanel.setPosition(-28+26+actionOffset, 0);
        final float spellOffset = ACTIVE_SPACE_OFFSET_X -60 + actionOffset + (IMAGE_SIZE * 6)- 35;
        activeSpacePanel.setPosition(spellOffset, ACTIVE_SPACE_OFFSET_Y);
        spellsPos.x = spellOffset;
        spellsPos.y = ACTIVE_SPACE_OFFSET_Y;
        bottomOverlay.setPosition(EMPTY_OFFSET + 100, -12);

        float x = quickSlotPanel.getX();

        bodyParamsBar.setPosition(x - 37, 47);
        buffPanelBody.setPosition(bodyParamsBar.getX() + 20, IMAGE_SIZE + 40);

        x = modeActionsPanel.getX()-34;
        mainHand.setPosition(x - 14,
                bodyParamsBar.getY() + 22);
        offhand.setPosition(mainHand.getX() + 235 + 146- 112,
                mainHand.getY()  );

        facingPanel.setPosition((mainHand.getX() + offhand.getX()) / 2  ,
                bodyParamsBar.getY() + 52);


        x = activeSpacePanel.getX();
        soulParamsBar.setPosition(x +11
                , bodyParamsBar.getY());
        buffPanelSoul.setPosition(soulParamsBar.getX() + 65, IMAGE_SIZE + 40);

        invBtn.setPosition(bodyParamsBar.getX() + bodyParamsBar.getWidth() / 2 + 79,
                getHeight() - 90);
        spellbookBtn.setPosition(soulParamsBar.getX() + soulParamsBar.getWidth() / 2 + 89,
                invBtn.getY());

        atkPts.setPosition(activeSpacePanel.getX()-12, 50);
        movePts.setPosition(bodyParamsBar.getX() + 337, 54);
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

        GuiEventManager.bind(GuiEventType.MINIMIZE_UI_ON, p -> {
            ActionMaster.addMoveToAction(this, getX(), PUZZLE_OFFSET_Y, 1.4f);
            ActionMaster.addAfter(this, ()-> setHidden(true));
            soulParamsBar.fadeOut();
            bodyParamsBar.fadeOut();
        });
        GuiEventManager.bind(GuiEventType.MINIMIZE_UI_OFF, p -> {
            ActionMaster.addMoveToAction(this, getX(), 0, 1.4f);
            ActionMaster.addAfter(this, ()-> setHidden(false));
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
            atkPts.setUserObject(source);
            movePts.setUserObject(source);
            quickSlotPanel.setUserObject(source);
            modeActionsPanel.setUserObject(source);
            activeSpacePanel.setUserObject(source);
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

        // altBg = EidolonsGame.isAltControlPanel();
        // background.setImage(altBg ? UiImages.BOTTOM_PANEL_BG_ALT : UiImages.BOTTOM_PANEL_BG);
        // if (altBg) {
        //     if (quickSlotPanel.getColor().a == 1)
        //         quickSlotPanel.fadeOut();
        //     spellbookBtn.setVisible(false);
        //     invBtn.setVisible(false);
        // } else
            {
            activeSpacePanel.setPosition(spellsPos.x, spellsPos.y);
            if (activeSpacePanel.getColor().a == 0)
                activeSpacePanel.fadeIn();
            if (modeActionsPanel.getColor().a == 0)
                modeActionsPanel.fadeIn();
            if (quickSlotPanel.getColor().a == 0)
                quickSlotPanel.fadeIn();

            spellbookBtn.setVisible(true);
            invBtn.setVisible(true);

            //TODO while hovering, don't update!!!
            BaseSlotPanel.hoveredAny = quickSlotPanel.isHovered() ||
                    activeSpacePanel.isHovered() ||
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

    public ActiveSpacePanel getActiveSpacePanel() {
        return activeSpacePanel;
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
    }

    public void setHidden(boolean hidden) {
        modeActionsPanel.setVisible(!hidden);
        activeSpacePanel.setVisible(!hidden);
        modeActionsPanel.setVisible(!hidden);
        log(1,"Action panel hidden: " +hidden);
    }


}
