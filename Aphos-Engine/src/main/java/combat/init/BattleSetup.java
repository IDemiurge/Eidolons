package combat.init;

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
        battleType = MiscTypes.BattleType.Skirmish;// EnumFinder.get(MiscTypes.BattleType.class, data.get("battle_type"));
    }

    public MiscTypes.BattleType getBattleType() {
        return battleType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public CombatParty getAlly() {
        return ally;
    }

    public CombatParty getEnemy() {
        return enemy;
    }

    /**
     * Created by Alexander on 8/21/2023
     */
    public static class CombatParty {
        public final Boolean ally;
        public final String unitData;
        public final int teamId;

        public CombatParty(String unitData, Boolean ally) {
            this.unitData = unitData;
            this.ally = ally;
            if (ally == null) teamId = 0;
            else
                teamId = ally ? 1 : 2;
        }
    }
}
