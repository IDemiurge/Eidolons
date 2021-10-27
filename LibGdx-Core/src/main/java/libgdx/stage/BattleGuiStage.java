package libgdx.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.mission.quest.QuestMissionStatManager;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.handlers.ExplorationMaster;
import libgdx.gui.dungeon.panels.sf_old.SoulforcePanel;
import libgdx.GDX;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.fullscreen.FullscreenAnims;
import libgdx.gui.HideButton;
import libgdx.gui.dungeon.overlay.choice.VisualChoice;
import libgdx.gui.dungeon.overlay.choice.VisualChoiceHandler;
import libgdx.gui.dungeon.panels.dc.actionpanel.ActionPanel;
import libgdx.gui.dungeon.panels.dc.inventory.CombatInventory;
import libgdx.gui.dungeon.panels.dc.inventory.datasource.InventoryDataSource;
import libgdx.gui.dungeon.panels.dc.menus.outcome.OutcomeDatasource;
import libgdx.gui.dungeon.panels.dc.menus.outcome.OutcomePanel;
import libgdx.gui.dungeon.panels.dc.topleft.TopLeftPanel;
import libgdx.gui.dungeon.panels.dc.unitinfo.neo.UnitInfoPanelNew;
import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import libgdx.particles.ParticlesSprites;
import libgdx.screens.handlers.ScreenMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;

import java.util.List;

import static eidolons.game.battlecraft.rules.RuleEnums.RULE.SOULFORCE;

/**
 * Created by JustMe on 3/31/2017.
 */
public class BattleGuiStage extends GuiStage {

    public static OrthographicCamera camera;
    private final TopLeftPanel topLeftPanel;
    private final ActionPanel bottomPanel;
    private final GuiVisualEffects guiVisualEffects;
    private final CombatInventory combatInventory;
    private final FullscreenAnims fullscreenAnims;
    private UnitInfoPanelNew infoPanel;
    protected OutcomePanel outcomePanel;
    ParticlesSprites particlesSprites;

    SoulforcePanel soulforcePanel;

    @Override
    public void resetZIndices() {
        super.resetZIndices();
        guiVisualEffects.setZIndex(Integer.MAX_VALUE);
        particlesSprites.setZIndex(Integer.MAX_VALUE);
    }

    public BattleGuiStage(ScreenViewport viewport, Batch batch) {
        super(viewport == null ?
                        //         new ScalingViewport(Scaling.stretch, GdxMaster.getWidth(),
                        //          GdxMaster.getHeight(), new OrthographicCamera())
                        new FillViewport(GdxMaster.getWidth(),
                                GdxMaster.getHeight(), new OrthographicCamera())
                        //        new ScreenViewport( new OrthographicCamera())
                        : viewport,
                batch == null ? GdxMaster.createBatchInstance() :
                        batch);
        addActor(guiVisualEffects = new GuiVisualEffects());
        addActor(particlesSprites = new ParticlesSprites());
        topLeftPanel = new TopLeftPanel();
        //        atbPanel.setPosition(0, GdxMaster.getHeight() - atbPanel.getHeight());
        addActor(topLeftPanel);
        bottomPanel = new ActionPanel(0, 0);
        addActor(bottomPanel);

        init();

        if (!UnitInfoPanelNew.isNewUnitInfoPanelWIP())
            addActor(infoPanel = UnitInfoPanelNew.getInstance());

        combatInventory = new CombatInventory();
        combatInventory.setPosition(0, GdxMaster.getHeight() - combatInventory.getHeight());
        this.addActor(combatInventory);

        outcomePanel = new OutcomePanel();
        addActor(outcomePanel);
        outcomePanel.setVisible(false);
        addActor(fullscreenAnims = new FullscreenAnims());


        getBottomPanel().setX(GdxMaster.centerWidthScreen(getBottomPanel()));
        if (VisualChoiceHandler.isOn()) {
            VisualChoice vc;
            addActor(vc = new VisualChoice());
            vc.setVisible(false);
        }
        //        getBottomPanel().setX((GdxMaster.getWidth() - fullLogPanel.getWidth() - getBottomPanel().getWidth()) / 2 + 70);

    }

    @Override
    protected void afterInit() {
        if (RuleKeeper.isRuleOn(SOULFORCE)) {
            //  RollDecorator.RollableGroup decorated;
            //  addActor(decorated = RollDecorator.decorate(
            //          bg = new FadeImageContainer(Images.COLUMNS), FACING_DIRECTION.NORTH, true));
            // decorated.setRollPercentage(0.78f);
            // decorated.setRollIsLessWhenOpen(true);
            //  decorated.toggle(false);
            addActor(soulforcePanel = new SoulforcePanel());
            GdxMaster.center(soulforcePanel);
            soulforcePanel.setY(GdxMaster.getTopY(soulforcePanel));
            HideButton sbHideBtn;
            addActor(sbHideBtn = new HideButton(soulforcePanel));
            GdxMaster.center(sbHideBtn);
            sbHideBtn.setX(sbHideBtn.getX() - 7);
            sbHideBtn.setY(GdxMaster.getHeight() - 73);
        }
    }

    @Override
    protected Actor[] getDialogueActors() {
        return new Actor[]{
                dialogueContainer,
                confirmationPanel,
                fullscreenAnims,
                particlesSprites,
        };
    }

    @Override
    protected void drawCinematicMode(Batch batch) {
        //        Camera camera = getViewport().getCamera();
        //        camera.update();
        //        batch.setProjectionMatrix(camera.combined);
        //        dialogueContainer.draw(batch, 1f);
        //        if (confirmationPanel.isVisible()) {
        //            confirmationPanel.draw(batch, 1f);
        //        }
        //        if (tipMessageWindow.isVisible()) {
        //            tipMessageWindow.draw(batch, 1f);
        //        }
        //        fullscreenAnims.draw(batch, 1f);

    }

    @Override
    protected boolean checkContainsNoOverlaying(List<Group> ancestors) {
        if (ancestors.contains(outcomePanel)) {
            return false;
        }
        if (infoPanel != null) {
            if (ancestors.contains(infoPanel)) {
                return false;
            }
            if (ancestors.contains(infoPanel.outside)) {
                return false;
            }
        }
        return super.checkContainsNoOverlaying(ancestors);
    }

    @Override
    protected boolean checkBlocked() {
        if (Flags.isActiveTestMode()) {
            return super.checkBlocked() || outcomePanel.isVisible();
        }
        return super.checkBlocked() || outcomePanel.isVisible() || GDX.isVisible(infoPanel);
    }

    @Override
    public void outsideClick() {
        setDraggedEntity(null);
        super.outsideClick();
        setScrollFocus(ScreenMaster.getGrid());

        if (combatInventory.isVisible()) {
            //            combatInventory.close(ExplorationMaster.isExplorationOn());
            InventoryDataSource dataSource = combatInventory.getUserObject();
            if (ExplorationMaster.isExplorationOn()) {
                dataSource.getDoneHandler().run();
            } else
                dataSource.getCancelHandler().run();
        }
    }

    @Override
    public boolean keyTyped(char character) {
        if (GdxMaster.isVisibleEffectively(textInputPanel)) {
            //TODO exit?!
            textInputPanel.keyTyped(character);
            textInputPanel.acceptChar(character);
            return true;
        }
        if (Flags.isIDE()) {
            GuiEventManager.trigger(GuiEventType.KEY_TYPED, (int) character);
        }
        return super.keyTyped(character);
    }

    protected void bindEvents() {
        super.bindEvents();

        GuiEventManager.bind(GuiEventType.COMBAT_STARTED, obj -> {
            topLeftPanel.getAtbPanel().toggleQueue(true);
            // WaitMaster.receiveInput(combat_ui_ready, true);
        });
        GuiEventManager.bind(GuiEventType.COMBAT_ENDED, obj -> {

            largeText.show("Battle is Over", obj.get().toString(), 2f);
            //TODO wait for 2 seconds!
            topLeftPanel.getAtbPanel().toggleQueue(false);
        });

        GuiEventManager.bind(GuiEventType.SHOW_ACHIEVEMENTS, p -> {
            String stats = QuestMissionStatManager.getGameStatsText();
            EUtils.onConfirm(stats +
                    "\n Press the Assault!", false, null);
        });
        GuiEventManager.bind(GuiEventType.SHOW_UNIT_INFO_PANEL, (obj) -> {
            Unit unit = (Unit) obj.get();
            if (UnitInfoPanelNew.isNewUnitInfoPanelWIP()) {
                addActor(infoPanel = UnitInfoPanelNew.getInstance());
            }
            Core.getGame().getLoop().setPaused(true, false);
            infoPanel.setUserObject(HqDataMaster.getHeroDataSource(unit));
        });

        GuiEventManager.bind(GuiEventType.GAME_FINISHED, p -> {
            outcomePanel.setUserObject(new OutcomeDatasource((DC_Game) p.get()));
            outcomePanel.setVisible(true);

            outcomePanel.setColor(new Color(1, 1, 1, 1));
            //            ActorMaster.addFadeInOrOut(outcomePanel, 2.5f);
            float y = GdxMaster.getHeight() -
                    (GdxMaster.getHeight() - outcomePanel.getHeight() / 2);
            float x = (GdxMaster.getWidth() - outcomePanel.getWidth()) / 2;
            outcomePanel.setPosition(x, y + outcomePanel.getHeight());
            ActionMasterGdx.addMoveToAction(outcomePanel, x, y, 2.5f);
        });

    }


    public void update() {

    }

    @Override
    public List<Actor> getActorsForTown() {
        List<Actor> list = super.getActorsForTown();
        list.add(guiVisualEffects);
        return list;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update();
        questProgressPanel.setY(logPanel.getY() - questProgressPanel.getPrefHeight() - 30);
        hideQuests.setPosition(questProgressPanel.getX()
                        + GdxMaster.adjustSizeBySquareRoot(100),
                questProgressPanel.getY() - 10 + questProgressPanel.getHeight());
    }

    public ActionPanel getBottomPanel() {
        return bottomPanel;
    }

    public GuiVisualEffects getGuiVisuals() {
        return guiVisualEffects;
    }
}
