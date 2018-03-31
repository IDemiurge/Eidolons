package main.data.ability.construct;

import main.data.ability.AE_Item;
import main.data.ability.Mapper;
import main.data.xml.XML_Converter;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class ConstructionManager {
    public static Object construct(Node node) {

        Construct construct = getConstruct(node);
        return construct(construct);

    }

    public static Object construct(Construct construct) {
        Object result = construct.construct();
        if (result instanceof Reconstructable) {
            ((Reconstructable) result).setConstruct(construct);
        }
        if (result == null) {
            return construct.construct();
        }
        return result;
    }

    private static Construct getConstruct(Node node) {
        AE_Item item = Mapper.getItem(node);
        if (item.isPrimitive()) {
            return new Construct(node.getNodeName(), node.getTextContent());
        }

        if (item.isENUM()) {
            return new Construct(node.getNodeName(), node.getTextContent(),
             true);
        }
        String xml = XML_Converter.getStringFromXML(node, false);
        return new Construct(node.getNodeName(), getConstructs(node), xml);
    }

    public static String getXmlFromObject(Object obj) { //for restoring constructor xml
        String xml = "" + obj;
        if (obj.getClass().isEnum()) {
// StringMaster.getWellFormattedString(string
        } else {
            //primitive

        }
        // what else? effects, other constructible/convertable...
        return xml;
    }

    public static String getXmlFromConstructorData(String name, Pair<Class, String>[] pairs) {
        String xml = "";
        for (Pair<Class, String> sub : pairs) {
            //TODO find arg for that class?
            // do  xml node and classname match?
            xml += XML_Converter.wrap(Mapper.getArgName(sub.getKey()), sub.getValue());
        }
        return XML_Converter.wrap(name, xml);
    }

    private static List<Construct> getConstructs(Node node) {
        List<Construct> list = new ArrayList<>();
        for (Node NODE : XML_Converter.getNodeList(node)) {
            Construct construct = getConstruct(NODE);
            list.add(construct);
        }
        return list;
    }

}
