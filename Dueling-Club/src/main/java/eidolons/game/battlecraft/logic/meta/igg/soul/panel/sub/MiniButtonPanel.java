package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;

public class MiniButtonPanel extends TablePanelX {
    public MiniButtonPanel(ButtonHandler handler, String ... btns) {

        for (String btn : btns) {
            add(new SmartButton(btn, ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN, () -> handler.handle(btn))).uniform().row();
        }
    }
}
