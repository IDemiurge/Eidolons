package libgdx.gui.panels.headquarters.creation;

import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.generic.btn.ButtonStyled;

/**
 * Created by JustMe on 7/6/2018.
 */
public class HcControlPanel extends TablePanelX{
    public HcControlPanel() {
        //symbol/title
        super(320, 64);
        add(new SymbolButton(ButtonStyled.STD_BUTTON.CANCEL, this::cancel));
        add(new SymbolButton(ButtonStyled.STD_BUTTON.UNDO, this::undo));
        add(new SymbolButton(ButtonStyled.STD_BUTTON.NEXT, this::redo));
        add(new SymbolButton(ButtonStyled.STD_BUTTON.HELP, this::cancel));
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
