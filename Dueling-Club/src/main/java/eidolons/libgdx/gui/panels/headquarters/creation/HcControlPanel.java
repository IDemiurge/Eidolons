package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;

/**
 * Created by JustMe on 7/6/2018.
 */
public class HcControlPanel extends TablePanelX{
    public HcControlPanel() {
        //symbol/title
        super(320, 64);
        add(new SmartButton(STD_BUTTON.CANCEL, ()->cancel()));
        add(new SmartButton(STD_BUTTON.UNDO, ()->undo()));
        add(new SmartButton(STD_BUTTON.NEXT, ()->redo()));
        add(new SmartButton(STD_BUTTON.HELP, ()->cancel()));
    }

    private void undo() {
        HeroCreationMaster.undo();
    }
    private void redo() {
        HeroCreationMaster.redo();
    }

    private void cancel() {
    }
}
