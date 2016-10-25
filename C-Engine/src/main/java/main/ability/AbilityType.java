package main.ability;

import main.content.properties.G_PROPS;
import main.data.ability.construct.AbilityConstructor;
import main.data.ability.construct.XmlDocHolder;
import main.data.xml.XML_Converter;
import main.entity.type.ObjType;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;

public class AbilityType extends ObjType implements XmlDocHolder {

    final static DocumentBuilderFactory builderFactory = DocumentBuilderFactory
            .newInstance();
    protected Abilities abilities;
    private Node doc;

    public AbilityType(AbilityType type) {
        super(type);

        setDoc(type.getDoc().cloneNode(true));
    }

    public AbilityType() {
    }

    public void construct() {
        AbilityConstructor.construct(this);
    }

    public void cloned() {
        this.doc = XML_Converter
                .getAbilitiesDoc(getProperty(G_PROPS.ABILITIES));

    }

    public Node getDoc() {
        return doc;
    }

    public void setDoc(Node child) {
        this.doc = child;
    }

    public Abilities getAbilities() {
        return abilities;
    }

    public void setAbilities(Abilities abilities) {
        this.abilities = abilities;
    }

}
