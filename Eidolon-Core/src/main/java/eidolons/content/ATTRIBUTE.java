package eidolons.content;

import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;

/**
 * Created by Alexander on 2/2/2022
 */
public enum ATTRIBUTE {
    STRENGTH(PARAMS.STRENGTH, true, PARAMS.TOUGHNESS, PARAMS.CARRYING_CAPACITY),
    VITALITY(PARAMS.VITALITY, true, PARAMS.TOUGHNESS, PARAMS.ENDURANCE, PARAMS.REST_BONUS, PARAMS.ENDURANCE_REGEN),
    AGILITY(PARAMS.AGILITY, true),
    DEXTERITY(PARAMS.DEXTERITY, true),
    WILLPOWER(PARAMS.WILLPOWER, false),
    INTELLIGENCE(PARAMS.INTELLIGENCE, false),
    WISDOM(PARAMS.WISDOM, false),
    KNOWLEDGE(PARAMS.KNOWLEDGE, false),
    SPELLPOWER(PARAMS.SPELLPOWER, false),
    CHARISMA(PARAMS.CHARISMA, false),
    ;
    private PARAMS parameter;
    private PARAMS[] params;

    ATTRIBUTE(PARAMS attr_param, boolean physical, PARAMS... params) {
        this.setParameter(attr_param);
        this.setParams(params);
    }

    public PARAMS[] getParams() {
        return params;
    }

    public void setParams(PARAMS[] params) {
        this.params = params;
    }

    public static ATTRIBUTE getForParameter(PARAMETER parameter) {
        for (ATTRIBUTE value : values()) {
            if (value.parameter == parameter) {
                return value;
            }
            if (value.parameter == ContentValsManager.getBaseAttribute(parameter)) {
                return value;
            }
        }
        return null;
    }

    public PARAMS getParameter() {
        return parameter;
    }

    public PARAMS getBaseParameter() {
        return (PARAMS) DC_ContentValsManager.getBaseAttr(this);
    }

    public void setParameter(PARAMS parameter) {
        this.parameter = parameter;
    }
}
