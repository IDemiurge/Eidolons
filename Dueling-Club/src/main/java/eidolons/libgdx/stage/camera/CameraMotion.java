package eidolons.libgdx.stage.camera;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import eidolons.libgdx.anims.actions.FloatActionLimited;

public class CameraMotion {

    private final FloatAction speedActionY;
    private final FloatAction speedActionX;
    private final Vector2 dest;
    CameraMan cameraMan;
    private Interpolation interpolation;
    private float lastCamX=-1;
    private float lastCamY=-1;

    public CameraMotion( CameraMan cameraMan, float duration, Vector2 dest , Interpolation interpolation){
        this.cameraMan = cameraMan;
        this.interpolation = interpolation;
        this.dest = dest;
        speedActionX =initAction(duration, dest, true);
        speedActionY =initAction(duration, dest, false);
    }



    private FloatAction initAction(float duration, Vector2 dest, boolean x) {
        FloatAction action=new FloatActionLimited();
        action.setStart(x? cameraMan.getCam().position.x: cameraMan.getCam().position.y);
        action.setEnd(x? dest.x : dest.y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public boolean act(float delta){
        speedActionX.act(delta);
        speedActionY.act(delta);
        if (speedActionX.getTime() >= speedActionX.getDuration()) {
        if (speedActionY.getTime() >= speedActionY.getDuration()) {
            return false;
        }
        }
        float offsetX =lastCamX==-1? 0: cameraMan.getCam().position.x - lastCamX;
        float offsetY =lastCamY==-1? 0: cameraMan.getCam().position.y - lastCamY;
        float x = Math.round(speedActionX.getValue())+offsetX;
        float y = Math.round(speedActionY.getValue())+offsetY;
//        main.system.auxiliary.log.LogMaster.dev("Camera pan to " + x + " " + y + "; time ==" + speedActionX.getTime()+
//                "; dest==" + dest);
        cameraMan.getCam().position.set(x, y, 0);

        lastCamX = cameraMan.getCam().position.x;
        lastCamY = cameraMan.getCam().position.y;
        return true;
    }
}
