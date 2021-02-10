package eidolons.ability.effects.oneshot.dungeon;

import eidolons.ability.effects.DC_Effect;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj;

/**
 * Created by JustMe on 9/23/2017.
 */
public class LockEffect extends DC_Effect {

    String identifier;

    public LockEffect(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean applyThis() {
        DungeonObj obj = (DungeonObj) ref.getTargetObj();
        //check traps!
        if (identifier != null) {
            if (!obj.getName().equalsIgnoreCase(identifier))
                return false;
        }
        boolean key = true;
        if (!key) {
            //try
        }

        obj.getDM().open(obj, ref);

        return true;
    }
}
