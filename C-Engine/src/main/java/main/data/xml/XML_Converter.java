package main.data.xml;

import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.VALUE;
import main.data.DataManager;
import main.data.ability.AE_Item;
import main.data.ability.Argument;
import main.data.ability.Mapper;
import main.entity.type.ObjType;
import main.system.auxiliary.log.Err;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class XML_Converter {
    public static final String TEXT_NODE = "#text";
    final static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private static final String TYPES_NODE = "Types";
    private static final String DOCUMENT_ROOT = null;
    public static Pattern p = Pattern.compile("\n");

    // public Document reformDocument(Collection<String> newGroups)
    // {

    public static Document getGroupDoc(String sub, String type) {
        Document doc = XML_Reader.getDocForGroup(type);
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
                e.printStackTrace();
                Err.info(sub + type + nl.getLength());
            }
        }
        if (groupDoc == null) {
            Err.info(check + sub + type + nl.getLength() + node.getNodeName());
        }
        return groupDoc;

    }

    public static List<Node> getNodeList(Node node) {
        List<Node> list = new LinkedList<>();
        if (node == null) {
            return list;
        }
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {

            Node item = nl.item(i);
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

    public static List<Node> getNodeListFromFirstChild(Node node) {
        List<Node> list = new LinkedList<>();
        NodeList nl = node.getFirstChild().getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(nl.item(i));

        }
        return list;

    }

    public static List getConvertedDoc(Node node) {
        List list = new LinkedList();

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
            e.printStackTrace();
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
        DocumentBuilder builder;
        Document document = null;
        try {
            builder = builderFactory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(myString)));

        } catch (Exception e) {
            LogMaster.log(2,

                    "failed to parse xml: " + myString);
            e.printStackTrace();
        }
        if (document == null) {
            document = null;
        }
        if (removeDocumentRootNode) {
            if (document.getNodeName().equalsIgnoreCase("#document")) {
                Node childAt = getChildAt(document, 0);
                String stringFromXML = getStringFromXML(childAt, false);
                Document doc = getDoc(stringFromXML);
                return doc;
            }
        }
        return document;
    }

    public static String getXmlFromNode(DefaultMutableTreeNode node)
            throws java.lang.ClassCastException {
        String nodeName;
        if (!(node.getUserObject() instanceof AE_Item)) {
            return "";
        }
        nodeName = ((AE_Item) node.getUserObject()).getName();
        AE_Item item = (AE_Item) node.getUserObject();
        if (item == null) {
            return "";
        }
        if (item.isENUM()) {
            return wrapLeaf(((AE_Item) node.getUserObject()).getName(), getEnumNodeValue(node));
        }
        if (item.isPrimitive()) {
            return wrapLeaf(((AE_Item) node.getUserObject()).getName(), getPrimitiveNodeValue(
                    (item).getArg(), node));
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

    private static String getPrimitiveNodeValue(Argument argument, DefaultMutableTreeNode node) {
        if (node.isLeaf()) {
            return "";
        }
        TreeNode child = node.getFirstChild();
        Object object = ((DefaultMutableTreeNode) child).getUserObject();
        return object.toString().replace(argument.name(), "");
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
        return XML_Writer.restoreXmlNodeText(child.getTextContent());
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
        return "<" + XML_Writer.formatStringForXmlNodeName(s) + ">";
    }

    public static String closeXmlFormatted(String s) {
        return "</" + XML_Writer.formatStringForXmlNodeName(s) + ">";
    }

    public static String wrapLeaf(String valName, String value) {
        return ("<" + XML_Writer.formatStringForXmlNodeName(valName) + ">" + value + "</"
                + XML_Writer.formatStringForXmlNodeName(valName) + ">\n");
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
        List<ObjType> list = new LinkedList<>();
        List<Node> typeGroupsList = null;
        for (Node n : getNodeList(doc)) {
            if (n.getNodeName().equalsIgnoreCase(TYPES_NODE)) {
                typeGroupsList = getNodeList(n);
            }
        }
        if (typeGroupsList == null) {
            return new LinkedList<>();
        }
        for (Node groupNode : typeGroupsList) {
            OBJ_TYPES obj_type = OBJ_TYPES.getType(groupNode.getNodeName());
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

    public static Node findNode(String string) {
        // TODO Auto-generated method stub
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
            typeString += wrap(XML_Writer.formatStringForXmlNodeName(type.getName()), "");
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
        for (Node node : getNodeList(parent)) {
            if (node.getNodeName().equals(name)) {
                return node;
            }
        }

        return null;
    }

}
