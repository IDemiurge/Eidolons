package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by EiDemiurge on 9/29/2018.
 */
public class NoHitGroup extends GroupX {
    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }
}
