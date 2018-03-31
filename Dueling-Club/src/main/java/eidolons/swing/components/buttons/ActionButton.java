package eidolons.swing.components.buttons;

import main.swing.generic.services.listener.ClickListenerEnum;

import java.awt.*;

public class ActionButton extends ActionButtonEnum<String> {

    public ActionButton(String text, Image image, String command,
                        ClickListenerEnum<String> actionListener) {
        super(text, image, command, actionListener);
    }
}
