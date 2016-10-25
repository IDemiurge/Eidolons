package main.elements.conditions;

import main.content.properties.PROPERTY;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;

public class PropCondition extends StringComparison {

    public PropCondition(String str1, String str2, Boolean strict) {
        super(str1, str2, strict);
    }

    public PropCondition(PROPERTY prop, String str2, Boolean strict) {
        super(StringMaster.getValueRef(KEYS.MATCH, prop), str2, strict);
    }

    public PropCondition(PROPERTY prop, KEYS key, String str2, Boolean strict) {
        super(StringMaster.getValueRef(key, prop), str2, strict);
    }

    public PropCondition(PROPERTY prop, Object str2) {
        super(StringMaster.getValueRef(KEYS.MATCH, prop), str2.toString(),
                false);
    }
}
