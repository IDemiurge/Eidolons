package main.libgdx.gui.panels.dc.unitinfo;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;

public class UnitDataSource implements UnitInfoMainParamSource {
    private Unit unit;

    public UnitDataSource(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String getStrength() {
        return unit.getParam(PARAMS.STRENGTH);
    }

    @Override
    public String getVitality() {
        return unit.getParam(PARAMS.VITALITY);
    }

    @Override
    public String getAgility() {
        return unit.getParam(PARAMS.AGILITY);
    }

    @Override
    public String getDexterity() {
        return unit.getParam(PARAMS.DEXTERITY);
    }

    @Override
    public String getWillpower() {
        return unit.getParam(PARAMS.WILLPOWER);
    }

    @Override
    public String getSpellpower() {
        return unit.getParam(PARAMS.SPELLPOWER);
    }

    @Override
    public String getIntelligence() {
        return unit.getParam(PARAMS.INTELLIGENCE);
    }

    @Override
    public String getKnowledge() {
        return unit.getParam(PARAMS.KNOWLEDGE);
    }

    @Override
    public String getWisdom() {
        return unit.getParam(PARAMS.WISDOM);
    }

    @Override
    public String getCharisma() {
        return unit.getParam(PARAMS.CHARISMA);
    }
}
