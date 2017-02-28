package main.libgdx.bf.controls.radial;

import com.badlogic.gdx.graphics.Texture;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/28/2017.
 */
public class RadialManager {

    public static Texture getTextureForActive(DC_ActiveObj obj, DC_Obj target ) {
        Ref ref = obj.getOwnerObj().getRef().
         getTargetingRef(target);
        return getTextureForActive(obj, ref);
    }
    public static Texture getTextureForActive(DC_ActiveObj obj, Ref ref) {
        return !obj.canBeActivated(ref) ?
         TextureCache.getOrCreateGrayscale(obj.getImagePath())
         : TextureCache.getOrCreate(obj.getImagePath());
    }
}
