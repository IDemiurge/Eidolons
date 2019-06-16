package eidolons.libgdx.anims.std.sprite;

import main.entity.Ref;
import main.game.logic.event.Event;

public class LockKeyAnimation extends CustomSpriteAnim {

    public LockKeyAnimation(Event e) {
        super(e.getRef().getEntity(Ref.KEYS.ACTIVE),"locks/" +"Jade Key");
    }
    //                        e.getRef().getObj(Ref.KEYS.ITEM).getName()

}
