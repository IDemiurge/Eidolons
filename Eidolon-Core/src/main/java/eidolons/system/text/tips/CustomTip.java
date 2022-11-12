package eidolons.system.text.tips;

import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;

public class CustomTip implements TextEvent {

    private   boolean optional  ;
    private   boolean once  ;
    private boolean done;
    private String img;
    private final WaitMaster.WAIT_OPERATIONS messageChannel = MESSAGE_RESPONSE;
    private final String msg;
    private  Runnable runnable;

    public CustomTip(String msg) {
        this.msg = msg;
    }

    public CustomTip(String img, String msg) {
        this.img = img;
        this.msg = msg;
    }

    public CustomTip(String msg, Runnable runnable) {
        this.msg = msg;
        this.runnable = runnable;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean isOnce() {
        return once;
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public String getImg() {
        return img;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public WaitMaster.WAIT_OPERATIONS getMessageChannel() {
        return messageChannel;
    }

    @Override
    public void run() {
        runnable.run();
    }
}
