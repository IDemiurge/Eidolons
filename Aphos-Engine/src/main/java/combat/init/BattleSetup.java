package combat.init;

import elements.content.enums.EnumFinder;
import elements.content.enums.types.MiscTypes;

import java.util.Map;

/**
 * Created by Alexander on 8/21/2023
 * Skirmish/battle/boss etc
 * Seals, env,
 * All other inputs? Who is joining the battle?
 */
public class BattleSetup {
    MiscTypes.BattleType battleType;
    Map<String, Object> data;
    CombatParty ally;
    CombatParty enemy;

    public BattleSetup(Map<String, Object> data , CombatParty ally, CombatParty enemy) {
        this.data = data;
        this.ally = ally;
        this.enemy = enemy;
        battleType = EnumFinder.get(MiscTypes.BattleType.class, data.get("type"));
    }

    public MiscTypes.BattleType getBattleType() {
        return battleType;
    }
}
