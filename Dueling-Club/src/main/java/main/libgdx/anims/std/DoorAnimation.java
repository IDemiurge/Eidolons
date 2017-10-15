package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimData;
import main.libgdx.bf.BaseView;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
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
        BaseView actor = DungeonScreen.getInstance().getGridPanel().getUnitMap().get(
         door);
        ActorMaster.addFadeInOrOut(actor.getPortrait(), getDuration());
        if (open) {
            String path = door.getImagePath();
            path = StringMaster.cropFormat(path) + " open" + ".png";
            if (newImage == null)
                newImage = new Image(TextureCache.getOrCreateR(path));
        } else {
            newImage = actor.getAltPortrait();
        }
        newImage.setColor(1, 1, 1, 0);
        actor.setAltPortrait(actor.getPortrait());
        actor.setPortrait(newImage);
        actor.addActor(newImage);
        ActorMaster.addFadeInOrOut(actor.getPortrait(), getDuration());
        super.start();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}