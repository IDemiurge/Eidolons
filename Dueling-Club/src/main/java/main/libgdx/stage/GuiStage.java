package main.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.entity.obj.DC_Obj;
import main.libgdx.gui.ToolTipManager;
import main.libgdx.gui.controls.radial.DebugRadialManager;
import main.libgdx.gui.controls.radial.RadialMenu;
import main.libgdx.gui.panels.dc.InitiativePanel;
import main.libgdx.gui.panels.dc.LogPanel;
import main.libgdx.gui.panels.dc.actionpanel.ActionPanelController;
import main.libgdx.gui.panels.dc.inventory.InventoryPanel;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.libgdx.gui.panels.dc.unitinfo.UnitInfoPanel;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import static main.libgdx.gui.controls.radial.RadialManager.createNew;
import static main.system.GuiEventType.CREATE_RADIAL_MENU;
import static main.system.GuiEventType.SHOW_TOOLTIP;

/**
 * Created by JustMe on 3/31/2017.
 */
public class GuiStage extends Stage {

    protected ToolTipManager toolTipManager;
    private LogPanel log;
    private InventoryPanel inventoryDialog;
    private RadialMenu radialMenu;
    private UnitInfoPanel infoPanel;
    private InitiativePanel initiativePanel;

    public GuiStage() {

        initiativePanel = new InitiativePanel();
        initiativePanel.setPosition(0, Gdx.graphics.getHeight() - initiativePanel.getHeight());
        addActor(initiativePanel);

        ActionPanelController actionPanelController = new ActionPanelController();
        actionPanelController.setPosition(0, 0);
        addActor(actionPanelController);

        infoPanel = new UnitInfoPanel();
        addActor(infoPanel);
        infoPanel.setPosition(0, 0);

        addActor(toolTipManager = new ToolTipManager());

        addActor(radialMenu = new RadialMenu());

        log = new LogPanel();
        addActor(log);
        log.setPosition(Gdx.graphics.getWidth() - log.getWidth(), 0);

        bindEvents();
    }

    public void bindEvents() {
        GuiEventManager.bind(GuiEventType.CLOSE_INVENTORY_DIALOG, (obj) -> {
            inventoryDialog.setVisible(false);
        });
        GuiEventManager.bind(GuiEventType.REFRESH_INVENTORY_DIALOG, (obj) -> {
            if (obj==null ){
                inventoryDialog.setUserObject(inventoryDialog.getUserObject());
            }else {
                inventoryDialog.setUserObject(obj.get());
            inventoryDialog.initButtonListeners(((InventoryDataSource) inventoryDialog.getUserObject()).getHandler());
            }
        });
        GuiEventManager.bind(GuiEventType.SHOW_INVENTORY_DIALOG, (obj) -> {
            if (inventoryDialog == null) {
                inventoryDialog = new InventoryPanel();
                this.addActor(inventoryDialog);
                inventoryDialog.setPosition(0, Gdx.graphics.getHeight() - inventoryDialog.getHeight());
            }
            GuiEventManager.trigger(GuiEventType.REFRESH_INVENTORY_DIALOG, obj);
            inventoryDialog.setVisible(true);
        });

        GuiEventManager.bind(CREATE_RADIAL_MENU, obj -> {
            DC_Obj dc_obj = (DC_Obj) obj.get();
            GuiEventManager.trigger(SHOW_TOOLTIP, new EventCallbackParam(null));
            if (Gdx.input.isButtonPressed(0) || Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                radialMenu.init(DebugRadialManager.getDebugNodes(dc_obj));
            } else {
                radialMenu.init(createNew(dc_obj));
            }
        });
    }
}