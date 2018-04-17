package eidolons.libgdx.gui.generic.btn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 12/1/2017.
 */
public class TextButtonX extends TextButton implements EventListener {

    private Runnable runnable;
    private boolean fixedSize;

    public TextButtonX(String text, TextButtonStyle style) {
        this(text, style, null);

    }


    @Override
    public float getPrefWidth() {
        if (isFixedSize())
            return getWidth();
        return super.getPrefWidth();
    }

    @Override
    public float getPrefHeight() {
        if (isFixedSize())
            return getHeight();
        return super.getPrefHeight();
    }

    public TextButtonX(String text, STD_BUTTON button, Runnable runnable,
                       FONT font, int size, Color color_) {
        this(text, StyleHolder.getTextButtonStyle(button,
         font, color_, size), runnable);
    }

    public TextButtonX(String text, STD_BUTTON button, Runnable runnable) {
        this(text, button, runnable,
         FONT.MAGIC, 20, GdxColorMaster.GOLDEN_WHITE);
    }

    public TextButtonX(String text, TextButtonStyle style, Runnable runnable) {
        super(text, style);
        this.runnable = runnable;
        addListener(this);
    }

    public TextButtonX(STD_BUTTON button) {
        this("", button, null);
    }

    @Override
    public boolean handle(Event e) {
        if (runnable == null)
            return true;
        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;
        if (event.getType() == Type.touchDown) {
            runnable.run();
        }
        return true;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }
}
