package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.Texture;
import main.content.PARAMS;
import main.entity.obj.DC_Obj;
import main.libgdx.texture.TextureCache;

public class UnitDataSource implements UnitInfoMainParamSource, ResourceSource, AvatarDataSource {
    private DC_Obj unit;

    public UnitDataSource(DC_Obj unit) {
        this.unit = unit;
    }

    @Override
    public String getStrength() {
        return String.valueOf(unit.getIntParam(PARAMS.STRENGTH));
    }

    @Override
    public String getVitality() {
        return String.valueOf(unit.getIntParam(PARAMS.VITALITY));
    }

    @Override
    public String getAgility() {
        return String.valueOf(unit.getIntParam(PARAMS.AGILITY));
    }

    @Override
    public String getDexterity() {
        return String.valueOf(unit.getIntParam(PARAMS.DEXTERITY));
    }

    @Override
    public String getWillpower() {
        return String.valueOf(unit.getIntParam(PARAMS.WILLPOWER));
    }

    @Override
    public String getSpellpower() {
        return String.valueOf(unit.getIntParam(PARAMS.SPELLPOWER));
    }

    @Override
    public String getIntelligence() {
        return String.valueOf(unit.getIntParam(PARAMS.INTELLIGENCE));
    }

    @Override
    public String getKnowledge() {
        return String.valueOf(unit.getIntParam(PARAMS.KNOWLEDGE));
    }

    @Override
    public String getWisdom() {
        return String.valueOf(unit.getIntParam(PARAMS.WISDOM));
    }

    @Override
    public String getCharisma() {
        return String.valueOf(unit.getIntParam(PARAMS.CHARISMA));
    }

    @Override
    public String getToughness() {
        int c = unit.getIntParam(PARAMS.C_TOUGHNESS);
        int m = unit.getIntParam(PARAMS.TOUGHNESS);
        return c + "/" + m;
    }

    @Override
    public String getEndurance() {
        int c = unit.getIntParam(PARAMS.C_ENDURANCE);
        int m = unit.getIntParam(PARAMS.ENDURANCE);
        return c + "/" + m;
    }

    @Override
    public String getStamina() {
        int c = unit.getIntParam(PARAMS.C_TOUGHNESS);
        int m = unit.getIntParam(PARAMS.TOUGHNESS);
        return c + "/" + m;
    }

    @Override
    public String getMorale() {
        int c = unit.getIntParam(PARAMS.C_MORALE);
        int m = unit.getIntParam(PARAMS.MORALE);
        return c + "/" + m;
    }

    @Override
    public String getEssence() {
        int c = unit.getIntParam(PARAMS.C_ESSENCE);
        int m = unit.getIntParam(PARAMS.ESSENCE);
        return c + "/" + m;
    }

    @Override
    public String getFocus() {
        int c = unit.getIntParam(PARAMS.C_FOCUS);
        int m = unit.getIntParam(PARAMS.FOCUS);
        return c + "/" + m;
    }

    @Override
    public Texture getAvatar() {
        return TextureCache.getOrCreate(unit.getImagePath());
    }

    @Override
    public String getName() {
        return unit.getName();
    }

    @Override
    public String getParam1() {
        return unit.getValue("Race");
    }

    @Override
    public String getParam2() {
        return unit.getParam("Level");
    }
}
