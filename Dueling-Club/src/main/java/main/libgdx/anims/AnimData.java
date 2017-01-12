package main.libgdx.anims;

import com.badlogic.gdx.graphics.Color;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.VALUE;
import main.libgdx.anims.particles.PARTICLE_EFFECTS;

import java.awt.*;
import java.util.Map;

/**
 * Created by JustMe on 1/12/2017.
 */
public class AnimData {

    float duration;
    float spriteDuration;
    float emitterDuration;

    String spriteImagePaths;
    PARTICLE_EFFECTS[] emitters; //other params?
    Color[] emitterColors;
    Point[] emitterOffsets;
    int[] emitterScales;

    int lightEmission;
    Color lightColor;

    Map<String, Object> dataMap;

    public void add(VALUE val, String value) {
        if (val instanceof PARAMS) {
            setParam ((PARAMS) val, value);
        }
        if (val instanceof PROPS) {
            setProp ((PROPS) val, value);
        }
    }

    private void setParam(PARAMS val, String value) {
    }

    private void setProp(PROPS val, String value) {
        switch(val){

        }
    }

    public enum ANIM_VALUES{

}

    public  AnimData(String... data){

    }


}
