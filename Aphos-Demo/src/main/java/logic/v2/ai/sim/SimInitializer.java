package logic.v2.ai.sim;

import logic.v2.ai.adapter.GameAdapter;
import logic.v2.ai.roleplay.IAiRoleModel;
import logic.v2.ai.single.SingleAi;
import logic.v2.entity.IUnit;

/**
 * Created by Alexander on 1/21/2023
 */
public class SimInitializer {

    public void init(Simulation sim) {
        GameAdapter adapter = sim.getAdapter();
        //fully separate from RPG Core logic - as it may be mocked...
        for (String name : sim.getParameters().getUnits()) {
            IUnit unit = adapter.createUnit(name);
            IAiRoleModel roleModel = getAiRoleModel(unit);
            sim.addAi(new SingleAi(roleModel, unit));

        }

    }

    private IAiRoleModel getAiRoleModel(IUnit unit) {
        return null;
    }
}
