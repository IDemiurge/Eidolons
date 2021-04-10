package libgdx.gui.panels.lord.sub;

import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.panels.TablePanelX;

public class MiniButtonPanel extends TablePanelX {
    public MiniButtonPanel(ButtonHandler handler, String ... btns) {

        for (String btn : btns) {
            add(new SmartTextButton(btn, ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN, () -> handler.handle(btn))).uniform().row();
        }
    }
}
