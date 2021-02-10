package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.content.PROPS;
import eidolons.libgdx.gui.generic.GroupWithEmitters;
import eidolons.libgdx.texture.TextureCache;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.ContainerUtils;

import java.util.Collection;

/*
grid-anims
add/remove
non-interactive?

textures, sprites, vfx

support overlaying-direction
support rotation and flip

++ create in addition to overlays/.. - e.g. torches with cinders / smoke
wait, that is more like linked - just add DIRECTION to it

This one is LITE
- for custom vfx, mass-overlays - cracks, protrusions

visibility from cell - ideally separate for vfx/textures
 */
public class DecorObj extends GroupWithEmitters {
    //can we do w/o baseview?
    Coordinates c;
    DIRECTION d;
    ObjType type;

    public DecorObj(Coordinates c, DIRECTION d, ObjType type) {
        this.c = c;
        this.d = d;
        this.type = type;

        addTextures(type.getProperty(PROPS.TEXTURES_OVERLAY), true);
        addTextures(type.getProperty(PROPS.TEXTURES_UNDERLAY), false);
        //sprites?
        addVfx(type.getProperty(PROPS.VFX));

        //IDEA - attach to gridcell?

    }

    private void addTextures(String property, boolean over) {
        for (String texture : ContainerUtils.openContainer(property)) {
            Image actor;
            addActor(actor = new Image(TextureCache.getRegionTEX(texture)));
            //position??
            if (d != null) {
                actor.setRotation(d.getDegrees());
            }
            //add to container over/under
        }
    }

    private void addVfx(String property) {
        for(String substring: ContainerUtils.openContainer( property )){
            if (d != null) {
//                offsetX= OverlayingMaster.getOffsetsForOverlaying(d, w, h);
            }
//            String path= BfObjEnums.BF_VFX.valueOf(substring).path;
//            createEmitter(path, offsetX,offsetY);
        }
    }

    @Override
    public Collection getEmitters() {
        return null;
    }

}
