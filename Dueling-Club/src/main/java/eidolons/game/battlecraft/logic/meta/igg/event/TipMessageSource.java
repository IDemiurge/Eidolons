package eidolons.game.battlecraft.logic.meta.igg.event;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.*;

public class TipMessageSource {
    public WaitMaster.WAIT_OPERATIONS msgChannel;
    public String title;
    String message;
    String image;
    String[] buttons;
    Runnable[] btnRun;
    private boolean optional;
    float width = GdxMaster.getWidth() / 3;
    float height = GdxMaster.getHeight() / 3;

    public TipMessageSource(String message, String image, String button, boolean optional, Runnable r) {
        this(message, image, button, optional, r, MESSAGE_RESPONSE);
    }

    public TipMessageSource(String message, String image, String button, boolean optional, Runnable r, WaitMaster.WAIT_OPERATIONS msgChannel) {
        this.message = message;
        this.image = image;
        this.optional = optional;
        this.msgChannel = msgChannel;
        this.buttons = new String[]{
                button
        };
        this.btnRun = new Runnable[]{
                r
        };
        if (image != null) {
            height=height*3/2;
        }
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

}
