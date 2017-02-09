package main.ability.conditions.req;

import main.content.PROPS;
import main.data.DataManager;
import main.elements.conditions.MicroCondition;
import main.elements.conditions.StringContainersComparison;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

public class MultiClassCondition extends MicroCondition {

    private String className;
    private KEYS key;

    public MultiClassCondition(String className) {
        this.className = className;
    }

    public MultiClassCondition(KEYS key) {
        this.key = key;
    }

    public MultiClassCondition() {
        this(KEYS.MATCH);
    }

    @Override
    public boolean check() {
        ObjType type = null;
        if (className == null) {
            type = ref.getType(key.toString());
        } else {
            type = DataManager.getType(className);
        }
        // if (type.checkProperty(G_PROPS.BASE_TYPE)) {
        // return true;
        // }
        for (String className : StringMaster
                .openContainer(type.getProperty(PROPS.BASE_CLASSES_ONE))) {
            if (!new ClassTreeCondition(className).check(ref))
            // setReason()
            {
                return false;
            }
        }
        for (String className : StringMaster
                .openContainer(type.getProperty(PROPS.BASE_CLASSES_TWO))) {
            if (!new ClassTreeCondition(className).check(ref)) {
                return false;
            }
        }

        return new StringContainersComparison(ref.getSourceObj().getProperty(PROPS.CLASSES), type
                .getProperty(PROPS.BASE_CLASSES_ONE)).check(ref)
                && new StringContainersComparison(ref.getSourceObj().getProperty(PROPS.CLASSES),
                type.getProperty(PROPS.BASE_CLASSES_TWO)).check(ref);
    }
}
