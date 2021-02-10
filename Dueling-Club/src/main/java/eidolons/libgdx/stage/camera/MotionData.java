package eidolons.libgdx.stage.camera;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.GridMaster;
import main.game.bf.Coordinates;

import java.util.List;

public class MotionData {

    public Vector2 dest;
    public float duration;
    public float zoom;
    public Interpolation interpolation = Interpolation.fade;
    public Boolean exclusive = false;

    public MotionData(float zoom, float duration, Interpolation interpolation) {
        this(duration, interpolation, zoom);
    }

    public MotionData(Vector2 dest, float duration, Interpolation interpolation) {
        this.dest = dest;
        this.duration = duration;
        this.interpolation = interpolation;
    }

    public MotionData(Object... params) {
        duration = 0;
        for (Object param : params) {
            if (param instanceof List) {
                List list = ((List) param);
                for (Object o : list) {
                    initParam(o);
                }
            } else {
                initParam(param);
            }
        }
    }

    private void initParam(Object o) {
        if (o instanceof Boolean) {
            exclusive = (Boolean) o;
        }
        if (o instanceof Vector2) {
            dest = (Vector2) o;
        }
        if (o instanceof Coordinates) {
            dest = GridMaster.getCenteredPos((Coordinates) o);
        }
        if (o instanceof BattleFieldObject) {
            dest = GridMaster.getCenteredPos(((BattleFieldObject) o).getCoordinates());
        }
        if (o instanceof Interpolation) {
            interpolation = ((Interpolation) o);
        }
        if (o instanceof Float) {
            if (duration == 0) {
                duration = (float) o;
            } else zoom = (float) o;
        }
    }
}
