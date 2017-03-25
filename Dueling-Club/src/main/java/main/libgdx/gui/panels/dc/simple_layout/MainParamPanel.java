package main.libgdx.gui.panels.dc.simple_layout;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.MainParamDataSource;

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

        strength = new ValueContainer(getOrCreateR("UI/value icons/attributes/strength.png"), "Strength", "");
        addElement(strength.left().bottom().pad(0, 10, 0, 25));

        vitality = new ValueContainer(getOrCreateR("UI/value icons/attributes/vitality.png"), "Vitality", "");
        addElement(vitality.left().bottom().pad(0, 10, 0, 25));

        agility = new ValueContainer(getOrCreateR("UI/value icons/attributes/agility.png"), "Agility", "");
        addElement(agility.left().bottom().pad(0, 10, 0, 25));

        dexterity = new ValueContainer(getOrCreateR("UI/value icons/attributes/dexterity.png"), "Dexterity", "");
        addElement(dexterity.left().bottom().pad(0, 10, 0, 25));

        willpower = new ValueContainer(getOrCreateR("UI/value icons/attributes/willpower.png"), "Willpower", "");
        addElement(willpower.left().bottom().pad(0, 10, 0, 25));

        addCol();

        spellpower = new ValueContainer(getOrCreateR("UI/value icons/attributes/spellpower.png"), "Spellpower", "");
        addElement(spellpower.left().bottom().pad(0, 10, 0, 25));

        intelligence = new ValueContainer(getOrCreateR("UI/value icons/attributes/intelligence.png"), "Intelligence", "");
        addElement(intelligence.left().bottom().pad(0, 10, 0, 25));

        knowledge = new ValueContainer(getOrCreateR("UI/value icons/attributes/knowledge.png"), "Knowledge", "");
        addElement(knowledge.left().bottom().pad(0, 10, 0, 25));

        wisdom = new ValueContainer(getOrCreateR("UI/value icons/attributes/wisdom.png"), "Wisdom", "");
        addElement(wisdom.left().bottom().pad(0, 10, 0, 25));

        charisma = new ValueContainer(getOrCreateR("UI/value icons/attributes/charisma.png"), "Charisma", "");
        addElement(charisma.left().bottom().pad(0, 10, 0, 25));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (updatePanel) {
            MainParamDataSource source = (MainParamDataSource) getUserObject();

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

            updatePanel = false;
        }
    }
}
