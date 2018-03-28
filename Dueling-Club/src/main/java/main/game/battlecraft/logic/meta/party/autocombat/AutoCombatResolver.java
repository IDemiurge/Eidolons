package main.game.battlecraft.logic.meta.party.autocombat;

/**
 * Created by JustMe on 7/21/2017.
 */
public class AutoCombatResolver {
    public void getPossibleCombatEffects(AutoCombat combat) {

    }

    public void autoCombatResolve(AutoCombat combat) {

    }

    public enum AUTO_COMBAT_EFFECT {
        INJURY_LIGHT,
        INJURY,
        INJURY_HEAVY,
        GRIEF_LIGHT,
        GRIEF,
        GRIEF_HEAVY,
    }

    public enum AUTO_COMBAT_OUTCOME {
        VICTORY,
        ENEMY_RETREAT,
        RETREAT,
        DEFEAT,

    }

    public enum AUTO_COMBAT_STATUS {
        FIGHTING,
        ENGAGING,
        FINISHED
    }
}
