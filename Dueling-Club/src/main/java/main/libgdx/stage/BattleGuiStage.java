package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.core.game.DC_Game;
import main.libgdx.gui.controls.radial.RadialMenu;
import main.libgdx.gui.panels.dc.InitiativePanel;
import main.libgdx.gui.panels.dc.actionpanel.ActionPanelController;
import main.libgdx.gui.panels.dc.inventory.InventoryWithAction;
import main.libgdx.gui.panels.dc.logpanel.FullLogPanel;
import main.libgdx.gui.panels.dc.logpanel.SimpleLogPanel;
import main.libgdx.gui.panels.dc.unitinfo.UnitInfoPanel;
import main.libgdx.gui.tooltips.ToolTipManager;

/**
 * Created by JustMe on 3/31/2017.
 */
public class BattleGuiStage extends Stage {

    public BattleGuiStage() {

        InitiativePanel initiativePanel = new InitiativePanel();
        initiativePanel.setPosition(0, Gdx.graphics.getHeight() - initiativePanel.getHeight());
        addActor(initiativePanel);

        addActor(new ActionPanelController(0, 0));

        addActor(new UnitInfoPanel(0, 0));

        addActor(new RadialMenu());

        addActor(new ToolTipManager());

        InventoryWithAction inventoryForm = new InventoryWithAction();
        inventoryForm.setPosition(0, Gdx.graphics.getHeight() - inventoryForm.getHeight());
        this.addActor(inventoryForm);

        SimpleLogPanel log = new SimpleLogPanel();
        log.setPosition(Gdx.graphics.getWidth() - log.getWidth(), 0);
        addActor(log);

        addActor(new FullLogPanel(100, 200));
    }

    @Override
    public boolean keyTyped(char character) {
        DC_Game.game.getKeyManager().handleKeyTyped(0, character);
        return super.keyTyped(character);
    }

    @Override
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
    }
}
