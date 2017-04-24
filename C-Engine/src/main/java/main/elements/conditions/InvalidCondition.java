package main.elements.conditions;

import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 4/23/2017.
 */
public class InvalidCondition extends MicroCondition {
    @Override
    public boolean check(Ref ref) {
        for (String substring : StringMaster.openContainer(
         ref.getMatchObj().getProperty(G_PROPS.ACTIVES))) {
            substring = VariableManager.removeVarPart(substring);
            if (DataManager.isTypeName(substring, DC_TYPE.ABILS))
                return true;
        }
        for (String substring : StringMaster.openContainer(
         ref.getMatchObj().getProperty(G_PROPS.PASSIVES))) {
            substring = VariableManager.removeVarPart(substring);
            if (DataManager.isTypeName(substring, DC_TYPE.ABILS))
                return true;
        }
        return false;
    }
}
