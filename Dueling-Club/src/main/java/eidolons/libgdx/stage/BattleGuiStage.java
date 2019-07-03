package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.mission.MissionStatManager;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Game;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.ScenarioGame;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims;
import eidolons.libgdx.gui.panels.dc.InitiativePanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.ActionPanel;
import eidolons.libgdx.gui.panels.dc.inventory.CombatInventory;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.dc.menus.outcome.OutcomeDatasource;
import eidolons.libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.neo.UnitInfoPanelNew;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.List;

/**
 * Created by JustMe on 3/31/2017.
 */
public class BattleGuiStage extends GuiStage {

    public static OrthographicCamera camera;
    private final InitiativePanel initiativePanel;
    private final ActionPanel bottomPanel;
    private final GuiVisualEffects guiVisualEffects;
    private final CombatInventory combatInventory;
    private UnitInfoPanelNew infoPanel;
    protected OutcomePanel outcomePanel;


    @Override
    public void resetZIndices() {
        super.resetZIndices();
        guiVisualEffects.setZIndex(Integer.MAX_VALUE);
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
        initiativePanel = new InitiativePanel();
        initiativePanel.setPosition(0, GdxMaster.getHeight() - initiativePanel.getHeight());
        addActor(initiativePanel);
        bottomPanel = new ActionPanel(0, 0);
        addActor(bottomPanel);

        init();

        if (!UnitInfoPanelNew.isNewUnitInfoPanelWIP())
            addActor( infoPanel = UnitInfoPanelNew.getInstance());

        combatInventory = new CombatInventory();
        combatInventory.setPosition(0, GdxMaster.getHeight() - combatInventory.getHeight());
        this.addActor(combatInventory);

        outcomePanel = new OutcomePanel();
        addActor(outcomePanel);
        outcomePanel.setVisible(false);
        addActor( new FullscreenAnims());

    }

    @Override
    protected boolean checkContainsNoOverlaying(List<Group> ancestors) {
        if (ancestors.contains(outcomePanel)){
            return false;
        }
        if (ancestors.contains(infoPanel)){
            return false;
        }
        if (ancestors.contains(infoPanel.outside)){
            return false;
        }
            return super.checkContainsNoOverlaying(ancestors);
    }

    @Override
    protected boolean checkBlocked() {
        if (CoreEngine.isActiveTestMode()) {
            return super.checkBlocked()  || outcomePanel.isVisible();
        }
        return super.checkBlocked()  || outcomePanel.isVisible() || infoPanel.isVisible();
    }

    @Override
    public void outsideClick() {
        setDraggedEntity(null);
        super.outsideClick();
        setScrollFocus(DungeonScreen.getInstance().getGridPanel());

        if (combatInventory.isVisible()) {
            //            combatInventory.close(ExplorationMaster.isExplorationOn());
            InventoryDataSource dataSource = (InventoryDataSource) combatInventory.getUserObject();
            if (ExplorationMaster.isExplorationOn()) {
                dataSource.getDoneHandler().run();
            } else
                dataSource.getCancelHandler().run();
        }
    }

    protected void bindEvents() {
        super.bindEvents();
        GuiEventManager.bind(GuiEventType. SHOW_ACHIEVEMENTS, p-> {
            String stats = MissionStatManager.getGameStatsText( );
            EUtils.onConfirm(stats +
             "\n Press the Assault!", false, null );
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
            CharSequence v= "";
            if (ScenarioGame.getGame() instanceof ScenarioGame) {
                try {
                    ScenarioMetaMaster m = ScenarioGame.getGame().getMetaMaster();
                    text = m.getMetaGame().getScenario().getName();
                    v = m.getMetaDataManager().getMissionName()
                            + ", Level [" + (m.getMetaGame().getMissionIndex() + 1) + "/" +
                            m.getMetaGame().getMissionNumber() + "]";
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
            outcomePanel.fadeIn();
            //            outcomePanel.setColor(new Color(1, 1, 1, 0));
            //            ActorMaster.addFadeInOrOut(outcomePanel, 2.5f);
            float y = GdxMaster.getHeight() -
             (GdxMaster.getHeight() - outcomePanel.getHeight() / 2);
            float x = (GdxMaster.getWidth() - outcomePanel.getWidth()) / 2;
            outcomePanel.setPosition(x, y + outcomePanel.getHeight());
            ActionMaster.addMoveToAction(outcomePanel, x, y, 2.5f);
        });

    }


    public void update() {
        getBottomPanel().setX(
         (GdxMaster.getWidth() - logPanel.getWidth() - getBottomPanel().getWidth()) / 2);

        locationLabel.setPosition(25,
         GdxMaster.getHeight() - locationLabel.getHeight() - initiativePanel.getHeight()-30);

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
    }

    public InitiativePanel getInitiativePanel() {
        return initiativePanel;
    }

    public ActionPanel getBottomPanel() {
        return bottomPanel;
    }

    public GuiVisualEffects getGuiVisuals() {
        return guiVisualEffects;
    }
}
