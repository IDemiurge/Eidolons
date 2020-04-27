package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.mission.quest.QuestMissionStatManager;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.ScenarioGame;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.netherflame.igg.CustomLaunch;
import eidolons.game.netherflame.igg.IGG_Game;
import eidolons.game.netherflame.igg.soul.SoulforcePanel;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims;
import eidolons.libgdx.gui.panels.dc.actionpanel.ActionPanel;
import eidolons.libgdx.gui.panels.dc.atb.AtbPanel;
import eidolons.libgdx.gui.panels.dc.inventory.CombatInventory;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.dc.menus.outcome.OutcomeDatasource;
import eidolons.libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.neo.UnitInfoPanelNew;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.particles.ParticlesSprites;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.screens.ScreenMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

import java.util.List;

import static eidolons.game.battlecraft.rules.RuleKeeper.RULE.SOULFORCE;

/**
 * Created by JustMe on 3/31/2017.
 */
public class BattleGuiStage extends GuiStage {

    public static OrthographicCamera camera;
    private final AtbPanel atbPanel;
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
                batch == null ? new CustomSpriteBatch() :
                        batch);
        addActor(guiVisualEffects = new GuiVisualEffects());
        addActor(particlesSprites = new ParticlesSprites());
        atbPanel = new AtbPanel();
        atbPanel.setPosition(0, GdxMaster.getHeight() - atbPanel.getHeight());
        addActor(atbPanel);
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

        if (RuleKeeper.isRuleOn(SOULFORCE)) {
            addActor(soulforcePanel = new SoulforcePanel());
            GdxMaster.center(soulforcePanel);
            soulforcePanel.setY(GdxMaster.getTopY(soulforcePanel));
        }

        getBottomPanel().setX(GdxMaster.centerWidthScreen(getBottomPanel()));
//        getBottomPanel().setX((GdxMaster.getWidth() - fullLogPanel.getWidth() - getBottomPanel().getWidth()) / 2 + 70);

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
        if (CoreEngine.isActiveTestMode()) {
            return super.checkBlocked() || outcomePanel.isVisible();
        }
        return super.checkBlocked() || outcomePanel.isVisible() || GDX.isVisible(infoPanel);
    }

    @Override
    public void outsideClick() {
        setDraggedEntity(null);
        super.outsideClick();
        setScrollFocus(ScreenMaster.getDungeonGrid());

        if (combatInventory.isVisible()) {
            //            combatInventory.close(ExplorationMaster.isExplorationOn());
            InventoryDataSource dataSource = combatInventory.getUserObject();
            if (ExplorationMaster.isExplorationOn()) {
                dataSource.getDoneHandler().run();
            } else
                dataSource.getCancelHandler().run();
        }
    }

    protected void bindEvents() {
        super.bindEvents();


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
            Eidolons.getGame().getLoop().setPaused(true, false);
            infoPanel.setUserObject(HqDataMaster.getHeroDataSource(unit));
        });
        GuiEventManager.bind(GuiEventType.GAME_STARTED, p -> {
            CharSequence text = "";
            CharSequence v = "";
            if (Eidolons.getGame() instanceof ScenarioGame) {
                try {

                    if (EidolonsGame.FOOTAGE) {
                        text = "Extended Demo";
                        v =
                                StringMaster.getWellFormattedString(PathUtils.getLastPathSegment(StringMaster.cropFormat(MainLauncher.getCustomLaunch().getValue(CustomLaunch.CustomLaunchValue.xml_path)))
                                        + ", Level [" + (RandomWizard.getRandomIntBetween(1, 3)) + "/" +
                                        3 + "]");
                    } else {
                        ScenarioMetaMaster m = ScenarioGame.getGame().getMetaMaster();
                        text = m.getMetaGame().getScenario().getName();
                        v = m.getMetaDataManager().getMissionName()
                                + ", Level [" + (m.getMetaGame().getMissionIndex() + 1) + "/" +
                                m.getMetaGame().getMissionNumber() + "]";
                    }

                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            } else {

                if (DC_Game.game instanceof IGG_Game) {
//               TODO igg demo fix
//                IGG_MetaMaster m = m = ScenarioGame.getGame().getMetaMaster();
//                    text = m.getMetaGame().getScenario().getName();
//                    v = m.getMetaDataManager().getMissionName()
//                            + ", Level [" + (m.getMetaGame().getMissionIndex() + 1) + "/" +
//                            m.getMetaGame().getMissionNumber() + "]";
                }
            }

            locationLabel.setNameText(text);
            locationLabel.setValueText(v);
            locationLabel.pack();
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
            ActionMaster.addMoveToAction(outcomePanel, x, y, 2.5f);
        });

    }


    public void update() {

        locationLabel.setPosition(25,
                GdxMaster.getHeight() - locationLabel.getHeight() - atbPanel.getHeight() - 100);

//        if (outcomePanel != null)
//            outcomePanel.setZIndex(Integer.MAX_VALUE);
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

    public AtbPanel getAtbPanel() {
        return atbPanel;
    }

    public ActionPanel getBottomPanel() {
        return bottomPanel;
    }

    public GuiVisualEffects getGuiVisuals() {
        return guiVisualEffects;
    }
}
