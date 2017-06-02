package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.AttributesDataSource;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class MainParamPanel extends TablePanel {

    private ValueContainer strength;
    private ValueContainer vitality;
    private ValueContainer agility;
    private ValueContainer dexterity;
    private ValueContainer willpower;

    private ValueContainer spellpower;
    private ValueContainer intelligence;
    private ValueContainer knowledge;
    private ValueContainer wisdom;
    private ValueContainer charisma;


    public MainParamPanel() {
        TextureRegion textureRegion = getOrCreateR("/UI/components/infopanel/main_param_panel.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        pad(20, 10, 20, 10);

        strength = new ValueContainer(getOrCreateR("UI/value icons/attributes/strength.png"), "Strength", "");
        addElement(strength);

        spellpower = new ValueContainer(getOrCreateR("UI/value icons/attributes/spellpower.png"), "Spellpower", "");
        addElement(spellpower);

        row();

        vitality = new ValueContainer(getOrCreateR("UI/value icons/attributes/vitality.png"), "Vitality", "");
        addElement(vitality);

        intelligence = new ValueContainer(getOrCreateR("UI/value icons/attributes/intelligence.png"), "Intelligence", "");
        addElement(intelligence);

        row();

        agility = new ValueContainer(getOrCreateR("UI/value icons/attributes/agility.png"), "Agility", "");
        addElement(agility);

        knowledge = new ValueContainer(getOrCreateR("UI/value icons/attributes/knowledge.png"), "Knowledge", "");
        addElement(knowledge);

        row();

        dexterity = new ValueContainer(getOrCreateR("UI/value icons/attributes/dexterity.png"), "Dexterity", "");
        addElement(dexterity);

        wisdom = new ValueContainer(getOrCreateR("UI/value icons/attributes/wisdom.png"), "Wisdom", "");
        addElement(wisdom);

        row();

        willpower = new ValueContainer(getOrCreateR("UI/value icons/attributes/willpower.png"), "Willpower", "");
        addElement(willpower);

        charisma = new ValueContainer(getOrCreateR("UI/value icons/attributes/charisma.png"), "Charisma", "");
        addElement(charisma);
    }

    @Override
    public void updateAct(float delta) {
        AttributesDataSource source = (AttributesDataSource) getUserObject();

        strength.updateValue(source.getStrength());
        vitality.updateValue(source.getVitality());
        agility.updateValue(source.getAgility());
        dexterity.updateValue(source.getDexterity());
        willpower.updateValue(source.getWillpower());

        spellpower.updateValue(source.getSpellpower());
        intelligence.updateValue(source.getIntelligence());
        knowledge.updateValue(source.getKnowledge());
        wisdom.updateValue(source.getWisdom());
        charisma.updateValue(source.getCharisma());
    }
}
