package main.game.ai.advanced.companion;

import main.entity.Ref;
import main.game.event.Event;

public class RolePlayMaster {

    RolePlayMaster(Ref ref) {
        //proValue = formula.getInt(ref);

    }

    RolePlayMaster() {
        //dialog event, orders/behaviors/rpg mode for ai
    }

    public boolean handleEvent(Event event) {

        return true;
    }

    public enum ROLE_PLAY_MODEL {
        VILLAIN, SCOUNDREL, DAREDEVIL, PARAGON, PROFESSIONAL, BERSERKER, MARAUDER,

    }

    public enum ROLE_PLAY_FACTORS {
        GREED, CAUTION, AGGRESSION, LOYALTY, VINDICTIVENESS, SMARTNESS,
    }

    public enum ROLE_PLAY_CASES {
        LOOT, HATED_ENEMY, REVENGE,

    }

    public enum ROLE_PLAY_AFFINITIES {
        GREEDY, CARELESS,
    }

}
