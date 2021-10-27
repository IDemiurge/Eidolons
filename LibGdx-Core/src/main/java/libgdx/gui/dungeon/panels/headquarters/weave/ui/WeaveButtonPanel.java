package libgdx.gui.dungeon.panels.headquarters.weave.ui;

import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveMaster;
import libgdx.gui.generic.btn.ButtonStyled;

/**
 * Created by JustMe on 6/25/2018.
 */
public class WeaveButtonPanel extends TablePanelX{



    public WeaveButtonPanel(){
        add (new SmartTextButton("Undo", ButtonStyled.STD_BUTTON.MENU, this::undo));
        add (new SmartTextButton("Toggle", ButtonStyled.STD_BUTTON.MENU, this::toggle));
    }

    private void toggle() {
        WeaveMaster.toggleSkillsClasses();
    }


    private void undo() {
        HqDataMaster.undo();
    }
}
