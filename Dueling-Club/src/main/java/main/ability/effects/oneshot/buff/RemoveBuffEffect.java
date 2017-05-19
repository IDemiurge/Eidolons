package main.ability.effects.oneshot.buff;

import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.ability.effects.attachment.AddBuffEffect;
import main.system.auxiliary.log.LogMaster;

public class RemoveBuffEffect extends MicroEffect  implements OneshotEffect {

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
        try {
            if (buffName.equals(AddBuffEffect.EMPTY_BUFF_NAME)) {
                buffName = ref.getActive().getName();
            }
            return ref.getTargetObj().getBuff(buffName, strict).kill();
        } catch (Exception e) {
            // e.printStackTrace();
            LogMaster.log(1, "removing buff named " + buffName + " failed: "
                    + ref);
            return false;
        }
    }

}
