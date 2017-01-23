package main.system.launch;

import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.MACRO_PROPS;
import main.content.properties.PROPERTY;
import main.data.ability.construct.XmlDocHolder;
import main.data.xml.XML_Converter;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.security.SecureRandom;

public class TypeBuilder {

    private static TypeInitializer typeInitializer;

    public static ObjType buildType(Node node, String typeType) {
        if (typeInitializer == null)
            typeInitializer = new TypeInitializer();
        OBJ_TYPE obj_type = ContentManager.getOBJ_TYPE(typeType);
        ObjType type = getTypeInitializer().getNewType(obj_type);

        buildType(node, type);

        return type;
    }

    public static ObjType buildType(Node node, ObjType type) {

        NodeList nl = node.getChildNodes();
        main.system.auxiliary.LogMaster.log(0, "building type: " +

                node.getNodeName());
        for (int i = 0; i < nl.getLength(); i++) {
            Node child = nl.item(i);

            if (child.getNodeName().equals("params")) {

                setParams(type, child.getChildNodes());

            }
            if (child.getNodeName().equals("props")) {

                setProps(type, child.getChildNodes());

            }

        }
        if (getTypeInitializer() != null)
            getTypeInitializer().setXmlTreeValue(false);

        checkUID(type);

        return type;
    }

    private static void checkUID(ObjType type) {
        if (type.getUniqueId().isEmpty()) {
            String id = getUniqueId(type);
            type.setProperty(G_PROPS.UNIQUE_ID, id);
        }

    }

    private static String getUniqueId(ObjType type) {
        return type.getOBJ_TYPE() + " " + type.getName() + new SecureRandom().nextLong();
    }

    public static void setProps(Entity type, Node node) {
        setProps(type, node.getChildNodes());
    }

    public static void setProps(Entity type, NodeList childNodes) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeName().equals(XML_Converter.TEXT_NODE))
                continue;
            if ((type) instanceof XmlDocHolder)
                if (child.getNodeName().equals(G_PROPS.ABILITIES.getName())
                        || StringMaster.getWellFormattedString(child.getNodeName()).equals(
                        MACRO_PROPS.DIALOGUE_TREE.getName())) {

                    child = XML_Converter.getAbilitiesDoc(child);

                    type.setProperty(ContentManager.getPROP(child.getNodeName()), XML_Converter
                            .getStringFromXML(child, false));

                    ((XmlDocHolder) type).setDoc(child);
                    continue;
                }
            PROPERTY prop = ContentManager.getPROP(child.getNodeName());
            if (prop == null) {
                main.system.auxiliary.LogMaster.log(1, "no such prop: " + child.getNodeName());
                prop = ContentManager.getPROP(child.getNodeName());
                continue;
            }
            type.setProperty(prop, getTextFromXml(child));
        }

    }

    private static String getTextFromXml(Node child) {
        return XML_Converter.getTextStringFromXML(child);
    }

    public static void setParams(Entity type, Node node) {
        setParams(type, node.getChildNodes());
    }

    public static void setParams(Entity type, NodeList childNodes) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);

            if (child.getTextContent().startsWith("\n"))
                continue;
            PARAMETER param = ContentManager.getPARAM(child.getNodeName());
            if (param == null) {
                main.system.auxiliary.LogMaster.log(1, "no such param: " + child.getNodeName());
                continue;
            }

            type.setParam(param, getTextFromXml(child), true);
        }

    }

    public static TypeInitializer getTypeInitializer() {
        return typeInitializer;
    }

    public static void setTypeInitializer(TypeInitializer typeInitializer) {
        TypeBuilder.typeInitializer = typeInitializer;
    }

}
