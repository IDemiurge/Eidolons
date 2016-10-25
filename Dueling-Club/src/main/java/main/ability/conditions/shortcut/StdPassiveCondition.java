package main.ability.conditions.shortcut;

import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.properties.G_PROPS;
import main.elements.conditions.StringComparison;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;

public class StdPassiveCondition extends StringComparison {

    public StdPassiveCondition(STANDARD_PASSIVES passive, KEYS key) {
        super(StringMaster.getValueRef(key, G_PROPS.STANDARD_PASSIVES), passive
                .getName(), false);
    }

    public StdPassiveCondition(STANDARD_PASSIVES passive) {
        super(StringMaster.getValueRef(KEYS.MATCH, G_PROPS.STANDARD_PASSIVES),
                passive.getName(), false);
    }

}
