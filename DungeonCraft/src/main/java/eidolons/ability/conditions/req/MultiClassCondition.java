package eidolons.ability.conditions.req;

import eidolons.content.PROPS;
import main.data.DataManager;
import main.elements.conditions.MicroCondition;
import main.elements.conditions.StringContainersComparison;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;

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
    public boolean check(Ref ref) {
        ObjType type;
        if (className == null) {
            type = ref.getType(key.toString());
        } else {
            type = DataManager.getType(className);
        }
        // if (type.checkProperty(G_PROPS.BASE_TYPE)) {
        // return true;
        // }
        for (String className : ContainerUtils
         .openContainer(type.getProperty(PROPS.BASE_CLASSES_ONE))) {
            if (!new ClassTreeCondition(className).preCheck(ref))
            // setReason()
            {
                return false;
            }
        }
        for (String className : ContainerUtils
         .openContainer(type.getProperty(PROPS.BASE_CLASSES_TWO))) {
            if (!new ClassTreeCondition(className).preCheck(ref)) {
                return false;
            }
        }

        return new StringContainersComparison(ref.getSourceObj().getProperty(PROPS.CLASSES), type
         .getProperty(PROPS.BASE_CLASSES_ONE)).preCheck(ref)
         && new StringContainersComparison(ref.getSourceObj().getProperty(PROPS.CLASSES),
         type.getProperty(PROPS.BASE_CLASSES_TWO)).preCheck(ref);
    }
}
