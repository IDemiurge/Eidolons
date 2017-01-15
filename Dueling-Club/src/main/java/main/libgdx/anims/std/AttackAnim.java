package main.libgdx.anims.std;

/**
 * Created by JustMe on 1/14/2017.
 */
public class AttackAnim {
    float overswing;
    float backswing;
    float preswing;
    float zoomOut;
    float zoomIn;
    float acceleration;
    float startSpeed;
    int force;
    public enum ATK_ANIMS {
        THRUST_LANCE,

        STAB,
        JAB,
        POKE,
        SLASH,
        POLE_SMASH,
        HEAVY_SWING(),;
        float baseX;// -1 to 1 from 0 to 100% of width/height of source unit view
        float baseY;
        float baseOffsetX; // in pixels
        float baseOffsetY;
        float targetX;
        float targetY;
        float targetOffsetX;
        float targetOffsetY;
        float baseAngle; // 0 - horizontal; 90 - vertical
        float[] targetAngles; // will assume each one in turn during animation
        float overswing; // go over the target's face-mark
        float backswing; // return after impact
        float preswing; // draw back before atk

        float zoomOut; // уменьшить дальний конец при атаке
        float zoomIn;// увеличить дальний конец до атаки
        float startSpeed;
        float acceleration; // +speed per second

        float rotationPointOffset;

        ATK_ANIMS(float... params) {

        }
    }

//    bloodTemplate; from real bleeding amount?
//    sparks;
}
