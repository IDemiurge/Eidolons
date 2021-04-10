package libgdx.particles.ambi;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import libgdx.particles.EmitterActor;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.utils.GdxTimeMaster;
import main.content.enums.GenericEnums;
import main.system.auxiliary.RandomWizard;

/**
 * Created by JustMe on 1/10/2017.
 */
public class Ambience extends EmitterActor {
    private static boolean modifyParticles;
    float moveSpeedMax = 12;
    float moveRadius = 300;
    Vector2 velocity;
    Vector2 acceleration;
    Vector2 originPos;
    private boolean blocked;

    public Ambience(String path) {
        super(path);
    }

    public Ambience(GenericEnums.VFX fx) {
        super(fx);
    }

    public static void setModifyParticles(boolean modifyParticles) {
        Ambience.modifyParticles = modifyParticles;
    }

    @Override
    public void act(float delta) {
        if (!ParticleManager.isAmbienceOn())
            return;
        if (!isVisible())
            return;
        if (isCullingOn())
            if (DungeonScreen.getInstance().controller != null)
                if (!DungeonScreen.getInstance().controller.
                 isWithinCamera(getX(), getY(), getWidth() * 2, getHeight() * 2)) {
                    return;
                }
        // super.act(delta); not needed
        if (!isMoveOn()) {
            return;
        }
        if (acceleration == null) {
            return;
        }
        setPosition(getX() + velocity.x * delta, getY() + velocity.y * delta);

        if (!GdxTimeMaster.isPeriodNow(5)) {
            return;
        }
        float angle = acceleration.angle();
        float dst = originPos.dst(new Vector2(getX(), getY()));
        if (dst > moveRadius) {
            angle += RandomWizard.getRandomInt(360);
            acceleration.setAngle(angle);
        }
        velocity.add(acceleration);
        velocity = velocity.limit(3);

    }

    protected boolean isMoveOn() {
        return true;
    }

    public void added() {
        originPos = new Vector2(getX(), getY());
        acceleration = new Vector2(1, 1);
        velocity = new Vector2(0, 0);
    }

    @Override
    public float getWidth() {
        //        FuncMaster.getGreatest()
        //        getEffect().getEmitters().forEach(e->
        //        e.getSpawnWidth().getHighMax());
        return 400;
    }

    @Override
    public float getHeight() {
        return 400;
    }

    @Override
    public void draw(Batch spriteBatch, float delta) {
        if (blocked)
            return;
        if (!ParticleManager.isAmbienceOn())
            return;
        if (isCullingOn())
            if (DungeonScreen.getInstance().controller != null)
                if (!DungeonScreen.getInstance().controller.
                 isWithinCamera(getX(), getY(), getWidth(), getHeight())) {
                    return;
                }
        if (modifyParticles) {
            getEffect().modifyParticles();
        }
        // try {
            super.draw(spriteBatch, delta);
        // } catch (Exception e) {
        //     main.system.ExceptionMaster.printStackTrace(e);
        //     main.system.auxiliary.log.LogMaster.log(1, "AMBIENCE_VFX FAILED: " + getPath());
        //     blocked = true;
        // }
    }

    protected boolean isCullingOn() {
        return true;
    }

}
