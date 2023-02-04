package logic.v2.ai.single;

import logic.v2.ai.ipc.AiStrategy;
import logic.v2.ai.ipc.ImpulseStrat;
import logic.v2.ai.roleplay.IAiRoleModel;
import logic.v2.entity.IUnit;

/**
 * Created by Alexander on 1/21/2023
 */
public class SingleAi {

    private final AiMoods moods;
    private final IAiRoleModel roleModel;
    private final IUnit unit;
    private AiStrategy impulseStrategy;

    public SingleAi(IAiRoleModel roleModel, IUnit unit) {
        this.roleModel = roleModel;
        this.unit = unit;
        moods = new AiMoods();
        impulseStrategy = new ImpulseStrat(this);
    }

    public AiMoods getMoods() {
        return moods;
    }

    public IAiRoleModel getRoleModel() {
        return roleModel;
    }

    public IUnit getUnit() {
        return unit;
    }

    public AiStrategy getImpulseStrategy() {
        return impulseStrategy;
    }

}
