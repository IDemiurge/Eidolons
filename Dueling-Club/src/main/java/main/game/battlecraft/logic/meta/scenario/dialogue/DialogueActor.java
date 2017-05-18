package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.game.core.game.Game;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueActor extends LightweightEntity {
    public DialogueActor(ObjType type) {
        super(type);
//        name, portrait, description
//        toBase(); copy maps?
    }

    @Override
    public Game getGame() {
        return null;
    }
}
