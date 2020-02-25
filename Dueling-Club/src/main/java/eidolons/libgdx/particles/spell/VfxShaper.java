package eidolons.libgdx.particles.spell;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.actions.MoveByActionLimited;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.particles.PhaseVfx;
import eidolons.libgdx.particles.VfxContainer;

/**
 * Created by JustMe on 11/28/2018.
 */
public class VfxShaper {

    public static VfxContainer<PhaseVfx> shape(String path, VFX_SHAPE shape, Vector2 origin, Vector2 destination) {
        float angle = destination.angleRad(origin) * 360;
        main.system.auxiliary.log.LogMaster.log(1, "Force attack with angle of " + angle);
        return shape(32, 32, 128, 1.5f * 128, angle, path, shape, origin, destination);
    }

    public static VfxContainer<PhaseVfx> shape(float w, float h,float shapeW,
     float shapeH, float rotate, String path, VFX_SHAPE shape,
                                               Vector2 origin, Vector2 destination
                                               //         , boolean flipX,boolean flipY
    ) {
        float scale;

        int y = (int) (shapeH / h);
        int x = (int) (shapeW / w);

        VfxContainer container = new VfxContainer(path);
        float pixelPerSecond = 300;
        float dst;

        switch (shape) {
            case TRIANGLE:
                int n=5;
                //via 2 lines...
                int length = (n - 1) / 2;
                float segment = h / length;
                float angle=60/360;
                createLine(segment, 0, length, h/length, path, container, (float) Math.sin (angle));
                createLine(0,  segment, length, h/length, path, container, (float) Math.cos (angle));

                //1 for vertex
                createPhaseVfx(path, container, 0, 0);
                break;
            case LINE:
                createPhaseVfx(path, container, 0, 0);
//                createLine(n, path, container, 1);
                break;
            case WAVE:
                //perpendicular movement
                //use sin from 1 to 0
                int bends = 3;
                  segment = (int) (shapeH / bends);
                for (int i = 0; i < y; i++) {
                    //how many bends?
                    int posY = (int) (i * h);
                    int posX = (int) (shapeW * Math.sin((i * h % segment) / segment));
                    dst = new Vector2(origin.x + posX, origin.y + posY).dst(destination);

                    PhaseVfx vfx =
                     createPhaseVfx(path,container, posX, posY);
                    vfx.setTimeToNext(dst / pixelPerSecond);
                    int segmentN = (int) (i * h / segment);

                    vfx.setActionManger(() -> {
                        int dest = segmentN % 2 == 0 ? 0 : (int) shapeW;
                        if (vfx.getActionsOfClass(MoveByActionLimited.class).size == 0)
                            ActionMaster.addMoveByAction(vfx, dest, posY, 0.5f);
                        ActionMaster.addAfter(vfx, new Action() {
                            @Override
                            public boolean act(float delta) {
                                vfx.getEffect().allowCompletion();
                                return false;
                            }
                        });
                    });
                    // blend
                    break;
                }
            case WHIP:
            case HALF_CIRCLE:
            case CIRCLE:
                break;
        }

        container.setRotation(rotate);

        //        applyFlip(set,flipX, flipY);
        //        applyRotate(set,rotate);
        //        applyDestination(set,destination);
        return container;
    }

    private static void createLine( float x,
     float y, int length,float segment, String path, VfxContainer container, float sin) {
        for (int i = 0; i < length; i++) {
            x += segment * sin;
            y += segment / sin;
            createPhaseVfx(path, container, x, y);
        }
    }

    private static PhaseVfx createPhaseVfx(String path, VfxContainer container, float posX, float posY) {
        PhaseVfx vfx = PhaseVfxPool.getEmitterActor(path, ANIM_PART.MISSILE, ANIM_PART.IMPACT);
        container.add(vfx);
        vfx.setPosition(posX, posY);
        return vfx;
    }

    public enum VFX_SHAPE {
        LINE, TRIANGLE,
        WAVE, WHIP, CIRCLE, HALF_CIRCLE,
    }
}
