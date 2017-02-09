package main.swing.components.buttons;

import main.swing.generic.services.listener.ClickListenerEnum;

import java.awt.*;

public class ActionButtonEnum<E> extends CustomButton {

    private ClickListenerEnum<E> clickListener;
    private E command;

    public ActionButtonEnum(String text, Image image, E command, ClickListenerEnum<E> actionListener) {
        super(text, image);
        this.clickListener = actionListener;
        this.command = command;
        if (text == null) {
            setDefaultSize(new Dimension(image.getWidth(null), image.getHeight(null)));
        }
    }

    @Override
    public void handleAltClick() {
        clickListener.handleClick(command, true);

    }

    @Override
    public void handleClick() {
        clickListener.handleClick(command, false);

    }

}
