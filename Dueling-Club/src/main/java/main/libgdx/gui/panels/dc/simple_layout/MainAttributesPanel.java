package main.libgdx.gui.panels.dc.simple_layout;

import main.libgdx.gui.panels.dc.unitinfo.datasource.MainAttributesSource;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class MainAttributesPanel extends TablePanel {
    private VerticalValueContainer resistance;
    private VerticalValueContainer defense;
    private VerticalValueContainer armor;
    private VerticalValueContainer fortitude;
    private VerticalValueContainer spirit;

    public MainAttributesPanel() {
        super();
        rowDirection = TOP_LEFT;
        resistance = new VerticalValueContainer(getOrCreateR("UI/value icons/resistance.jpg"), "");
        addElement(resistance);

        defense = new VerticalValueContainer(getOrCreateR("UI/value icons/Defense.jpg"), "");
        addElement(defense);

        armor = new VerticalValueContainer(getOrCreateR("UI/value icons/armor.jpg"), "");
        addElement(armor);

        fortitude = new VerticalValueContainer(getOrCreateR("UI/value icons/Fortitude.jpg"), "");
        addElement(fortitude);

        spirit = new VerticalValueContainer(getOrCreateR("UI/value icons/spirit.jpg"), "");
        addElement(spirit);
    }

    @Override
    public void updateAct(float delta) {
        MainAttributesSource source = (MainAttributesSource) getUserObject();

        resistance.updateValue(source.getResistance());
        defense.updateValue(source.getDefense());
        armor.updateValue(source.getArmor());
        fortitude.updateValue(source.getFortitude());
        spirit.updateValue(source.getSpirit());
    }
}
