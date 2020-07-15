package eidolons.game.netherflame.main.event.text;

import main.system.threading.WaitMaster;

public interface TextEvent {
    boolean isOptional();

    boolean isOnce();

    void setDone(boolean done);

    boolean isDone();

    String getImg();

    String getMessage();

    WaitMaster.WAIT_OPERATIONS getMessageChannel();

    void run();

    void setMessage(String value);
}
