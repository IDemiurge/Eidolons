package eidolons.entity.unit.trainers;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;

public class PtsItem {

    private ObjType type;
    private PROPERTY prop;

    public PtsItem(String typeName) {
        this.type = DataManager.getType(typeName);
        switch (type.getOBJ_TYPE()) {
            //TODO // prop = PROPS.LEARNED_PASSIVES;
            case ("skills"):
                prop = PROPS.SKILLS;
                break;
            case ("spells"):
                prop = PROPS.LEARNED_SPELLS;
                break;
            case ("actions"):
                prop = PROPS.LEARNED_ACTIONS;
                break;
        }
    }

    public PROPERTY getProperty() {
        return this.prop;
    }

    public String getName() {
        return type.getName();
    }

    public Integer getPointCost() {
        return type.getIntParam(PARAMS.CIRCLE);
    }

    public   PARAMETER getPoolProperty() {
        switch (type.getOBJ_TYPE()) {
            case ("spells"):
                return PARAMS.SPELL_POINTS_UNSPENT;
            case ("skills"):
            case ("actions"):
                return PARAMS.SKILL_POINTS_UNSPENT;
        }
        return null;
    }
}
