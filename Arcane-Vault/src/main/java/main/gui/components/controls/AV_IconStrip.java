package main.gui.components.controls;

import libgdx.gui.generic.btn.ButtonStyled;
import main.v2_0.AV2;
import main.swing.generic.components.G_Panel;

public class AV_IconStrip extends G_Panel {

    protected AvIconBtn save;
    protected AvIconBtn backup;
    protected AvIconBtn undo;
    protected AvIconBtn redo;

    public AV_IconStrip() {
        // setLayout(new HorizontalLayout(10));
        add(undo = new AvIconBtn(()-> AV2.getButtonHandler().handle(false, false,""), ButtonStyled.STD_BUTTON.LE_UNDO, "Undo"), "sg z");
        add(save = new AvIconBtn(()-> AV2.getButtonHandler().handle(false, false,""), ButtonStyled.STD_BUTTON.REPAIR, "Save"), "sg z");
        add(backup = new AvIconBtn(()-> AV2.getButtonHandler().handle(false,false, ""), ButtonStyled.STD_BUTTON.CHEST, "Commit"), "sg z");
        add(redo = new AvIconBtn(()-> AV2.getButtonHandler().handle(false, false,""), ButtonStyled.STD_BUTTON.LE_REDO, "Redo"), "sg z");

    }
}
