package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.entity.obj.unit.Unit;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.game.core.game.Game;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueActor extends LightweightEntity {
    private Unit linkedUnit;

    public DialogueActor(ObjType type) {
        super(type);
//        name, portrait, description
//        toBase(); copy maps?
    }

    @Override
    public Game getGame() {
        return null;
    }

    public Unit getLinkedUnit() {
        return linkedUnit;
    }

    public void setLinkedUnit(Unit linkedUnit) {
        this.linkedUnit = linkedUnit;
    }
}
