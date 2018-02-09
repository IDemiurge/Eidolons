package main.libgdx.screens.map.obj;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import main.libgdx.bf.BaseView;

/**
 * Created by JustMe on 2/7/2018.
 */
public class MapActor extends BaseView {
    MAP_OBJ_INFO_LEVEL infoLevel;

    public MapActor(TextureRegion portraitTexture) {
        super(portraitTexture);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void move(Vector2 destination) {

    }

    @Override
    public float getWidth() {
        return portrait.getWidth();
    }

    @Override
    public float getHeight() {
        return portrait.getHeight();
    }

    public enum MAP_OBJ_INFO_LEVEL {

    }
}
