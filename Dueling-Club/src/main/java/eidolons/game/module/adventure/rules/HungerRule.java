package eidolons.game.module.adventure.rules;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.adventure.MacroManager;
import eidolons.game.module.adventure.entity.MacroParty;
import main.content.values.parameters.MACRO_PARAMS;

public class HungerRule extends TurnRule {
    private static final float HEALTH_REDUCTION_FACTOR = 0.1f;

    public void newTurn() {
        for (MacroParty p : MacroManager.getGame().getParties()) {
            // TODO preCheck turn is 'normal' - not a chase/flight, nor a battle or
            // march
            consumeProvisions(p);
        }
    }

    private void consumeProvisions(MacroParty p) {
        p.modifyParameter(MACRO_PARAMS.C_PROVISIONS,
         -p.getIntParam(MACRO_PARAMS.CONSUMPTION));
        if (p.getIntParam(MACRO_PARAMS.C_PROVISIONS) < 0) {
            int hungerAmount = p.getIntParam(MACRO_PARAMS.C_PROVISIONS);
            // choose who gets the last provisions or to split equally
            p.setParam(MACRO_PARAMS.C_PROVISIONS, 0);
            // reduce health, motivation, maybe other params too... preCheck alive!
            // :)
            // hunger parameter? starvation buff that increases init/atk/sp but
            // reduces foc/def/ess
            // increase speed, reduce
            // stealth/detection
            for (Unit hero : p.getMembers()) {
                hero.modifyParameter(MACRO_PARAMS.HUNGER, hungerAmount);
                applyHunger(hero);
            }
        } else {
            for (Unit hero : p.getMembers()) {
                int hungerSated = hero.getIntParam(MACRO_PARAMS.HUNGER);
                if (hungerSated > p.getIntParam(MACRO_PARAMS.C_PROVISIONS)) {
                    hungerSated = p.getIntParam(MACRO_PARAMS.C_PROVISIONS);
                }

                hero.modifyParameter(MACRO_PARAMS.HUNGER, -hungerSated);
                p.modifyParameter(MACRO_PARAMS.C_PROVISIONS, -hungerSated);

            }
        }

    }

    private void applyHunger(Unit hero) {
        int hunger = hero.getIntParam(MACRO_PARAMS.HUNGER);

        // continuous vs oneshot ... each turn

        hero.modifyParameter(MACRO_PARAMS.HEALTH,
         -(Math.round(hunger * HEALTH_REDUCTION_FACTOR)));

        // 'buff rule' - hungry, starved
        // HEALTH
        // ONENESS
        // LOYALTY

    }

}
