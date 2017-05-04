package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
public class GuiStage extends Stage {

    public GuiStage() {

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
}
