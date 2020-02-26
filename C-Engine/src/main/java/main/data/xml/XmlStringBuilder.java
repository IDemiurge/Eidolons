package main.data.xml;

public class XmlStringBuilder {
    StringBuilder builder = new StringBuilder();


    public void close(String nodeName) {
        builder.append(XML_Converter.closeXml(nodeName));
    }

    public void open(String nodeName) {
        builder.append(XML_Converter.openXml(nodeName));
    }

    public void appendNode(String contents, String nodeName) {
        builder.append(XML_Converter.wrap(nodeName, contents));
    }

    public void append(String s) {
        builder.append(s);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
