package eidolons.game.battlecraft.rules.mechanics;

import eidolons.content.PARAMS;
import eidolons.entity.active.spaces.Feat;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.DC_SecondsRule;

public class CooldownRule implements DC_SecondsRule {

    @Override
    public void secondsPassed(Unit unit, int seconds) {
        for (Feat feat : unit.getActiveFeats()) {
            if (feat.getIntParam(PARAMS.COOLDOWN)!=0)
                feat.timePassed(seconds);
        }
    }
}
