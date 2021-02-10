package eidolons.libgdx.gui.panels.headquarters.weave.ui;

import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveMaster;

/**
 * Created by JustMe on 6/25/2018.
 */
public class WeaveButtonPanel extends TablePanelX{



    public WeaveButtonPanel(){
        add (new SmartTextButton("Undo", STD_BUTTON.MENU, this::undo));
        add (new SmartTextButton("Toggle", STD_BUTTON.MENU, this::toggle));
    }

    private void toggle() {
        WeaveMaster.toggleSkillsClasses();
    }


    private void undo() {
        HqDataMaster.undo();
    }
}
