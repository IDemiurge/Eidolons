package main.ability.conditions.req;

import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.conditions.MicroCondition;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;

public class ClassTreeCondition extends MicroCondition {

    private String className;

    public ClassTreeCondition(String className) {
        this.className = className;
    }

    @Override
    public boolean check() {
        ObjType type = DataManager.getType(className, DC_TYPE.CLASSES);
        if (type == null) {
            return true;
        }
        Unit hero = (Unit) ref.getSourceObj();
        for (DC_FeatObj c : hero.getClasses()) {
            if (c.getType().equals(type)) {
                return true;
            }
            if (c.getProperty(G_PROPS.CLASS_GROUP).equalsIgnoreCase(
                    type.getProperty(G_PROPS.CLASS_GROUP))) {
                if (c.getIntParam(PARAMS.CIRCLE) >= type.getIntParam(PARAMS.CIRCLE)) {
                    return false;
                }
            }
        }
        return true;
    }

}
