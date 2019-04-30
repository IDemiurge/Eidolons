package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.btn.SmartButton;

public class TipMessageSource {
    String message;
    String image;
    String[] buttons;
    Runnable[] btnRun;
    private boolean optional;


    public TipMessageSource(String message, String image, String  button ,boolean optional, Runnable r) {
        this.message = message;
        this.image = image;
        this.optional = optional;
        this.buttons = new String[]{
                button
        };
        this.btnRun = new Runnable[]{
                r
        };
    }
    public TipMessageSource(String message, String image, String[] buttons, Runnable[] btnRun) {
        this.message = message;
        this.image = image;
        this.buttons = buttons;
        this.btnRun = btnRun;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public String[] getButtons() {
        return buttons;
    }

    public Runnable[] getBtnRun() {
        return btnRun;
    }

    public void setRunnable(Runnable runnable) {
        this.btnRun = new Runnable[]{
                runnable
        };
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
