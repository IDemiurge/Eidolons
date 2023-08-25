package elements.exec.effect;

import elements.exec.EntityRef;
import elements.exec.effect.framework.EffectResult;
import framework.entity.Entity;

/**
 * Created by Alexander on 8/23/2023
 */
public class ModifyStatEffect extends Effect {

    @Override
    public void applyThis(EntityRef ref) {
        Entity target = ref.getTarget();
        String valueName = data.getS("value_name");
        Object value = data.get("value");
        //maybe we can have clear arg numbers - so (1) is value, (2) is name etc? Then we just input the relevant stuff
        target.modifyValue(valueName, value); //adds or sets if not integer?
    }
    @Override
    public String[] getArgNames(){
        return new String[]{
                "value_name",
                "value"
        };
    }
}
