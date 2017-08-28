package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.core.game.DC_Game;
import main.libgdx.anims.ActorMaster;
import main.libgdx.gui.controls.radial.RadialMenu;
import main.libgdx.gui.panels.dc.ButtonStyled;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.InitiativePanel;
import main.libgdx.gui.panels.dc.actionpanel.ActionPanelController;
import main.libgdx.gui.panels.dc.inventory.InventoryWithAction;
import main.libgdx.gui.panels.dc.logpanel.FullLogPanel;
import main.libgdx.gui.panels.dc.logpanel.SimpleLogPanel;
import main.libgdx.gui.panels.dc.menus.outcome.OutcomeDatasource;
import main.libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import main.libgdx.gui.panels.dc.unitinfo.UnitInfoPanel;
import main.libgdx.gui.tooltips.ToolTipManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.options.OptionsMaster;

/**
 * Created by JustMe on 3/31/2017.
 */
public class BattleGuiStage extends Stage {

    private OutcomePanel outcomePanel;

    public BattleGuiStage() {

        InitiativePanel initiativePanel = new InitiativePanel();
        initiativePanel.setPosition(0, Gdx.graphics.getHeight() - initiativePanel.getHeight());
        addActor(initiativePanel);

        addActor(new ActionPanelController(0, 0));

        addActor(new UnitInfoPanel(0, 0));

        ButtonStyled menuButton = new ButtonStyled(STD_BUTTON.OPTIONS, () -> OptionsMaster.openMenu());
        menuButton.setPosition(Gdx.graphics.getWidth() - menuButton.getWidth(),
         Gdx.graphics.getHeight() - menuButton.getHeight());
        addActor(menuButton);

        addActor(new RadialMenu());

        addActor(new ToolTipManager());

        InventoryWithAction inventoryForm = new InventoryWithAction();
        inventoryForm.setPosition(0, Gdx.graphics.getHeight() - inventoryForm.getHeight());
        this.addActor(inventoryForm);

        SimpleLogPanel log = new SimpleLogPanel();
        log.setPosition(Gdx.graphics.getWidth() - log.getWidth(), 0);
        addActor(log);

        addActor(new FullLogPanel(100, 200));

        bindEvents();
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.GAME_FINISHED, p -> {
            if (outcomePanel != null)
                outcomePanel.remove();
            outcomePanel = new OutcomePanel(new OutcomeDatasource((DC_Game) p.get()));
            addActor(outcomePanel);
            outcomePanel.setZIndex(getActors().size);
//            outcomePanel.setColor(new Color(1, 1, 1, 0));
//            ActorMaster.addFadeInOrOut(outcomePanel, 2.5f);
            float y = Gdx.graphics.getHeight() -
             (Gdx.graphics.getHeight() - outcomePanel.getHeight()/ 2) ;
            float x = (Gdx.graphics.getWidth() - outcomePanel.getWidth()) / 2;
            outcomePanel.setPosition(x, y + outcomePanel.getHeight());
            ActorMaster.addMoveToAction(outcomePanel, x, y, 2.5f);
        });
    }

    @Override
    public void act() {
        super.act();
        if (outcomePanel != null)
            outcomePanel.setZIndex(Integer.MAX_VALUE);
    }

    @Override
    public boolean keyTyped(char character) {
        boolean result = DC_Game.game.getKeyManager().handleKeyTyped(0, character);
        if (result)
            return true;
        return super.keyTyped(character);
    }

/*    @Override
    public void draw() {
        final Matrix4 combined = getCamera().combined.cpy();
        getCamera().update();

        final Group root = getRoot();

        if (!root.isVisible()) return;

        combined.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Batch batch = this.getBatch();
        batch.setProjectionMatrix(combined);
        batch.begin();
        root.draw(batch, 1);
        batch.end();
    }*/
}
