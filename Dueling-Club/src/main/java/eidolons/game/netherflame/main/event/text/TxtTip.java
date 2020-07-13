package eidolons.game.netherflame.main.event.text;

import eidolons.system.text.Texts;
import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;

public enum TxtTip implements TextEvent {
    tester_welcome,

    ;
    private final boolean optional = true;
    private final boolean once = true;
    private boolean done;
    private String img;
    private String message;
    private final WaitMaster.WAIT_OPERATIONS messageChannel = MESSAGE_RESPONSE;

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
        if (message == null) {
            message = Texts.getTextMap("tips").get(name());
        }
        return message;
    }

    @Override
    public WaitMaster.WAIT_OPERATIONS getMessageChannel() {
        return messageChannel;
    }
}

