package main.level_editor.backend.handlers;

import eidolons.libgdx.gui.overlay.choice.VC_DataSource;
import eidolons.system.text.tips.TIP;
import eidolons.system.text.tips.TextEvent;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_TestHandler extends LE_Handler implements ITestHandler {
    public LE_TestHandler(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void tip() {
        TextEvent tip = getDialogHandler().chooseEnum(TIP.class);
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, tip);
    }

    @Override
    public void vc() {
        VC_DataSource.VC_TYPE tip = getDialogHandler().chooseEnum(VC_DataSource.VC_TYPE.class);
        GuiEventManager.trigger(GuiEventType.VISUAL_CHOICE, tip);
    }

    @Override
    public void dialogue() {
        // VC_DataSource.VC_TYPE tip = getDialogHandler().chooseEnum(VC_DataSource.VC_TYPE.class);
        // GuiEventManager.trigger(GuiEventType.DIALOG_SHOW, tip);
    }
}
