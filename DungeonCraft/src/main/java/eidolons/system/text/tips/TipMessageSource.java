package eidolons.system.text.tips;

import eidolons.system.libgdx.GdxStatic;
import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.CONFIRM;
import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;

public class TipMessageSource {
    public WaitMaster.WAIT_OPERATIONS msgChannel=CONFIRM;
    public String title;
    String message;
    String image;
    String[] buttons;
    String soundPath;
    Runnable[] btnRun;
    private boolean optional;
    private boolean nonGdxThread=true;
    float width = GdxStatic.getWidth() / 3;
    float height = GdxStatic.getHeight() / 3;


    public TipMessageSource(String message, String soundPath, String image, String button, boolean optional, Runnable r) {
        this(message, image, button, optional, r, MESSAGE_RESPONSE);
        this.soundPath = soundPath;
    }
    public TipMessageSource(String message, String image, String button, boolean optional, Runnable r) {
        this(message, image, button, optional, r, MESSAGE_RESPONSE);
    }

    public TipMessageSource(  String message, String image, String  buttons, Runnable  btnRun,
                              WaitMaster.WAIT_OPERATIONS msgChannel, boolean optional, boolean nonGdxThread) {
        this.message = message;
        this.image = image;
        this.optional = optional;
        this.msgChannel = msgChannel;
        this.nonGdxThread = nonGdxThread;
        this.buttons = new String[]{
                buttons
        };
        this.btnRun = new Runnable[]{
                btnRun
        };
         init();
    }

    private void init() {
        if (image != null) {
            height=height*3/2;
        }
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

    public void setMsgChannel(WaitMaster.WAIT_OPERATIONS msgChannel) {
        this.msgChannel = msgChannel;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setButtons(String[] buttons) {
        this.buttons = buttons;
    }

    public void setBtnRun(Runnable[] btnRun) {
        this.btnRun = btnRun;
    }

    public void setNonGdxThread(boolean nonGdxThread) {
        this.nonGdxThread = nonGdxThread;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isOptional() {
        return optional;
    }

    public WaitMaster.WAIT_OPERATIONS getMsgChannel() {
        return msgChannel;
    }

    public String getTitle() {
        return title;
    }

    public boolean isNonGdxThread() {
        return nonGdxThread;
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
