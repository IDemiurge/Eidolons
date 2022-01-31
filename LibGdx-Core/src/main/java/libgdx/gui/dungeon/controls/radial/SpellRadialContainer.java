package libgdx.gui.dungeon.controls.radial;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.active.ActiveObj;
import eidolons.entity.obj.DC_Obj;
import libgdx.gui.UiMaster;

/**
 * Created by JustMe on 3/9/2018.
 */
public class SpellRadialContainer extends RadialContainer {
    public SpellRadialContainer(TextureRegion texture, Runnable action) {
        super(texture, action);
    }

    public SpellRadialContainer(TextureRegion textureRegion, Runnable runnable, boolean valid, ActiveObj activeObj, DC_Obj target) {
        super(textureRegion, runnable, valid, activeObj, target);
    }

    @Override
    protected boolean isScaledOnHover() {
        return false;
    }

    @Override
    protected void initSize() {
        overrideImageSize(UiMaster.getSpellIconSize(), UiMaster.getSpellIconSize());

    }
}
