package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import main.libgdx.StyleHolder;

public class ButtonStyled extends Button {

    public enum STD_BUTTON{
        OK("UI/components/small/ok.png"),
        CANCEL("UI/components/small/no.png"),
        UNDO("UI/components/small/back2.png"),
//        NEXT, LEVEL_UP,
        ;
        String path;

        STD_BUTTON(String path) {
            this.path = path;
        }
    }

    public ButtonStyled(  STD_BUTTON b) {
        this(b.path);
    }

    public ButtonStyled(String backGroundPath) {
        super(StyleHolder.getCustomButtonStyle(backGroundPath));
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
    }
}
