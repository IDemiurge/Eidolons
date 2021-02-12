package libgdx.adapters;

import com.badlogic.gdx.math.Interpolation;
import eidolons.content.consts.VisualEnums;
import eidolons.system.libgdx.api.GridManagerApi;
import eidolons.system.libgdx.wrapper.Vector2;
import libgdx.anims.fullscreen.Screenshake;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.moving.PlatformController;
import libgdx.particles.ParticlesSprites;
import libgdx.screens.ScreenMaster;
import libgdx.stage.camera.MotionData;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class GridManagerApiImpl implements GridManagerApi {
    @Override
    public Boolean checkPlatform(Entity unit, Coordinates c) {
        if (ScreenMaster.getGrid() == null) {
            return null;
        }
        PlatformController platformController =
                ScreenMaster.getGrid().getPlatformHandler().get(c);
        if (platformController != null)
            if (platformController.canEnter(unit)) {
                return true;
            }
        return null;
    }

    @Override
    public void doZoom(float zoom, float dur, Interpolation interpolation) {
        GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, new MotionData(zoom, dur, interpolation));
    }

    @Override
    public void doShake(float dur, Boolean vert, VisualEnums.ScreenShakeTemplate temp) {
        GuiEventManager.trigger(GuiEventType.CAMERA_SHAKE, new Screenshake(dur, vert, temp));
    }

    @Override
    public void doParticles(VisualEnums.PARTICLES_SPRITE sprite, float v) {
        ParticlesSprites.doParticles(sprite, v);
    }

    @Override
    public Vector2 getCenteredPos(Coordinates coordinate) {
        com.badlogic.gdx.math.Vector2 v = GridMaster.getCenteredPos(coordinate);
        return new Vector2(v.x, v.y);
    }

    @Override
    public Coordinates getCameraCenter() {
        return  GridMaster.getCameraCenter();
    }
}
