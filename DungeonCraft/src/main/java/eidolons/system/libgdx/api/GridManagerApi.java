package eidolons.system.libgdx.api;

import com.badlogic.gdx.math.Interpolation;
import eidolons.content.consts.VisualEnums;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.VoidMazeHandler;
import eidolons.system.libgdx.wrapper.VectorGdx;
import main.entity.Entity;
import main.game.bf.Coordinates;

public interface GridManagerApi {
    VectorGdx getCenteredPos(Coordinates coordinate);
    Coordinates getCameraCenter();
    Boolean checkPlatform(Entity unit, Coordinates c);

    void doZoom(float zoom, float dur, Interpolation interpolation);

    void doShake(float dur, Boolean vert, VisualEnums.ScreenShakeTemplate temp);

    void doParticles(VisualEnums.PARTICLES_SPRITE sprite, float v);

    void initVoidHandler(VoidMazeHandler voidMazeHandler);
}
