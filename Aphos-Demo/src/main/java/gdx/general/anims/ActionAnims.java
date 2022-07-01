package gdx.general.anims;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import gdx.views.HeroView;
import gdx.views.UnitView;
import libgdx.anims.actions.ActionMasterGdx;
import logic.lane.HeroPos;
import logic.lane.LanePos;

public class ActionAnims {
    /*
    via gdx actions
     */

    public static void hit(UnitView view) {

    }

    public  static void moveUnit(UnitView view, LanePos prevPos, LanePos pos) {
        if (prevPos.lane != pos.lane){
//            jumpLane()
        }
        float scale = getScale(pos.pos);
        float dur= 1;
        ScaleToAction scaleAction = ActionMasterGdx.getScaleAction(scale, dur);
        scaleAction.setInterpolation(Interpolation.smooth);
        scaleAction.setTarget(view);
        view.addAction(scaleAction);

        MoveToAction moveToAction = ActionMasterGdx.getMoveToAction(getX(pos), getY(pos), dur);
        scaleAction.setInterpolation(Interpolation.smooth);
        scaleAction.setTarget(view);
        view.addAction(scaleAction);

    }

    private static float getScale(int pos) {
        return 1 - pos*0.15f + pos*pos*0.015f;
        /*
        return 1 - pos*0.2f + pos*pos*0.02f;
        1: 0.82
        2: 0.66
        3: 0.56
        4: 0.52
         */
    }

    public  static void moveHero(HeroView view, HeroPos prevPos, HeroPos pos) {
        boolean jump = Math.abs(pos.getCell() - prevPos.getCell()) >=2;
        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setInterpolation(jump ? Interpolation.swingIn : Interpolation.smooth);

        float dur = 1;
        moveToAction.setDuration(dur);
        float x = getX(pos);
        float y = getY(pos);
        moveToAction.setPosition(x, y);
        moveToAction.setTarget(view);
        view.addAction(moveToAction);

    }

    private static float getY(LanePos pos) {
        return 0;
    }

    private static float getX(LanePos pos) {
        //TODO
        return 0;
    }
    private static float getY(HeroPos pos) {
        //Just Diagonal? Aye, but not 45 degrees, or?
        return 0;
    }

    private static float getX(HeroPos pos) {
        //TODO
        return 0;
    }
}
