package campaign.run.battle;

import campaign.run.RunHandler;
import combat.init.BattleSetup;
import combat.init.CombatParty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 *
 * Consider now how to MOCK this for our DEMO's ! Without any RUN or CAMPAIGN!
 * Layers, aye, but not bindings!
 */
public class BattleBuilder extends RunHandler {

    public BattleSetup build(){
        Map<String, Object> data = new HashMap<>();
        CombatParty allies = null;
        CombatParty enemies = null;
        BattleSetup setup= new BattleSetup(data, allies, enemies);

        return setup;
    }
}
