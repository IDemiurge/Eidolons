package main.level_editor.gui.top;

import libgdx.gui.generic.ValueContainer;

public class LE_StatusContainer extends ValueContainer {
    public LE_StatusContainer(String title, String value) {
        super(title, value);
        // setWidth();
        // setBackground();

    }

    @Override
    public void setValueText(CharSequence newText) {
        super.setValueText(newText);
        if (isFixedSize())
            return;
        setWidth(getPrefWidth()*1.25f);
        setFixedSize(true);
    }
}
