package eidolons.system.libgdx.api;

import com.badlogic.gdx.math.Interpolation;
import eidolons.content.consts.VisualEnums;
import eidolons.system.libgdx.wrapper.Vector2;
import main.entity.Entity;
import main.game.bf.Coordinates;

public interface GridManagerApi {
    Vector2 getCenteredPos(Coordinates coordinate);
    Coordinates getCameraCenter();
    Boolean checkPlatform(Entity unit, Coordinates c);

    void doZoom(float zoom, float dur, Interpolation interpolation);

    void doShake(float dur, Boolean vert, VisualEnums.ScreenShakeTemplate temp);

    void doParticles(VisualEnums.PARTICLES_SPRITE sprite, float v);
}
