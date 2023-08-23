package combat.init;

import com.graphbuilder.math.FuncNode;
import combat.BattleHandler;
import combat.sub.BattleManager;
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
public class BattleInitializer extends BattleHandler {

    private final BattleSetup setup;

    public BattleInitializer(BattleManager battleManager, BattleSetup setup) {
        super(battleManager);
        this.setup = setup;
    }

    @Override
    public void battleStarts() {
        initParty(setup.getAlly().unitData, setup.getAlly().ally);
        initParty(setup.getEnemy().unitData, setup.getEnemy().ally);
        //setup env etc   setup.getData()
    }

    private void initParty(String partyData, Boolean ally) {
        Map<String, Object> map = DataManager.stringArrayToMap(partyData.split(";"));
        for (String name : map.keySet()) {
            Integer posId = NumberUtils.getInt(name);
            FieldPos pos = getField().getPos(posId);
            Map<String, Object> unitData= DataManager.getEntityData(map.get(name).toString());
            Set<Unit> units = new HashSet<>();
            units.add(new Unit(unitData, ally, pos));
            //save initial party? What for?
        }
    }

}
