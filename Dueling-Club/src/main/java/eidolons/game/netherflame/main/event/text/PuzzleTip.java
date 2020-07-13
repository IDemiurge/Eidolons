package eidolons.game.netherflame.main.event.text;

import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;

public enum PuzzleTip implements TextEvent {

    void_maze_intro("Welcome to the Void Maze. Best use double-click to quickly " +
            "navigate the cells that appear before you"),
    void_maze_intro2(""),
    void_maze_win("Void Maze Completed"),
    void_maze_defeat_first(""),
    void_maze_defeat(""),
;

    PuzzleTip(String message) {
        this.message = message;
    }

    PuzzleTip(String img, String message) {
        this.img = img;
        this.message = message;
    }

    private final boolean optional = true;
    private final boolean once=true ;
    private boolean done ;
    private String img;
    private final String message;
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
        return message;
    }

    @Override
    public WaitMaster.WAIT_OPERATIONS getMessageChannel() {
        return messageChannel;
    }
}
