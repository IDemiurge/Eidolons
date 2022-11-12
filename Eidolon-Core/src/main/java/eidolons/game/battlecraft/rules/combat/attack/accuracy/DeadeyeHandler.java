package eidolons.game.battlecraft.rules.combat.attack.accuracy;

import eidolons.entity.feat.active.ActiveObj;
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
        ActiveObj action = attack.getAction();
        Set<Deadeye> set = new LinkedHashSet<>();
        NewRpgEnums.DeadeyeType type = getKillDeadeye(action);
        set.add(createDeadeye(attack, type));
        type = getStdDeadeye(action);
        set.add(createDeadeye(attack, type));
        type = getSpecialDeadeye(action);
        set.add(createDeadeye(attack, type));

        return set.toArray(new Deadeye[0]);
    }

    private Deadeye createDeadeye(Attack attack, NewRpgEnums.DeadeyeType type) {
        return new Deadeye(attack, type);
    }

    private NewRpgEnums.DeadeyeType getSpecialDeadeye(ActiveObj action) {
        return NewRpgEnums.DeadeyeType.maim;
    }

    private NewRpgEnums.DeadeyeType getKillDeadeye(ActiveObj action) {
        return NewRpgEnums.DeadeyeType.decapitate;
    }

    private NewRpgEnums.DeadeyeType getStdDeadeye(ActiveObj action) {
        return NewRpgEnums.DeadeyeType.heartseeker;

    }
}
