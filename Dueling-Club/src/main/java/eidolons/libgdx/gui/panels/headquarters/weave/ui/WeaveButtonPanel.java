package eidolons.libgdx.gui.panels.headquarters.weave.ui;

import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveMaster;

/**
 * Created by JustMe on 6/25/2018.
 */
public class WeaveButtonPanel extends TablePanelX{



    public WeaveButtonPanel(){
        add (new TextButtonX("Undo", STD_BUTTON.MENU, ()->undo()));
        add (new TextButtonX("Toggle", STD_BUTTON.MENU, ()->toggle()));
    }

    private void toggle() {
        WeaveMaster.toggleSkillsClasses();
    }


    private void undo() {
        HqDataMaster.undo();
    }
}
