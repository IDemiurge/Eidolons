package eidolons.game.module.herocreator.logic;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;

public class XpItem {

    private ObjType type;
    private PROPERTY prop;

    public XpItem(String typeName) {
        this.type = DataManager.getType(typeName);
        switch (type.getOBJ_TYPE()) {
            case ("skills"):
                prop = PROPS.SKILLS;
                break;
            case ("spells"):
                prop = PROPS.VERBATIM_SPELLS;
                break;
            case ("actions"):
                prop = G_PROPS.ACTIVES;
                break;
        }
    }

    public PROPERTY getProperty() {
        return this.prop;
    }

    public String getName() {
        return type.getName();
    }

    public Integer getXpCost() {
        // TODO Auto-generated method stub
        return type.getIntParam(PARAMS.XP_COST);
    }

}
