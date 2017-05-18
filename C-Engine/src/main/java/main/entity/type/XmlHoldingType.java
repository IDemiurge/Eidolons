package main.entity.type;

import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.ability.construct.AbilityConstructor;
import main.data.ability.construct.XmlDocHolder;
import main.data.xml.XML_Converter;
import org.w3c.dom.Node;

/**
 * Created by JustMe on 5/17/2017.
 */
public abstract class XmlHoldingType extends ObjType implements XmlDocHolder {
    protected Node doc;

    public XmlHoldingType(XmlHoldingType type) {
        super(type);
        setDoc(type.getDoc().cloneNode(true));
    }

    public XmlHoldingType() {
    }

    public String getXml() {
        return getProperty(getXmlProperty());
    }

    public void construct() {
        AbilityConstructor.constructXml(this);
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


    public abstract PROPERTY getXmlProperty();
}