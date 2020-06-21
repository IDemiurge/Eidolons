package main.entity.type;

import main.content.ContentValsManager;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.ability.construct.XmlDocHolder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.entity.DataModel;
import main.entity.type.impl.XmlHoldingType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class TypeBuilder {

    public static final String PROPS_NODE = "props";
    public static final String PARAMS_NODE = "params";
    private static TypeInitializer typeInitializer;

    public static final Set<OBJ_TYPE> typeBuildOverride = new HashSet<>();

    public static ObjType buildType(Node node, String typeType) {
        if (typeInitializer == null) {
            typeInitializer = new TypeInitializer();
        }
        OBJ_TYPE TYPE = ContentValsManager.getOBJ_TYPE(typeType);
        ObjType type = null;

        if (TYPE != null) {
            if (isUseDefaultType(TYPE)){
                type=getTypeInitializer().getOrCreateDefault(TYPE);
            } else
                type = getTypeInitializer().getNewType(TYPE);
            if (isBuildTypeOnInit(TYPE))
                buildType(node, type);
            else {
                type.setName(XML_Formatter.restoreXmlNodeName(node.getNodeName()));
                type.setNode(node); //lazy
            }
        } else {
            LogMaster.error("type with name \"" + typeType + "\" not found!");
        }

        return type;
    }

    private static boolean isUseDefaultType(OBJ_TYPE TYPE) {
        return !TYPE.isTreeEditType();
    }

    private static boolean isBuildTypeOnInit(OBJ_TYPE objType) {
        if (CoreEngine.isArcaneVault()) {
            return true;
        }
        return typeBuildOverride.contains(objType) ;
//        return true;
    }

    public static ObjType buildType(Node node, ObjType type) {

        NodeList nl = node.getChildNodes();
//        LogMaster.log(1, "building type: " + node.getNodeName());
        type.setInitialized(false);
        for (int i = 0; i < nl.getLength(); i++) {
            Node child = nl.item(i);

            if (child.getNodeName().equals(PARAMS_NODE)) {

                setParams(type, child.getChildNodes());

            }
            if (child.getNodeName().equals(PROPS_NODE)) {

                setProps(type, child.getChildNodes());

            }

        }
        if (getTypeInitializer() != null) {
            getTypeInitializer().setXmlTreeValue(false);
        }

        checkUID(type);
        type.setInitialized(true);
        type.setBuilt(true);
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

    public static void setProps(DataModel type, Node node) {
        setProps(type, node.getChildNodes());
    }

    private static void setProps(DataModel type, NodeList childNodes) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeName().equals(XML_Converter.TEXT_NODE)) {
                continue;
            }
            if ((type) instanceof XmlHoldingType) {


                if (StringMaster.format(child.getNodeName()).equals(
                        ((XmlHoldingType) (type)).getXmlProperty()
                                .getName())
                ) {

                    child = XML_Converter.getAbilitiesDoc(child);

                    type.setProperty(ContentValsManager.getPROP(child.getNodeName()), XML_Converter
                            .getStringFromXML(child, false));

                    ((XmlDocHolder) type).setDoc(child);
                    continue;
                }
            }
            PROPERTY prop = ContentValsManager.getPROP(child.getNodeName());
            if (prop == null) {
                LogMaster.log(1, "no such prop: " + child.getNodeName());
                prop = ContentValsManager.getPROP(child.getNodeName());
                continue;
            }
            type.setProperty(prop, getTextFromXml(child));
        }

    }

    private static String getTextFromXml(Node child) {
        return XML_Converter.getTextStringFromXML(child);
    }

    public static void setParams(DataModel type, Node node) {
        setParams(type, node.getChildNodes());
    }

    private static void setParams(DataModel type, NodeList childNodes) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);

            if (child.getTextContent().startsWith("\n")) {
                continue;
            }
            PARAMETER param = ContentValsManager.getPARAM(child.getNodeName());
            if (param == null) {
//                LogMaster.log(1, "no such param: " + child.getNodeName());
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

    public static String getAlteredValuesXml(DataModel entity, ObjType type) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append(XML_Converter.openXml(PROPS_NODE));
        for (PROPERTY sub : entity.getPropMap().keySet()) {
            if (sub.isDynamic() || !entity.getProperty(sub).equalsIgnoreCase(type.getProperty(sub)))
                xmlBuilder.append(XML_Formatter.getValueNode(entity, (sub)));
        }
        xmlBuilder.append(XML_Converter.closeXml(PROPS_NODE));

        xmlBuilder.append(XML_Converter.openXml(PARAMS_NODE));
        for (PARAMETER sub : entity.getParamMap().keySet()) {
            if (sub.isDynamic() || !entity.getParam(sub).equals(type.getParam(sub)))
                xmlBuilder.append(XML_Formatter.getValueNode(entity, (sub)));
        }
        xmlBuilder.append(XML_Converter.closeXml(PARAMS_NODE));

        return xmlBuilder.toString();
    }
}
