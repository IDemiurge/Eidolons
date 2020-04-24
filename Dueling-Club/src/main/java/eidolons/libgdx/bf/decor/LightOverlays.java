package eidolons.libgdx.bf.decor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.screens.CustomSpriteBatch;
import main.content.enums.GenericEnums;
import main.data.XLinkedMap;

import java.util.Map;

public class LightOverlays extends GroupX {

    public enum LIGHT_MAP_OVERLAY{
        SUN,
        MOON,
        PALE,
    CLOUDS,

        COLORLESS,
        //could combine
    }

    boolean fullscreen;
    Map<Vector2, LightOverlay> map = new XLinkedMap<>();

    private static class LightOverlay {
        SpriteX sprite;
        LIGHT_MAP_OVERLAY type;
        Vector2 acceleration;
        Vector2 speed;

        Fluctuating fluctuating;

        public LightOverlay(LIGHT_MAP_OVERLAY type) {
            this.type = type;
        }
    }

    public void reset(){
        map.put(new Vector2(0, 0), new LightOverlay(LIGHT_MAP_OVERLAY.CLOUDS));

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        }
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending( );
        }
    }

    /**
     * especially for PA!
     */

}
