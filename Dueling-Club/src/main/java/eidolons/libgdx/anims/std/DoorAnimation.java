package eidolons.libgdx.anims.std;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.bf.grid.cell.BaseView;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.texture.TextureCache;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 10/15/2017.
 */
public class DoorAnimation extends Anim {
    boolean open;
    private Image newImage;

    public DoorAnimation(Event e) {
        super(e.getRef().getEntity(KEYS.ACTIVE), new AnimData());
        open = e.getType() == STANDARD_EVENT_TYPE.DOOR_OPENS;

    }

    @Override
    public void start() {
        setDuration(1);
        Obj door = getRef().getTargetObj();
        BaseView actor = ScreenMaster.getGrid().getViewMap().get(
         door);
//        ActorMaster.addFadeInOrOut(actor.getPortrait(), getDuration());
        if (open) {
            String path = door.getImagePath();
            path = StringMaster.cropFormat(path) + "_open" + ".png";
//            if (newImage == null)
            { //TODO as an afterAction!
                TextureRegion r = TextureCache.getOrCreateR(path);
                actor.setOriginalTextureAlt(r);
//                newImage = new Image(r);
            }
        } else {
            actor.setOriginalTextureAlt(null);
//            newImage = actor.getAltPortrait();
        }
//        newImage.setColor(1, 1, 1, 0);
//        actor.setAltPortrait(actor.getPortrait());
//        actor.setPortrait(newImage);
//        actor.addActor(newImage);
//        ActorMaster.addFadeInOrOut(actor.getPortrait(), getDuration());
        super.start();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
