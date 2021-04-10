package eidolons.system.text.tips;

import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;

public enum PuzzleTip implements TxtTip {

    encounter_puzzle_intro,
    encounter_puzzle_win,
    encounter_puzzle_defeat,

    void_maze_intro,
    void_maze_intro_2,
    void_maze_intro_3,
    void_maze_win,
    void_maze_defeat_first,
    void_maze_defeat,


    art_puzzle_intro,
    art_puzzle_win,
    art_puzzle_defeat,
    art_puzzle_intro_move,
    art_puzzle_win_portal,
    art_puzzle_defeat_awaken,

    maze_puzzle_intro,
    maze_puzzle_win,
    maze_puzzle_defeat_first,
    maze_puzzle_defeat,
    maze_puzzle_defeat_awaken,
    ;

    PuzzleTip() {
        //from txt!
    }

    PuzzleTip(String img) {
        this.img = img;
    }

    private boolean done;
    private String img; //TODO what about this one?
    private final WaitMaster.WAIT_OPERATIONS messageChannel = MESSAGE_RESPONSE;

    @Override
    public String getMapId() {
        return  "puzzle_tips";
    }

    @Override
    public boolean isOptional() {
        boolean optional = true;
        return optional;
    }

    @Override
    public boolean isOnce() {
        boolean once = true;
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
    public WaitMaster.WAIT_OPERATIONS getMessageChannel() {
        return messageChannel;
    }

    @Override
    public void run() {

    }

}
