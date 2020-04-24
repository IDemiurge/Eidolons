package main.data.xml;

import main.system.auxiliary.StringMaster;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class XmlNodeMaster {

    public static List<Node> getNodeList(Node node) {
        return getNodeList(node, true);
    }

    //    public static List<Node> getNodeList(Node node, boolean ignoreTextNodes, boolean recursive) {
//         List<Object> list = new ArrayList<>();
//        if (recursive) {
//            getNodeList(node).stream().
//        }
//    }
    public static List<Node> getNodeList(Node node, boolean ignoreTextNodes) {
        List<Node> list = new ArrayList<>();
        if (node == null) {
            return list;
        }
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {

            Node item = nl.item(i);
            if (ignoreTextNodes)
                if (XML_Converter.isTextNode(item)) {
                    continue;
                }
            list.add(item);

        }
        return list;

    }

    public static List<Node> getNodeListFromFirstChild(Node node, boolean ignoreTextNodes) {
        return getNodeList(getNodeList(node, true).get(0), ignoreTextNodes);
    }

    public static String findNodeText(String xml, String nodeName) {
        return XML_Converter.wrap(nodeName, findNode(xml, nodeName).getTextContent());
    }

    public static Node findNode(String xml, String nodeName) {

        Document document = XML_Converter.getDoc(xml);
        return findNode(document, nodeName);
    }

    public static Node findNode(Document document, String nodeName) {
        //TODO recursive

        for (Node sub : getNodeList(document)) {
            if (sub.getNodeName().equalsIgnoreCase(nodeName)) {
                return sub;
            }
        }
        for (Node sub : getNodeListFromFirstChild(document, true)) {
            Node found = findNode(XML_Converter.getStringFromXML(sub, false), nodeName);
            if (found != null)
                return found;
        }

        return null;
    }

    public static Node find(Node parent, String nodeName) {
        return findNode(getNodeList(parent), nodeName);
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
        int firstIndexOf = xmlString.indexOf(XML_Converter.openXml(string));
        int lastIndexOf = xmlString.lastIndexOf(XML_Converter.closeXml(string));
        String nodeContent = xmlString.substring(firstIndexOf, lastIndexOf);
        Document node = XML_Converter.getDoc(
                // openXML(string) + already there?
                nodeContent + XML_Converter.closeXml(string));
        return node;
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

}
