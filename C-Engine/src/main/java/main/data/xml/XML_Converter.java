package main.data.xml;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.data.DataManager;
import main.data.ability.AE_Item;
import main.data.ability.Mapper;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TreeMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.XMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class XML_Converter {
    public static final String TEXT_NODE = "#text";
    private final static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private static final String TYPES_NODE = "Types";
    private static final String DOCUMENT_ROOT = null;
    public static Pattern p = Pattern.compile("\n");
    private static DocumentBuilder builder;

    public static List<Node> getNodeList(Node node) {
        return getNodeList(node, true);
    }

    // public Document reformDocument(Collection<String> newGroups)
    // {

/*    public static Document getGroupDoc(String sub, String type) {
        Document doc = XML_Converter.getDoc(XML_Reader.xmlMap.get(type));
        Document groupDoc = null;
        NodeList nl = doc.getFirstChild().getChildNodes();
        Node node = null;
        int check = 0;
        for (int i = 0; i < nl.getLength(); i++) {
            node = nl.item(i);
            // node.getOwnerDocument()
            try {
                if (node.getNodeName().toLowerCase().equals(sub.toLowerCase())) {
                    sub += "!";
                    groupDoc = getDoc(node);
                    break;
                }

                i++;
                check = i;
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                Err.info(sub + type + nl.getLength());
            }
        }
        if (groupDoc == null) {
            Err.info(check + sub + type + nl.getLength() + node.getNodeName());
        }
        return groupDoc;

    }*/

    public static List<Node> getNodeList(Node node, boolean ignoreTextNodes) {
        List<Node> list = new ArrayList<>();
        if (node == null) {
            return list;
        }
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {

            Node item = nl.item(i);
            if (ignoreTextNodes)
                if (checkTextNode(item)) {
                    continue;
                }
            list.add(item);

        }
        return list;

    }

    private static boolean checkTextNode(Node item) {
        return item.getNodeName().equals(TEXT_NODE);
    }

    public static List<Node> getNodeListFromFirstChild(Node node, boolean ignoreTextNodes) {
        return getNodeList(node.getFirstChild(), ignoreTextNodes);
    }

    public static List getConvertedDoc(Node node) {
        List list = new ArrayList();

        if (!node.hasChildNodes()) {
            list.add(node.getNodeName());
            return list;
        }

        NodeList nl = node.getFirstChild().getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(getConvertedDoc(nl.item(i)));

        }

        return list;

    }

    private static Document getDoc(Node node) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        Document newDocument = builder.newDocument();
        newDocument.appendChild(newDocument.importNode(node, true));
        return newDocument;
    }

    public static Node getChildAt(Node levelDocument, int i) {
        List<Node> nodeList = getNodeList(levelDocument);
        if (nodeList.size() <= i) {
            return null;
        }
        return nodeList.get(i);
    }

    public static Document getDoc(String myString) {
        return getDoc(myString, false);
    }

    public static Document getDoc(String myString, boolean removeDocumentRootNode) {
        Document document = null;
        try {
            document = getBuilder().parse(new InputSource(new StringReader(myString)));

            if (removeDocumentRootNode) {
                if (document.getNodeName().equalsIgnoreCase("#document")) {
                    Node childAt = getChildAt(document, 0);
                    String stringFromXML = getStringFromXML(childAt, false);
                    document = getDoc(stringFromXML);
                }
            }
        } catch (Exception e) {
            LogMaster.log(LogMaster.DATA_DEBUG,
             "failed to parse xml: " + myString);
            main.system.ExceptionMaster.printStackTrace(e);
        }

        return document;
    }

    public static String getXmlFromNode(DefaultMutableTreeNode node)
     throws java.lang.ClassCastException {
        String nodeName;
        boolean primitive = false;
        if ((node.getUserObject() instanceof AE_Item)) {
            nodeName = ((AE_Item) node.getUserObject()).getName();
            AE_Item item = (AE_Item) node.getUserObject();
            if (item == null) {
                return "";
            }
            if (item.isENUM()) {
                return wrapLeaf(((AE_Item) node.getUserObject()).getName(), getEnumNodeValue(node));
            }
            primitive = item.isPrimitive();
        } else {
            nodeName = node.getUserObject().toString();
            primitive = true;
        }

        if (primitive) {
            return wrapLeaf(nodeName, getPrimitiveNodeValue(
//                    (item).getArg()
             nodeName
             , node));
        }
        String xml = openXmlFormatted(nodeName);
        if (!node.isLeaf()) {
            for (DefaultMutableTreeNode child : TreeMaster.getChildren(node)) {
                xml += getXmlFromNode(child);
            }
        }

        xml += closeXmlFormatted(nodeName);
        return xml;

    }

    private static String getEnumNodeValue(DefaultMutableTreeNode node) {
        if (node.isLeaf()) {
            return "";
        }
        TreeNode child = node.getFirstChild();
        Object object = ((DefaultMutableTreeNode) child).getUserObject();
        if (object == null) {
            return "";
        }
        return object.toString();
    }

    private static String getPrimitiveNodeValue(String argumentName, DefaultMutableTreeNode node) {
        if (node.isLeaf()) {
            return "";
        }
        TreeNode child = node.getFirstChild();
        Object object = ((DefaultMutableTreeNode) child).getUserObject();
        return object.toString().replace(argumentName, "");
    }

    private static String getStringFromXMLNode(Node node) {
        if (node.getNodeName().contains("#text"))

        {
            return node.getTextContent();
        }
        String string = openXmlFormatted(node.getNodeName());
        if (node.hasChildNodes()) {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                String childstring = getStringFromXMLNode(child);
                string += childstring;
            }
        } else {
            string += node.getTextContent();
        }

        string += closeXmlFormatted(node.getNodeName());
        return string;
    }

    public static String getXMLfromTree(JTree tree) throws java.lang.ClassCastException {

        String xml = getXmlFromNode((DefaultMutableTreeNode) tree.getModel().getRoot());
        // xml = layerDown(xml); clean documents now!
        return xml;
    }

    public static String getTextStringFromXML(Node child) {
        return XML_Formatter.restoreXmlNodeText(child.getTextContent());
    }

    public static String toString(Node child) {
        return getStringFromXML(child);
    }

    public static String getStringFromXML(Node child) {
        return getStringFromXML(child, true);
    }

    public static String getStringFromXML(Node child, boolean layerDown) {
        String xml = getStringFromXMLNode(child);
        if (layerDown) {
            xml = layerDown(xml);
        }
        return xml;
    }

    private static String layerDown(String xml) {
        int endIndex = xml.indexOf('>');
        int beginIndex = xml.indexOf('<');
        String substring = xml.substring(beginIndex + 1, endIndex);
        xml = xml.replaceFirst(openXmlFormatted(substring), "");
        xml = xml.replaceFirst(closeXmlFormatted(substring), "");
        return xml;
    }

    public static String openXml(String s) {
        return "<" + (s) + ">";
    }

    public static String closeXml(String s) {
        return "</" + (s) + ">";
    }

    public static String openXmlFormatted(String s) {
        return "<" + XML_Formatter.formatStringForXmlNodeName(s) + ">";
    }

    public static String closeXmlFormatted(String s) {
        return "</" + XML_Formatter.formatStringForXmlNodeName(s) + ">";
    }

    public static String wrapLeaf(String valName, String value) {
        return ("<" + XML_Formatter.formatStringForXmlNodeName(valName) + ">" + value + "</"
         + XML_Formatter.formatStringForXmlNodeName(valName) + ">\n");
    }

    public static String getXmlNodeName(VALUE val) {
        return val.getName().replace(" ", "_");
    }

    public static Node getAbilitiesDoc(Node node) {
        List<Node> nodeList = XML_Converter.getNodeList(node);
        if (nodeList.size() < 1) {
            return node;
        }
        while (nodeList.size() < 2 && nodeList.get(0).getNodeName().equals(Mapper.ABILITIES)) {

            Node child = node.getFirstChild();
            nodeList = XML_Converter.getNodeList(child);
            if (nodeList.size() < 1) {
                return node;
            }

            if (child.getNodeName().equals(Mapper.ABILITIES)) {
                node = child;
            }
        }
        return node;
    }

    public static Node getAbilitiesDoc(String property) {
        return getAbilitiesDoc(XML_Converter.getDoc(property));

    }

    public static List<ObjType> getTypeListFromXML(String xml, boolean layerDown) {
        if (layerDown) {
            xml = layerDown(xml);
        }
        Document doc = getDoc(xml);
        List<ObjType> list = new ArrayList<>();
        List<Node> typeGroupsList = null;
        for (Node n : getNodeList(doc)) {
            if (n.getNodeName().equalsIgnoreCase(TYPES_NODE)) {
                typeGroupsList = getNodeList(n);
            }
        }
        if (typeGroupsList == null) {
            return new ArrayList<>();
        }
        for (Node groupNode : typeGroupsList) {
            DC_TYPE obj_type = DC_TYPE.getType(groupNode.getNodeName());
            for (Node typeNode : getNodeList(groupNode)) {
                ObjType type = DataManager.getType(typeNode.getNodeName(), obj_type);
                if (type != null) // TODO find?
                {
                    list.add(type);
                }
                // TODO each type node could have some spec data for workspace?
            }
        }

        return list;
    }

    public static Node findNode(String xml, String nodeName) {
        //TODO recursive
        Document node = getDoc(xml);

        for (Node sub : getNodeList(node)) {
            if (sub.getNodeName().equalsIgnoreCase(nodeName)) {
                return sub;
            }
        }
        for (Node sub : getNodeListFromFirstChild(node, true)) {
            Node found = findNode(getStringFromXML(sub, false), nodeName);
            if (found != null)
                return found;
        }

        return null;
    }

    private static Node findNode(List<Node> nodes, String nodeName) {
        for (Node sub : nodes) {
            if (sub.getNodeName().equalsIgnoreCase(nodeName)) {
                return sub;
            }
        }

        for (Node node : nodes) {
            Node found = findNode(getNodeListFromFirstChild(node, true), nodeName);
            if (found != null)
                return found;
        }
        return null;
    }

    public static Document findAndBuildNode(String xmlString, String string) {
        int firstIndexOf = xmlString.indexOf(openXml(string));
        int lastIndexOf = xmlString.lastIndexOf(closeXml(string));
        String nodeContent = xmlString.substring(firstIndexOf, lastIndexOf);
        Document node = getDoc(
         // openXML(string) + already there?
         nodeContent + closeXml(string));
        return node;
    }

    public static String getXMLFromTypeList(List<ObjType> typeList) {
        Map<OBJ_TYPE, String> subStringMap = new XMap<>();
        String xml = "";

        for (ObjType type : typeList) {
            String typeString = subStringMap.get(type.getOBJ_TYPE_ENUM());
            if (typeString == null) {
                typeString = "";
            }
            typeString += wrap(XML_Formatter.formatStringForXmlNodeName(type.getName()), "");
            subStringMap.put(type.getOBJ_TYPE_ENUM(), typeString);
        }

        for (OBJ_TYPE type : subStringMap.keySet()) {
            xml += wrap(type.getName(), subStringMap.get(type));
        }

        xml = wrap(TYPES_NODE, xml);
        return xml;
    }

    public static String wrap(String enclosing, String node) {
        return openXmlFormatted(enclosing) + node + closeXmlFormatted(enclosing);
    }

    public static Node getChildByName(Node parent, String name) {
        return getNodeByName(getNodeList(parent), name);
    }

    public static Node getNodeByName(List<Node> list, String name) {
        for (Node node : (list)) {
            if (StringMaster.compare(node.getNodeName(), name)) { //node.getNodeName().equalsIgnoreCase(name))
                return node;
            }
        }


        return null;
    }

    public static DocumentBuilder getBuilder() {
        if (builder == null)
            try {
                builder = builderFactory.newDocumentBuilder();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        return builder;
    }

}
