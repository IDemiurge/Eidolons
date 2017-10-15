package main.ability.effects.oneshot.dungeon;

import main.ability.effects.DC_Effect;
import main.game.module.dungeoncrawl.objects.DungeonObj;

/**
 * Created by JustMe on 9/23/2017.
 */
public class LockEffect extends DC_Effect {

    String identifier;
    private boolean key=true;

    public LockEffect(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean applyThis() {
        DungeonObj obj = (DungeonObj) ref.getTargetObj();
        //check traps!
        if (identifier!=null ){
            if (!obj.getName().equalsIgnoreCase(identifier))
                return false;
        }
        if (!key){
            //try
        }

        obj.getDM().open(obj,ref );

        return true;
    }
}
