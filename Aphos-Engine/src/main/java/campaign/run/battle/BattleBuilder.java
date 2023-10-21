package campaign.run.battle;

import campaign.run.RunHandler;
import combat.init.BattleSetup;
import framework.data.DataManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 * <p>
 * Consider now how to MOCK this for our DEMO's ! Without any RUN or CAMPAIGN! Layers, aye, but not bindings!
 */
public class BattleBuilder extends RunHandler {

    public static final  String ALLIES= "allies";
    public static final  String ENEMIES= "enemies";
    public static final  String SEPARATOR= "::";

    public BattleSetup build(String[] data) {
        Map<String, Object> map = new HashMap<>();
        BattleSetup.CombatParty allies = null;
        BattleSetup.CombatParty enemies = null;
        for (String datum : data) {
            String key = datum.split(SEPARATOR)[0];
            String value = datum.split(SEPARATOR)[1];
            if (key.equalsIgnoreCase(ALLIES)) {
                allies = new BattleSetup.CombatParty(value, true);
            } else
            if (key.equalsIgnoreCase(ENEMIES)) {
                enemies = new BattleSetup.CombatParty(value, false);
            } else {
                map = DataManager.stringArrayToMap(value.split(";"));
            }
        }

        BattleSetup setup = new BattleSetup(map, allies, enemies);

        return setup;
    }
}
