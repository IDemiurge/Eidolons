package main.ability.effects.oneshot.buff;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.ability.effects.attachment.AddBuffEffect;
import main.entity.obj.BuffObj;
import main.system.auxiliary.log.LogMaster;

public class RemoveBuffEffect extends MicroEffect implements OneshotEffect {

    private String buffName;
    private boolean strict;

    public RemoveBuffEffect(String buffName) {
        this(buffName, true);
    }

    public RemoveBuffEffect(String buffName, boolean strict) {
        this.buffName = buffName;
        this.strict = strict;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " - " + buffName;
        // + ((ref.getTargetObj() != null) ? " from "
        // + ref.getTargetObj().getName() : "");
    }

    @Override
    public boolean applyThis() {
        if (buffName.equals(AddBuffEffect.EMPTY_BUFF_NAME)) {
            buffName = ref.getActive().getName();
        }
        BuffObj buff = ref.getTargetObj().getBuff(buffName, strict);
        if (buff != null)
            return buff.kill();


        LogMaster.log(1, ref.getTargetObj().getNameAndCoordinate() + " has no buff named " + buffName
         + ref);
        return false;
    }

}
