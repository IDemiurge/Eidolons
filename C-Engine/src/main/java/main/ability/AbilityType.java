package main.ability;

import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.ability.construct.AbilityConstructor;
import main.data.xml.XML_Converter;
import main.entity.type.ObjType;
import main.entity.type.XmlHoldingType;

public class AbilityType extends XmlHoldingType {


    protected Abilities abilities;

    public AbilityType(AbilityType type) {
        super(type);

        setDoc(type.getDoc().cloneNode(true));
    }

    public AbilityType() {
    }

    public AbilityType(boolean gen, ObjType objType) {
        super(gen, objType);
    }

    public void construct() {
        AbilityConstructor.construct(this);
    }

    public void cloned() {
        this.doc = XML_Converter
         .getAbilitiesDoc(getProperty(G_PROPS.ABILITIES));

    }

    @Override
    public PROPERTY getXmlProperty() {
        return G_PROPS.ABILITIES;
    }

    public Abilities getAbilities() {
        return abilities;
    }

    public void setAbilities(Abilities abilities) {
        this.abilities = abilities;
    }

}
