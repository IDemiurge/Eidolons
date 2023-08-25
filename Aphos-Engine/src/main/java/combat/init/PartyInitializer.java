package combat.init;

import combat.battlefield.BattleField;
import framework.data.DataManager;
import framework.entity.field.Unit;
import framework.field.FieldPos;
import main.system.auxiliary.NumberUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Alexander on 8/23/2023
 */
public class PartyInitializer {

    private final BattleField field;
    private final BattleSetup setup;

    public PartyInitializer(BattleField field, BattleSetup setup) {
        this.field = field;
        this.setup = setup;
    }

    public void initParties() {
        initParty(setup.getAlly().unitData, setup.getAlly().ally);
        initParty(setup.getEnemy().unitData, setup.getEnemy().ally);
        //setup env etc   setup.getData()
        //the rest should be done by specific managers
    }

    private void initParty(String partyData, Boolean ally) {
        Map<String, Object> map = DataManager.stringArrayToMap(partyData.split(";"));
        for (String name : map.keySet()) {
            Integer posId = NumberUtils.getInt(name);
            FieldPos pos = field.getPos(posId);
            Map<String, Object> unitData= DataManager.getEntityData(map.get(name).toString());
            Set<Unit> units = new HashSet<>();
            units.add(new Unit(unitData, ally, pos));
            //save initial party? What for?
        }
    }

}
