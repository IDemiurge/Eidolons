package logic.functions;

import gdx.general.anims.ActionAnims;
import gdx.visuals.lanes.LaneConsts;
import logic.lane.HeroPos;

public class MoveLogic extends LogicController {
    public static final int INVALID_STEP = -1;
    public static final int INVALID_JUMP = -2;
    public static final int MID_JUMP = 0;
    public static final int REVERSE_JUMP = 1;
    public static final int NORMAL = 2;
    public static final String INVALID_MOVE_1 = "Can't step over Barrier, only jump";
    public static final String INVALID_MOVE_2 = "Can't jump into Barrier!";

    public MoveLogic(GameController controller) {
        super(controller);
    }

    public void move_(int length, boolean direction) {
        HeroPos prev = hero.getPos();
        int move = getMoveType(prev, length, direction);
        switch (move) {
            case INVALID_STEP -> inputError(INVALID_MOVE_1);
            case INVALID_JUMP -> inputError(INVALID_MOVE_2);

            case MID_JUMP -> sideJump(prev, true);
            case REVERSE_JUMP -> sideJump(prev, false);
            case NORMAL -> step(length, direction, prev);
        }

    }

    private void step(int length, boolean direction, HeroPos prev) {
        if (prev.isLeftSide())
            direction = !direction;
        int mod = direction ? length : -length;
        HeroPos pos = new HeroPos(prev.getCell() + mod, prev.isLeftSide()); //TODO
        hero.setPos(pos);
        boolean triggered = false;
//        HeroView view = FrontField.get().getView(hero);
        if (triggered) {
//            GuiEventManager.trigger(GuiEventType. )
        } else {
            //direct call - but we can't do it all from logic thread, eh?
            ActionAnims.moveHero(view, prev, pos, NORMAL);
        }
    }

    private void sideJump(HeroPos prev, boolean mid) {
        HeroPos pos = new HeroPos(prev.getCell(), !prev.isLeftSide());
        hero.setPos(pos);
        ActionAnims.sideJump(mid, view, prev, pos);
    }

    private int getMoveType(HeroPos prev, int length, boolean direction) {
        int cell = prev.getCell();
//        int toEdge = Math.min(Math.abs(LaneConsts.CELLS_PER_SIDE - cell), cell);

        int destination=0;

        if (!prev.isLeftSide())
            direction = !direction;
        if (direction)
            destination = cell - length;
        else
            destination = cell + length;

        System.out.println("Destination: " + destination);

        if (destination==-1 || destination== LaneConsts.CELLS_PER_SIDE){
            if (length > 1)
                return INVALID_JUMP;
            return INVALID_STEP;
        }
        if (destination < -1){
            return MID_JUMP;
        }
        if (destination >  LaneConsts.CELLS_PER_SIDE){
            return REVERSE_JUMP;
        }
        return NORMAL;
    }


}
