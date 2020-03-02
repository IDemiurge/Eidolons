package main.level_editor.gui.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.panels.TabbedPanel;

public class LE_Palette extends TabbedPanel {

    public void init(){


//    ScrollPanel scrollPanel = new ScrollPanel(); //for tabs?
}

    protected int getWrap() {
        return 30;
    }

    @Override
    public void addTab(Actor actor, String tabName) {
        super.addTab(actor, tabName);
    }

    @Override
    protected TextButton.TextButtonStyle getTabStyle() {
//        VisUI.getSkin().getTiledDrawable()
        return StyleHolder.getTabStyle(StyleHolder.getButtonStyle(ButtonStyled.STD_BUTTON.HIGHLIGHT_ALT));
    }
}
