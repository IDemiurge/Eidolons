package eidolons.libgdx.gui.panels.headquarters.weave.ui;

import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;

/**
 * Created by JustMe on 6/25/2018.
 */
public class WeaveButtonPanel extends GroupX{

    public WeaveButtonPanel(){
        addActor(new TextButtonX("Undo", STD_BUTTON.MENU, ()->undo()));
    }

    private void undo() {
        HqDataMaster.undo();
    }
}
