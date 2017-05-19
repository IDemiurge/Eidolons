package main.ability.effects.oneshot.unit;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.data.DataManager;
import main.entity.type.ObjType;

public class TransformEffect extends MicroEffect  implements OneshotEffect {

    private String newType;

    public TransformEffect(String newType) {
        this.newType = newType;
    }

    @Override
    public boolean applyThis() {
        // kill quietly, summon, transfer buffs and percentages
        // it is possible to do it without killing i suppose... applyNewType()
        // could be worth a try!
        ObjType type = DataManager.getType(newType);
        // TODO apply XP!
        ref.getTargetObj().applyType(type);

        // set focus!
        return true;
    }

}
