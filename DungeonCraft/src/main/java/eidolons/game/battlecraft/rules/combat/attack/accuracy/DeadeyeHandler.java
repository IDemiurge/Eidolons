package eidolons.game.battlecraft.rules.combat.attack.accuracy;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.meta.universal.event.ChoiceEventMaster;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import main.content.enums.entity.NewRpgEnums;

import java.util.LinkedHashSet;
import java.util.Set;

public class DeadeyeHandler {


    public void deadEye(Attack attack) {
        Deadeye[] options = getDeadEyeOptions(attack);
        Deadeye deadeye = new ChoiceEventMaster<Deadeye>().promptAndWait(options);
        apply(deadeye, attack);
    }

    private void apply(Deadeye deadeye, Attack attack) {
        //abstract? damageModifier, effect, target(s)
    }

    private Deadeye[] getDeadEyeOptions(Attack attack) {
        DC_ActiveObj action = attack.getAction();
        Set<Deadeye> set = new LinkedHashSet<>();
        NewRpgEnums.DeadeyeType type = getKillDeadeye(action);
        set.add(createDeadeye(action, type));
        type = getStdDeadeye(action);
        set.add(createDeadeye(action, type));
        type = getSpecialDeadeye(action);
        set.add(createDeadeye(action, type));

        return set.toArray(new Deadeye[0]);
    }
}
