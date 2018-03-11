package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.libgdx.gui.UiMaster;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 3/9/2018.
 */
public class SpellRadialContainer extends RadialValueContainer {
    public SpellRadialContainer(TextureRegion texture, Runnable action) {
        super(texture, action);
    }

    public SpellRadialContainer(TextureRegion textureRegion, Runnable runnable, boolean valid, DC_ActiveObj activeObj, DC_Obj target) {
        super(textureRegion, runnable, valid, activeObj, target);
         }

    @Override
    protected void initSize() {
        overrideImageSize(UiMaster.getSpellIconSize(), UiMaster.getSpellIconSize());

    }
}
